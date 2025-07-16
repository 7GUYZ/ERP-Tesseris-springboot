//package com.jakdang.labs.security.oauth;
//
//import java.util.Map;
//
///**
// * Naver OAuth2 사용자 정보 클래스 (주석 처리됨)
// * Naver OAuth2로부터 받은 사용자 정보를 파싱하는 클래스
// * 현재 주석 처리되어 사용되지 않음
// */
//public class NaverUserInfo implements OAuth2UserInfo {
//
//    /** OAuth2 사용자 속성 */
//    private Map<String, Object> attributes; //oauth2User.getAttributes()
//
//    /**
//     * NaverUserInfo 생성자
//     * 
//     * @param attributes OAuth2 사용자 속성
//     */
//    public NaverUserInfo(Map<String, Object> attributes) {
//        this.attributes = attributes;
//    }
//
//    /**
//     * Naver 제공자에서 제공하는 사용자 ID 반환
//     * 
//     * @return Naver 사용자 ID
//     */
//    @Override
//    public String getProviderId() {
//        return (String)attributes.get("id");
//    }
//
//    /**
//     * OAuth2 제공자 이름 반환
//     * 
//     * @return "naver"
//     */
//    @Override
//    public String getProvider() {
//       return "naver";
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
