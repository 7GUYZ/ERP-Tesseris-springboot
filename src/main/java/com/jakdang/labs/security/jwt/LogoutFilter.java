package com.jakdang.labs.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.jakdang.labs.exceptions.JwtExceptionCode;
import com.jakdang.labs.security.jwt.service.LogoutService;
import com.jakdang.labs.security.jwt.utils.TokenUtils;
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
            String refreshToken = tokenUtils.extractRefreshToken(request.getCookies());
            logoutService.processLogout(refreshToken);

            Cookie logoutCookie = tokenUtils.createLogoutCookie();
            response.addCookie(logoutCookie);

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