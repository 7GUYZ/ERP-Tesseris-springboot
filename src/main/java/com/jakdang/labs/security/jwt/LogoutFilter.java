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

            String refreshToken = tokenUtils.extractRefreshToken(request.getCookies());
            log.info("추출된 리프레시 토큰: {}", refreshToken != null ? "존재함" : "null");

            logoutService.processLogout(refreshToken);
            
            Cookie logoutCookie = tokenUtils.createLogoutCookie();
            response.addCookie(logoutCookie);

            // cms_access_log 테이블에 로그아웃 기록 삽입하기
            // 1. 토큰에서 사용자 id 추출
            String id = jwtUtil.getUserId(refreshToken);
            // 2. 서비스 사용하여 user_index 추출
            LoginUserTesserisDTO userDTO = userSvc.findByUsersId(id);
            Integer user_index = userDTO.getUserIndex();
            // 3. Client ip 받아오기 - 만든 util 사용하기
            String clientIp = GetIpUtil.getClientIp(request);

            // 4. DTO에 담기
            LoginoutCmsAccessLogDTO logDTO = LoginoutCmsAccessLogDTO.builder()
                                .cmsAccessLogUserIndex(user_index)
                                .cmsAccessUserValue("로그아웃")
                                .cmsAccessUserIp(clientIp)
                                .build();
            // 5. 로그 저장 (로그인 실패로 이어지지 않게!)
            try {
                cmsLogSvc.saveLog(logDTO);
            } catch (Exception e) {
                log.warn("로그 저장 실패: {}", e.getMessage());
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