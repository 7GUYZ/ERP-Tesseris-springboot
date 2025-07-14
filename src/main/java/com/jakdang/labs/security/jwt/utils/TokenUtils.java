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
     * @return 리프레시 토큰 (없으면 null)
     */
    public String extractRefreshToken(Cookie[] cookies) {
        return Optional.ofNullable(cookies)
                .flatMap(cookieArray -> Arrays.stream(cookieArray)
                        .filter(cookie -> "refresh".equals(cookie.getName()))
                        .map(Cookie::getValue)
                        .findFirst())
                .orElse(null);
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

        if (!"refresh".equals(jwtUtil.getCategory(refreshToken))) {
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
     * @return 로그아웃용 쿠키
     */
    public Cookie createLogoutCookie() {
        Cookie cookie = new Cookie("refresh", null);
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
     * @param response HTTP 응답
     * @param refreshToken 리프레시 토큰
     */
    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        // (**0712 정은 추가 및 수정 코드)
        // response.addHeader(HttpHeaders.SET_COOKIE, createRefreshCookie(refreshToken).toString());
        ResponseCookie cookie = createRefreshCookie(refreshToken);
        log.info("리프레시 토큰 쿠키 생성: domain={}, secure={}, sameSite={}", 
                cookie.getDomain(), cookie.isSecure(), cookie.getSameSite());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    }

    /**
     * 리프레시 토큰 쿠키 생성
     * 보안을 위해 HttpOnly, Secure, SameSite 설정을 포함
     * 
     * @param value 쿠키 값 (리프레시 토큰)
     * @return ResponseCookie 객체
     */
    public ResponseCookie createRefreshCookie(String value) {
        // (**0712 정은 수정 및 추가 코드)

        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from("refresh", value)
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