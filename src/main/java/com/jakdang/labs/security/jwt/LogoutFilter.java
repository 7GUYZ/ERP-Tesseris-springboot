package com.jakdang.labs.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakdang.labs.api.jungeun.dto.LoginUserTesserisDTO;
import com.jakdang.labs.api.jungeun.dto.LoginoutCmsAccessLogDTO;
import com.jakdang.labs.api.jungeun.service.CmsAccessLogLjeSvc;
import com.jakdang.labs.api.jungeun.service.UserTesserisLjeSvc;
import com.jakdang.labs.exceptions.JwtExceptionCode;
import com.jakdang.labs.security.jwt.service.LogoutService;
import com.jakdang.labs.security.jwt.utils.JwtUtil;
import com.jakdang.labs.security.jwt.utils.TokenUtils;
import com.jakdang.labs.utils.jungeun.GetIpUtil;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import com.jakdang.labs.api.auth.entity.UserToken;
import com.jakdang.labs.api.auth.repository.UserTokenRepository;

/**
 * 로그아웃 처리 필터
 * 사용자 로그아웃 요청을 처리하고 토큰을 무효화하는 필터
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LogoutFilter extends OncePerRequestFilter {

    private final LogoutService logoutService;
    private final TokenUtils tokenUtils;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil; // 정은 추가 - 토큰 추출
    private final UserTesserisLjeSvc userSvc; // 정은 추가 - user_index를 얻기 위함
    private final CmsAccessLogLjeSvc cmsLogSvc; // 정은 추가 - cms_access_log 데이터 저장 위함
    private final UserTokenRepository userTokenRepository; // 정은 추가 - 토큰 조회를 위함

    /**
     * 필터 내부 처리 로직
     * 로그아웃 요청을 감지하고 처리
     * 
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param filterChain 필터 체인
     * @throws ServletException 서블릿 예외
     * @throws IOException IO 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (!isLogoutRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("로그아웃 요청 처리 시작");

        try {
            // 쿠키 디버깅 로그 추가
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                log.info("요청에 포함된 쿠키 개수: {}", cookies.length);
                for (Cookie cookie : cookies) {
                    log.info("쿠키 이름: {}, 값: {}", cookie.getName(), cookie.getValue());
                }
            } else {
                log.warn("요청에 쿠키가 없습니다");
            }

            // 1. 쿠키에서 토큰 추출 (기존 방식)
            String refreshToken = tokenUtils.extractRefreshToken(request.getCookies(), null);
            log.info("추출된 리프레시 토큰: {}", refreshToken != null ? "존재함" : "null");

            if (refreshToken != null) {
                try {
                    // 2. 데이터베이스에서 해당 토큰 조회
                    UserToken userToken = userTokenRepository.findByRefreshToken(refreshToken)
                            .orElse(null);
                    
                    if (userToken != null) {
                        log.info("데이터베이스에서 토큰 조회 성공 - userId: {}", userToken.getUserId());
                        
                        // 3. 사용자 정보 조회하여 user_role_index 확인
                        LoginUserTesserisDTO userDTO = userSvc.findByUsersId(userToken.getUserId());
                        
                        if (userDTO != null) {
                            Integer user_role_index = userDTO.getUserRoleIndex();
                            log.info("사용자 역할 인덱스: {}", user_role_index);
                            
                            // 4. 토큰 무효화
                            logoutService.processLogout(refreshToken);
                            
                            // 5. user_role_index에 따라 올바른 쿠키 삭제
                            if (user_role_index != null && user_role_index == 4) {
                                log.info("관리자 로그아웃 - adminRefresh 쿠키 삭제");
                                Cookie adminLogoutCookie = tokenUtils.createLogoutCookie("adminRefresh");
                                response.addCookie(adminLogoutCookie);
                            } else {
                                log.info("일반 사용자 로그아웃 - userRefresh 쿠키 삭제");
                                Cookie userLogoutCookie = tokenUtils.createLogoutCookie("userRefresh");
                                response.addCookie(userLogoutCookie);
                            }
                            
                            // cms_access_log 기록
                            try {
                                String clientIp = GetIpUtil.getClientIp(request);
                                LoginoutCmsAccessLogDTO logDTO = LoginoutCmsAccessLogDTO.builder()
                                        .cmsAccessLogUserIndex(userDTO.getUserIndex())
                                        .cmsAccessUserValue("로그아웃")
                                        .cmsAccessUserIp(clientIp)
                                        .build();
                                cmsLogSvc.saveLog(logDTO);
                            } catch (Exception e) {
                                log.warn("로그 저장 실패: {}", e.getMessage());
                            }
                        } else {
                            log.warn("사용자 정보를 찾을 수 없음 - userId: {}", userToken.getUserId());
                            // 토큰 무효화 후 모든 쿠키 삭제
                            logoutService.processLogout(refreshToken);
                            Cookie adminLogoutCookie = tokenUtils.createLogoutCookie("adminRefresh");
                            Cookie userLogoutCookie = tokenUtils.createLogoutCookie("userRefresh");
                            response.addCookie(adminLogoutCookie);
                            response.addCookie(userLogoutCookie);
                        }
                    } else {
                        log.warn("데이터베이스에서 토큰을 찾을 수 없음");
                        // 토큰이 DB에 없으면 모든 쿠키 삭제
                        Cookie adminLogoutCookie = tokenUtils.createLogoutCookie("adminRefresh");
                        Cookie userLogoutCookie = tokenUtils.createLogoutCookie("userRefresh");
                        response.addCookie(adminLogoutCookie);
                        response.addCookie(userLogoutCookie);
                    }
                } catch (Exception e) {
                    log.error("로그아웃 처리 중 오류 발생: {}", e.getMessage());
                    // 오류 발생 시 모든 쿠키 삭제
                    Cookie adminLogoutCookie = tokenUtils.createLogoutCookie("adminRefresh");
                    Cookie userLogoutCookie = tokenUtils.createLogoutCookie("userRefresh");
                    response.addCookie(adminLogoutCookie);
                    response.addCookie(userLogoutCookie);
                }
            } else {
                // 토큰이 null이면 두 토큰이 모두 존재하는 경우
                log.warn("두 토큰이 모두 존재하거나 토큰이 없음 - DB에서 확인");
                
                // 요청 헤더에서 User-Type 확인
                String userType = request.getHeader("User-Type");
                log.info("요청 헤더 User-Type: {}", userType);
                
                // 모든 쿠키에서 토큰을 찾아서 DB에서 확인
                Cookie[] allCookies = request.getCookies();
                UserToken adminToken = null;
                UserToken userToken = null;
                String adminTokenValue = null;
                String userTokenValue = null;
                
                if (allCookies != null) {
                    for (Cookie cookie : allCookies) {
                        if ("adminRefresh".equals(cookie.getName())) {
                            String tokenValue = cookie.getValue();
                            if (tokenValue != null) {
                                UserToken foundToken = userTokenRepository.findByRefreshToken(tokenValue).orElse(null);
                                if (foundToken != null) {
                                    adminToken = foundToken;
                                    adminTokenValue = tokenValue;
                                    log.info("adminRefresh 토큰 발견 - userId: {}", foundToken.getUserId());
                                }
                            }
                        } else if ("userRefresh".equals(cookie.getName())) {
                            String tokenValue = cookie.getValue();
                            if (tokenValue != null) {
                                UserToken foundToken = userTokenRepository.findByRefreshToken(tokenValue).orElse(null);
                                if (foundToken != null) {
                                    userToken = foundToken;
                                    userTokenValue = tokenValue;
                                    log.info("userRefresh 토큰 발견 - userId: {}", foundToken.getUserId());
                                }
                            }
                        }
                    }
                }
                
                // User-Type 헤더에 따라 해당 토큰 선택
                UserToken requestUserToken = null;
                String requestTokenValue = null;
                
                if ("admin".equals(userType) && adminToken != null) {
                    requestUserToken = adminToken;
                    requestTokenValue = adminTokenValue;
                    log.info("관리자 로그아웃 요청 - adminToken 선택 - userId: {}", requestUserToken.getUserId());
                } else if ("user".equals(userType) && userToken != null) {
                    requestUserToken = userToken;
                    requestTokenValue = userTokenValue;
                    log.info("사용자 로그아웃 요청 - userToken 선택 - userId: {}", requestUserToken.getUserId());
                } else {
                    log.warn("User-Type 헤더와 일치하는 토큰을 찾을 수 없음 - userType: {}, adminToken: {}, userToken: {}", 
                            userType, adminToken != null, userToken != null);
                }
                
                if (requestUserToken != null) {
                    log.info("요청 사용자 토큰 발견 - userId: {}", requestUserToken.getUserId());
                    
                    // 사용자 정보 조회
                    LoginUserTesserisDTO userDTO = userSvc.findByUsersId(requestUserToken.getUserId());
                    
                    if (userDTO != null) {
                        Integer user_role_index = userDTO.getUserRoleIndex();
                        log.info("사용자 역할 인덱스: {}", user_role_index);
                        
                        // 토큰 무효화
                        logoutService.processLogout(requestTokenValue);
                        
                        // 올바른 쿠키 삭제
                        if (user_role_index != null && user_role_index == 4) {
                            log.info("관리자 로그아웃 - adminRefresh 쿠키 삭제");
                            Cookie adminLogoutCookie = tokenUtils.createLogoutCookie("adminRefresh");
                            response.addCookie(adminLogoutCookie);
                        } else {
                            log.info("일반 사용자 로그아웃 - userRefresh 쿠키 삭제");
                            Cookie userLogoutCookie = tokenUtils.createLogoutCookie("userRefresh");
                            response.addCookie(userLogoutCookie);
                        }
                        
                        // cms_access_log 기록
                        try {
                            String clientIp = GetIpUtil.getClientIp(request);
                            LoginoutCmsAccessLogDTO logDTO = LoginoutCmsAccessLogDTO.builder()
                                    .cmsAccessLogUserIndex(userDTO.getUserIndex())
                                    .cmsAccessUserValue("로그아웃")
                                    .cmsAccessUserIp(clientIp)
                                    .build();
                            cmsLogSvc.saveLog(logDTO);
                        } catch (Exception e) {
                            log.warn("로그 저장 실패: {}", e.getMessage());
                        }
                        
                        sendSuccessResponse(response);
                        log.info("로그아웃 처리 완료");
                        return;
                    }
                }
                
                // 모든 토큰이 DB에 없거나 확인할 수 없는 경우 모든 쿠키 삭제
                log.warn("유효한 토큰을 찾을 수 없음 - 모든 쿠키 삭제");
                Cookie adminLogoutCookie = tokenUtils.createLogoutCookie("adminRefresh");
                Cookie userLogoutCookie = tokenUtils.createLogoutCookie("userRefresh");
                response.addCookie(adminLogoutCookie);
                response.addCookie(userLogoutCookie);
            }


            sendSuccessResponse(response);
            log.info("로그아웃 처리 완료");

        } catch (JwtException e) {
            log.error("로그아웃 실패: {}", e.getMessage());
            sendErrorResponse(response, e.getMessage());
        }
    }

    /**
     * 로그아웃 요청 여부 확인
     * 
     * @param request HTTP 요청
     * @return 로그아웃 요청 여부
     */
    private boolean isLogoutRequest(HttpServletRequest request) {
        return request.getRequestURI().equals("/api/auth/logout") && request.getMethod().equals("POST");
    }

    /**
     * 로그아웃 성공 응답 전송
     * 
     * @param response HTTP 응답
     * @throws IOException IO 예외
     */
    private void sendSuccessResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");

        objectMapper.writeValue(response.getOutputStream(),
                Map.of("status", "success", "message", "로그아웃 완료"));
    }

    /**
     * 로그아웃 실패 응답 전송
     * 
     * @param response HTTP 응답
     * @param errorMessage 에러 메시지
     * @throws IOException IO 예외
     */
    private void sendErrorResponse(HttpServletResponse response, String errorMessage) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");

        objectMapper.writeValue(response.getOutputStream(),
                Map.of("status", "error", "message", errorMessage));
    }
}