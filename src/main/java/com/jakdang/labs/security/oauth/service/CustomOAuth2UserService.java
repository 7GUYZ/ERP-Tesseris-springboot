//package com.jakdang.labs.security.oauth.service;
//
//import com.jakdang.labs.api.auth.entity.UserEntity;
//import com.jakdang.labs.api.auth.repository.AuthRepository;
//import com.jakdang.labs.security.oauth.*;
//import com.jakdang.labs.security.oauth.dto.PrincipalDetails;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//
//import java.util.Map;
//import java.util.Optional;
//
///**
// * OAuth2 사용자 서비스 클래스 (주석 처리됨)
// * OAuth2 로그인을 처리하고 사용자 정보를 관리하는 서비스
// * 현재 주석 처리되어 사용되지 않음
// */
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class CustomOAuth2UserService extends DefaultOAuth2UserService {
//
//    private final AuthRepository authRepository;
//
//    /**
//     * OAuth2 사용자 정보 로드
//     * OAuth2 제공자로부터 사용자 정보를 받아와서 처리
//     * 
//     * @param userRequest OAuth2 사용자 요청
//     * @return OAuth2 사용자 객체
//     * @throws OAuth2AuthenticationException OAuth2 인증 예외
//     */
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        OAuth2User oAuth2User = super.loadUser(userRequest);
//
//        String registrationId = userRequest.getClientRegistration().getRegistrationId();
//        OAuth2UserInfo oAuth2UserInfo = null;
//
//        if (registrationId.equals("kakao")) {
//            log.info("카카오 사용자 정보 처리");
//            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
//        } else if (registrationId.equals("naver")) {
//            log.info("네이버 사용자 정보 처리");
//            oAuth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));
//        } else if (registrationId.equals("google")) {
//            log.info("구글 사용자 정보 처리");
//            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
//        } else if (registrationId.equals("apple")) {
//            log.info("애플 사용자 정보 처리");
//            oAuth2UserInfo = new AppleUserInfo(oAuth2User.getAttributes());
//        } else {
//            log.warn("지원하지 않는 OAuth2 제공자: {}", registrationId);
//            throw new OAuth2AuthenticationException("지원하지 않는 OAuth2 제공자입니다.");
//        }
//
//        String providerId = oAuth2UserInfo.getProviderId();
//        String provider = oAuth2UserInfo.getProvider();
//        String email = oAuth2UserInfo.getEmail();
//        String name = oAuth2UserInfo.getName();
//        String username = provider + "_" + providerId;
//
//        Optional<UserEntity> optionalUser = authRepository.findByEmail(email);
//        UserEntity user;
//
//        if (optionalUser.isEmpty()) {
//            user = UserEntity.builder()
//                    .name(username)
//                    .email(email)
//                    .name(name)
//                    .provider(provider)
//                    .build();
//            authRepository.save(user);
//        } else {
//            user = optionalUser.get();
//        }
//
//        return new PrincipalDetails(user, oAuth2User.getAttributes());
//    }
//}