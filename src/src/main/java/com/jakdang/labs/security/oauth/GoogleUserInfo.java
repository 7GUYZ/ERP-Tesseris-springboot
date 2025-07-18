//package com.jakdang.labs.security.oauth;
//
//import java.util.Map;
//
///**
// * Google OAuth2 사용자 정보 클래스 (주석 처리됨)
// * Google OAuth2로부터 받은 사용자 정보를 파싱하는 클래스
// * 현재 주석 처리되어 사용되지 않음
// */
//public class GoogleUserInfo implements OAuth2UserInfo {
//
//    /** OAuth2 사용자 속성 */
//    private Map<String, Object> attributes; //oauth2User.getAttributes()
//
//    /**
//     * GoogleUserInfo 생성자
//     * 
//     * @param attributes OAuth2 사용자 속성
//     */
//    public GoogleUserInfo(Map<String, Object> attributes) {
//        this.attributes = attributes;
//    }
//
//    /**
//     * Google 제공자에서 제공하는 사용자 ID 반환
//     * 
//     * @return Google 사용자 ID (sub)
//     */
//    @Override
//    public String getProviderId() {
//        return (String)attributes.get("sub");
//    }
//
//    /**
//     * OAuth2 제공자 이름 반환
//     * 
//     * @return "google"
//     */
//    @Override
//    public String getProvider() {
//       return "google";
//    }
//
//    /**
//     * 사용자 이메일 반환
//     * 
//     * @return 사용자 이메일
//     */
//    @Override
//    public String getEmail() {
//       return (String)attributes.get("email");
//    }
//
//    /**
//     * 사용자 이름 반환
//     * 
//     * @return 사용자 이름
//     */
//    @Override
//    public String getName() {
//       return (String)attributes.get("name");
//    }
//
//}
