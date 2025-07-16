//package com.jakdang.labs.security.oauth.handler;
//
//import com.jakdang.labs.api.auth.dto.RoleType;
//import com.jakdang.labs.api.auth.dto.TokenDTO;
//import com.jakdang.labs.security.jwt.service.TokenService;
//import com.jakdang.labs.security.oauth.dto.PrincipalDetails;
//import com.jakdang.labs.security.oauth.service.HttpCookieOAuth2AuthorizationRequestRepository;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.io.IOException;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//
///**
// * OAuth2 인증 성공 핸들러 클래스 (주석 처리됨)
// * OAuth2 로그인 성공 시 JWT 토큰을 생성하고 프론트엔드로 리다이렉트하는 핸들러
// * 현재 주석 처리되어 사용되지 않음
// */
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//
//    private final TokenService tokenService;
//    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;
//
//    /** 프론트엔드 URL */
//    @Value("${app.frontend-url}")
//    private String frontendUrl;
//    
//    /** 리다이렉트 URI */
//    private String REDIRECT_URI = frontendUrl + "/callback";
//
//    /**
//     * OAuth2 인증 성공 처리
//     * 인증이 성공했을 때 JWT 토큰을 생성하고 프론트엔드로 리다이렉트
//     * 
//     * @param request HTTP 요청
//     * @param response HTTP 응답
//     * @param authentication 인증 객체
//     * @throws IOException IO 예외
//     * @throws ServletException 서블릿 예외
//     */
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
//            throws IOException, ServletException {
//
//        log.info("OAuth2 인증 성공");
//
//        try {
//            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
//            String email = principalDetails.getUsername();
//            String userId = principalDetails.getId();
//
//            log.info("사용자 정보 - 이메일: {}, ID: {}", email, userId);
//
//            TokenDTO tokenDTO = tokenService.createTokenPair(principalDetails.getUsername(), principalDetails.getRole(), email, userId);
//
//            // 기존 코드 제거 및 수동 쿠키 삭제 추가
//            try {
//                Cookie[] cookies = request.getCookies();
//                if (cookies != null) {
//                    for (Cookie cookie : cookies) {
//                        if (cookie.getName().equals("oauth2_auth_request")) {
//                            cookie.setValue("");
//                            cookie.setPath("/");
//                            cookie.setMaxAge(0);
//                            response.addCookie(cookie);
//                            log.info("인증 요청 쿠키 수동 삭제 완료");
//                            break;
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                log.error("쿠키 수동 삭제 중 오류: {}", e.getMessage());
//                // 오류가 발생해도 인증 과정 계속 진행
//            }
//
//            String targetUrl = UriComponentsBuilder.fromUriString(REDIRECT_URI)
//                    .queryParam("accessToken", tokenDTO.getAccessToken())
//                    .queryParam("refreshToken", tokenDTO.getRefreshToken())
//                    .build().toUriString();
//
//            log.info("리다이렉트 URL: {}", targetUrl);
//            getRedirectStrategy().sendRedirect(request, response, targetUrl);
//
//        } catch (Exception e) {
//            log.error("OAuth2 인증 성공 처리 중 오류: {}", e.getMessage(), e);
//            // 오류 발생 시 로그인 페이지로 리다이렉트
//            String errorMessage = URLEncoder.encode("서버 오류: " + e.getMessage(), StandardCharsets.UTF_8);
//            getRedirectStrategy().sendRedirect(request, response, REDIRECT_URI + "?error=" + errorMessage);
//        }
//    }
//}