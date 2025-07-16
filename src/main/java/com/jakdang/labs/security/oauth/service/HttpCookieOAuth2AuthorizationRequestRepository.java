//package com.jakdang.labs.security.oauth.service;
//
//import com.jakdang.labs.utils.CookieUtils;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
//import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
//import org.springframework.stereotype.Component;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.util.Optional;
//
///**
// * OAuth2 인증 요청 쿠키 저장소 클래스 (주석 처리됨)
// * OAuth2 인증 요청을 쿠키에 저장하고 관리하는 클래스
// * 현재 주석 처리되어 사용되지 않음
// */
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
//
//    private final CookieUtils cookieUtils;
//
//    /** OAuth2 인증 요청 쿠키 이름 */
//    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
//    
//    /** 쿠키 만료 시간 (초) */
//    private static final int COOKIE_EXPIRE_SECONDS = 180;
//
//    /**
//     * 인증 요청 로드
//     * 쿠키에서 OAuth2 인증 요청을 로드
//     * 
//     * @param request HTTP 요청
//     * @return OAuth2 인증 요청 (없으면 null)
//     */
//    @Override
//    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
//        try {
//            Optional<OAuth2AuthorizationRequest> requestOptional = CookieUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
//                    .map(cookie -> CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest.class));
//
//            return requestOptional.orElse(null);
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    /**
//     * 인증 요청 저장
//     * OAuth2 인증 요청을 쿠키에 저장
//     * 
//     * @param authorizationRequest OAuth2 인증 요청
//     * @param request HTTP 요청
//     * @param response HTTP 응답
//     */
//    @Override
//    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
//        if (authorizationRequest == null) {
//            CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
//            return;
//        }
//
//        try {
//            CookieUtils.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
//                    CookieUtils.serialize(authorizationRequest), COOKIE_EXPIRE_SECONDS);
//        } catch (Exception e) {
//        }
//    }
//
//    /**
//     * 인증 요청 제거
//     * 쿠키에서 OAuth2 인증 요청을 제거
//     * 
//     * @param request HTTP 요청
//     * @param response HTTP 응답
//     * @return 제거된 OAuth2 인증 요청
//     */
//    @Override
//    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
//        try {
//            OAuth2AuthorizationRequest authRequest = this.loadAuthorizationRequest(request);
//            if (authRequest != null) {
//                CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
//                return authRequest;
//            } else {
//                return null;
//            }
//        } catch (Exception e) {
//            log.error("인증 요청 쿠키 삭제 중 오류: {}", e.getMessage(), e);
//            CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
//            return null;
//        }
//    }
//}