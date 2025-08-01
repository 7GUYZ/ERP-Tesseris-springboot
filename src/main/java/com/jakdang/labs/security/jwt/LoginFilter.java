package com.jakdang.labs.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakdang.labs.api.auth.dto.CustomUserDetails;
import com.jakdang.labs.api.auth.dto.RoleType;
import com.jakdang.labs.api.auth.dto.TokenDTO;
import com.jakdang.labs.api.auth.dto.UserLoginRequest;
import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.jungeun.dto.LoginAdminDTO;
import com.jakdang.labs.api.jungeun.dto.LoginResponseDTO;
import com.jakdang.labs.api.jungeun.dto.LoginUserTesserisDTO;
import com.jakdang.labs.api.jungeun.dto.LoginoutCmsAccessLogDTO;
import com.jakdang.labs.api.jungeun.service.AdminLjeSvc;
import com.jakdang.labs.api.jungeun.service.CmsAccessLogLjeSvc;
import com.jakdang.labs.api.jungeun.service.UserTesserisLjeSvc;
import com.jakdang.labs.security.jwt.service.TokenService;
import com.jakdang.labs.security.jwt.utils.TokenUtils;
import com.jakdang.labs.utils.jungeun.GetIpUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 로그인 인증 필터
 * Spring Security의 UsernamePasswordAuthenticationFilter를 확장하여
 * 사용자 로그인 요청을 처리하고 JWT 토큰을 발급하는 필터
 */
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final TokenUtils tokenUtils;
    private final ObjectMapper objectMapper;
    private final UserTesserisLjeSvc userTesserisSvc;
    private final AdminLjeSvc adminLjeSvc;
    private final CmsAccessLogLjeSvc cmsLogSvc;

    /**
     * LoginFilter 생성자
     * 
     * @param authenticationManager 인증 관리자
     * @param tokenService          토큰 서비스
     * @param tokenUtils            토큰 유틸리티
     * @param objectMapper          JSON 매퍼
     */
    public LoginFilter(AuthenticationManager authenticationManager,
            TokenService tokenService,
            TokenUtils tokenUtils,
            ObjectMapper objectMapper,
            UserTesserisLjeSvc userTesserisSvc,
            AdminLjeSvc adminLjeSvc,
            CmsAccessLogLjeSvc cmsLogSvc) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.tokenUtils = tokenUtils;
        this.objectMapper = objectMapper;
        this.userTesserisSvc = userTesserisSvc;
        this.adminLjeSvc = adminLjeSvc;
        this.cmsLogSvc = cmsLogSvc;

        this.setFilterProcessesUrl("/api/auth/login");
    }

    /**
     * 인증 시도 처리
     * 요청에서 로그인 정보를 추출하고 인증을 시도
     * 재시도 메커니즘 포함
     * 
     * @param request  HTTP 요청
     * @param response HTTP 응답
     * @return 인증 결과
     * @throws AuthenticationException 인증 예외
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        int maxRetries = 3;
        int retryCount = 0;
        AuthenticationException lastException = null;

        while (retryCount < maxRetries) {
            try {
                UserLoginRequest loginRequest = objectMapper.readValue(
                        request.getInputStream(),
                        UserLoginRequest.class);

                log.info("로그인 시도 {}: {} {}", retryCount + 1, loginRequest.getUsername(), loginRequest.getPassword());

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword(),
                        null);

                return authenticationManager.authenticate(authToken);

            } catch (IOException e) {
                log.error("로그인 요청 파싱 실패: {}", e.getMessage());
                throw new AuthenticationException("로그인 요청을 처리할 수 없습니다.") {};
            } catch (AuthenticationException e) {
                lastException = e;
                retryCount++;
                
                if (retryCount < maxRetries) {
                    log.warn("로그인 시도 {} 실패, 재시도 중... ({}/{})", retryCount, retryCount, maxRetries);
                    try {
                        Thread.sleep(1000 * retryCount); // 점진적 대기 시간
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new AuthenticationException("로그인 처리 중 중단되었습니다.") {};
                    }
                } else {
                    log.error("로그인 시도 {}회 모두 실패", maxRetries);
                    throw lastException;
                }
            }
        }

        throw lastException != null ? lastException : 
            new AuthenticationException("로그인 처리 중 오류가 발생했습니다.") {};
    }

    /**
     * 인증 성공 처리
     * 인증이 성공했을 때 JWT 토큰을 생성하고 응답에 포함
     * 
     * @param request        HTTP 요청
     * @param response       HTTP 응답
     * @param chain          필터 체인
     * @param authentication 인증 객체
     * @throws IOException      IO 예외
     * @throws ServletException 서블릿 예외
     */
    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authentication) throws IOException, ServletException {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        if (!userDetails.isEnabled()) {
            log.warn("활성화되지 않은 사용자 {} 로그인 시도", userDetails.getUsername());
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            objectMapper.writeValue(response.getOutputStream(),
                    Map.of("error", "인증 실패", "message", "활성화되지 않은 계정입니다."));
            return;
        }

        String username = authentication.getName();
        String role = extractRole(authentication);
        String userId = extractUserId(authentication);
        String email = extractUserEmail(authentication);
        String phone = extractUserPhone(authentication);
        String createdAt = extractUserCreatedAt(authentication);

        log.info("사용자 {} {} {} {} 로그인 성공", username, role, email, userId);

        // 1. UserTesseris 정보 받기 - user_role_index를 위한
        LoginUserTesserisDTO userTesseris = userTesserisSvc.findByUsersId(userId);
        Integer user_role_index = userTesseris.getUserRoleIndex();
        // Admin일시 admin 테이블 가기 위한 작업
        Integer user_index = userTesseris.getUserIndex();

        // 2. (**0715 정은 추가) Admin일 경우 admin_type_index까지 받기
        Integer admin_type_index = null;
        String admin_type_name = null;
        LoginAdminDTO admin = adminLjeSvc.findByUserIndex(user_index);
        if (admin != null) {
            admin_type_index = admin.getAdminTypeIndex();
            admin_type_name = admin.getAdminTypeName();
        }

        // 토큰 생성 및 저장
        TokenDTO tokenPair = tokenService.createTokenPair(username, role, email, userId, user_role_index);

        // 응답 설정
        response.setStatus(HttpStatus.OK.value());
        response.setHeader("Authorization", "Bearer " + tokenPair.getAccessToken());

        log.info("로그인 성공 - 리프레시 토큰 쿠키 설정 시작");
        
        // user_role_index에 따라 해당하는 쿠키만 삭제
        if (user_role_index == 4) {
            // 관리자: adminRefresh만 삭제
            Cookie adminLogoutCookie = tokenUtils.createLogoutCookie("adminRefresh");
            response.addCookie(adminLogoutCookie);
        } else {
            // 일반 사용자: userRefresh만 삭제
            Cookie userLogoutCookie = tokenUtils.createLogoutCookie("userRefresh");
            response.addCookie(userLogoutCookie);
        }
        
        // 새로운 쿠키 설정
        tokenUtils.addRefreshTokenCookie(user_role_index, response, tokenPair.getRefreshToken()); // 0801 정은 수정 - user_role_index에 따라 쿠키이름 다르게 설정
        log.info("로그인 성공 - 리프레시 토큰 쿠키 설정 완료");

        // cms_access_log 테이블에 로그인 기록 삽입하기
        // 1. Client ip 받아오기 - 만든 util 사용하기
        String clientIp = GetIpUtil.getClientIp(request);

        // 2. DTO에 담기
        LoginoutCmsAccessLogDTO logDTO = LoginoutCmsAccessLogDTO.builder()
                .cmsAccessLogUserIndex(user_index)
                .cmsAccessUserValue("로그인")
                .cmsAccessUserIp(clientIp)
                .build();
        // 3. 로그 저장 (로그인 실패로 이어지지 않게!)
        try {
            cmsLogSvc.saveLog(logDTO);
        } catch (Exception e) {
            log.warn("로그 저장 실패: {}", e.getMessage());
        }

        // WebSocket 연결 정보를 응답에 포함(0726 정은 추가 - 알림WebSocket)
        Map<String, Object> websocketInfo = Map.of(
                "websocketUrl", "/ws/notifications",
                "subscribeTopics", List.of(
                        // "/topic/chat/" + userId, // 개인 채팅
                        "/topic/notifications/" + user_index //알림
                ),
                "userDestination", "/user/" + user_index + "/queue/messages");

        // LoginResponseDTO에 WebSocket 정보 추가(0726 정은 추가 - 알림WebSocket)
        LoginResponseDTO responseDTO = LoginResponseDTO.builder()
                .id(userId)
                .email(email)
                .name(username)
                .phone(phone)
                .createdAt(createdAt)
                .user_role_index(String.valueOf(user_role_index))
                .admin_type_index(admin_type_index == null ? "관리자회원X" : String.valueOf(admin_type_index))
                .admin_type_name(admin_type_name)
                .user_index(String.valueOf(user_index))
                .websocketInfo(websocketInfo)  // 이 부분 추가
                .build();

        // 로그인 성공 응답 작성 (**정은 수정함 0709**)
        // writeSuccessResponse(response, username, role);
        writeSuccessResponse(response, responseDTO);
    }

    /**
     * 인증 실패 처리
     * 인증이 실패했을 때 에러 응답을 반환
     * 
     * @param request   HTTP 요청
     * @param response  HTTP 응답
     * @param exception 인증 예외
     * @throws IOException      IO 예외
     * @throws ServletException 서블릿 예외
     */
    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        log.warn("로그인 실패: {}", exception.getMessage());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        objectMapper.writeValue(response.getOutputStream(),
                Map.of("error", "인증 실패", "message", "사용자 이름 또는 비밀번호가 잘못되었습니다"));
    }

    /**
     * 인증 객체에서 사용자 역할 추출
     * 
     * @param authentication 인증 객체
     * @return 사용자 역할
     */
    private String extractRole(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(RoleType.ROLE_USER.getRole());
    }

    /**
     * 인증 객체에서 사용자 ID 추출
     * 
     * @param authentication 인증 객체
     * @return 사용자 ID
     */
    private String extractUserId(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUserId();
    }

    /**
     * 인증 객체에서 사용자 이메일 추출
     * 
     * @param authentication 인증 객체
     * @return 사용자 이메일
     */
    private String extractUserEmail(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getEmail();
    }

    // 정은 추가 0710
    private String extractUserCreatedAt(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getCreatedAt();
    }

    // 정은 추가 0710
    private String extractUserPhone(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getPhone();
    }

    /**
     * 로그인 성공 응답 작성
     * 
     * @param response HTTP 응답
     * @param username 사용자명
     * @param role     사용자 역할
     * @throws IOException IO 예외
     */

    // private void writeSuccessResponse(HttpServletResponse response, String
    // username, String role) throws IOException {
    // response.setContentType("application/json");
    // objectMapper.writeValue(response.getOutputStream(),
    // ResponseDTO.createSuccessResponse("로그인 성공", null));
    // }
    // (**정은 수정 및 추가 0709**)
    private <T> void writeSuccessResponse(HttpServletResponse response, T responseDTO) throws IOException {
        response.setContentType("application/json");
        objectMapper.writeValue(response.getOutputStream(),
                ResponseDTO.createSuccessResponse("로그인 성공", responseDTO));
    }
}