package com.jakdang.labs.security.jwt.utils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

/**
 * 토큰 유틸리티 클래스
 * JWT 토큰의 추출, 검증, 쿠키 관리를 담당하는 유틸리티
 */
@Slf4j // (**0712 정은 추가 및 수정 코드)
@Component
@RequiredArgsConstructor
public class TokenUtils {

    private final JwtUtil jwtUtil;
    
    /** 프론트엔드 도메인 */
    @Value("${app.domain}")
    private String FRONT_DOMAIN;

    /** 개발 모드 여부 */
    @Value("${app.dev-mode}")
    private boolean IS_DEV_MODE;

    /**
     * 쿠키에서 리프레시 토큰 추출
     * 
     * @param cookies 쿠키 배열
     * @param user_role_index 사용자 역할 인덱스 (4: admin, 그 외: user, null: 모든 토큰 반환)
     * @return 리프레시 토큰 (없으면 null)
     */
    public String extractRefreshToken(Cookie[] cookies, Integer user_role_index) {
        if (cookies == null) {
            return null;
        }
        
        // user_role_index가 null이면 모든 토큰을 찾아서 반환 (DB에서 확인 필요)
        if (user_role_index == null) {
            // adminRefresh와 userRefresh 쿠키를 모두 찾아서 로그 출력
            String adminRefreshValue = null;
            String userRefreshValue = null;
            
            for (Cookie cookie : cookies) {
                if ("adminRefresh".equals(cookie.getName())) {
                    adminRefreshValue = cookie.getValue();
                    log.info("adminRefresh 쿠키 발견: {}", adminRefreshValue != null ? "값 있음" : "값 없음");
                } else if ("userRefresh".equals(cookie.getName())) {
                    userRefreshValue = cookie.getValue();
                    log.info("userRefresh 쿠키 발견: {}", userRefreshValue != null ? "값 있음" : "값 없음");
                }
            }
            
            // 두 토큰이 모두 있으면 DB에서 확인이 필요하므로 null 반환
            if (adminRefreshValue != null && userRefreshValue != null) {
                log.warn("adminRefresh와 userRefresh가 모두 존재 - DB에서 확인 필요");
                return null;
            }
            
            // 하나만 있으면 해당 토큰 반환
            if (adminRefreshValue != null) {
                log.info("adminRefresh 토큰만 존재 - 반환");
                return adminRefreshValue;
            } else if (userRefreshValue != null) {
                log.info("userRefresh 토큰만 존재 - 반환");
                return userRefreshValue;
            }
            
            log.warn("리프레시 토큰 쿠키를 찾을 수 없음");
            return null;
        }
        
        // user_role_index에 따라 올바른 쿠키에서 토큰 추출
        String targetCookieName = (user_role_index == 4) ? "adminRefresh" : "userRefresh";
        log.info("토큰 추출 대상 쿠키: {} (user_role_index: {})", targetCookieName, user_role_index);
        
        for (Cookie cookie : cookies) {
            if (targetCookieName.equals(cookie.getName())) {
                String tokenValue = cookie.getValue();
                if (tokenValue != null) {
                    log.info("{} 토큰 추출 성공", targetCookieName);
                    return tokenValue;
                } else {
                    log.warn("{} 쿠키는 있지만 값이 없음", targetCookieName);
                }
            }
        }
        
        log.warn("{} 쿠키를 찾을 수 없음", targetCookieName);
        return null;
    }

    /**
     * 리프레시 토큰 검증
     * 토큰의 존재 여부, 만료 여부, 카테고리를 검증
     * 
     * @param refreshToken 검증할 리프레시 토큰
     * @throws JwtException 토큰이 유효하지 않은 경우
     */
    public void validateRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw new JwtException("No Refresh Token");
        }

        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new JwtException("Expired Refresh Token");
        }

        String category = jwtUtil.getCategory(refreshToken);
        if (!"adminRefresh".equals(category) && !"userRefresh".equals(category)) {
            throw new JwtException("Invalid Token Category");
        }
    }

    /**
     * 액세스 토큰 유효성 검증
     * 
     * @param accessToken 검증할 액세스 토큰
     * @return 토큰 유효 여부
     * @throws ExpiredJwtException 토큰이 만료된 경우
     */
    public boolean isAccessTokenValid(String accessToken) throws ExpiredJwtException {
        return !jwtUtil.isExpired(accessToken) && "access".equals(jwtUtil.getCategory(accessToken));
    }

    /**
     * 로그아웃용 쿠키 생성
     * 리프레시 토큰을 무효화하기 위한 빈 쿠키를 생성
     * 
     * @param cookieName 쿠키 이름 (adminRefresh, userRefresh)
     * @return 로그아웃용 쿠키
     */
    public Cookie createLogoutCookie(String cookieName) {
        // 쿠키 이름이 null이면 기본값 "userRefresh" 사용
        String name = cookieName != null ? cookieName : "userRefresh";
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        
        // (**0712 정은 수정 및 추가 코드)
        // 개발 모드에서는 Secure를 false로 설정
        if (IS_DEV_MODE) {
            cookie.setSecure(false);
        } else {
            cookie.setSecure(true);
        }
        
        return cookie;
    }

    /**
     * 응답에 리프레시 토큰 쿠키 추가
     * 
     * @param user_role_index 사용자 역할 인덱스 (4: admin, 그 외: user)
     * @param response HTTP 응답
     * @param refreshToken 리프레시 토큰
     */
    public void addRefreshTokenCookie(Integer user_role_index, HttpServletResponse response, String refreshToken) {
        // (**0712 정은 추가 및 수정 코드)
        // response.addHeader(HttpHeaders.SET_COOKIE, createRefreshCookie(refreshToken).toString());
        ResponseCookie cookie = createRefreshCookie(user_role_index, refreshToken);
        log.info("리프레시 토큰 쿠키 생성: domain={}, secure={}, sameSite={}", 
                cookie.getDomain(), cookie.isSecure(), cookie.getSameSite());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    }

    /**
     * 리프레시 토큰 쿠키 생성
     * 보안을 위해 HttpOnly, Secure, SameSite 설정을 포함
     * 
     * @param user_role_index 사용자 역할 인덱스 (4: admin, 그 외: user)
     * @param value 쿠키 값 (리프레시 토큰)
     * @return ResponseCookie 객체
     */
    public ResponseCookie createRefreshCookie(Integer user_role_index, String value) {

        // 08001 정은 수정 및 추가 - user_role_index에 따라 쿠키이름 다르게 설정
        String cookieName = null;

        if(user_role_index == 4){
            cookieName = "adminRefresh";
        }else{
            cookieName = "userRefresh";
        }
        
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(cookieName, value)
                .maxAge(Duration.ofDays(1))
                .path("/")
                .httpOnly(true);

        // 개발 모드에서는 Secure를 false로 설정하고 SameSite를 Lax로 설정
        if (IS_DEV_MODE) {
            builder.secure(false)
                   .sameSite("Lax");
        } else {
            builder.secure(true)
                   .domain(FRONT_DOMAIN)
                   .sameSite("None");
        }

        return builder.build();
    }
}