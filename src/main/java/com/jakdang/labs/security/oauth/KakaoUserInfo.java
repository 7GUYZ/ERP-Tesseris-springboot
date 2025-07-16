//package com.jakdang.labs.security.oauth;
//
//import java.util.Map;
//
///**
// * Kakao OAuth2 사용자 정보 클래스 (주석 처리됨)
// * Kakao OAuth2로부터 받은 사용자 정보를 파싱하는 클래스
// * 현재 주석 처리되어 사용되지 않음
// */
//public class KakaoUserInfo implements OAuth2UserInfo {
//
//    /** OAuth2 사용자 속성 */
//    private Map<String, Object> attributes; //oauth2User.getAttributes()
//
//    /**
//     * KakaoUserInfo 생성자
//     * 
//     * @param attributes OAuth2 사용자 속성
//     */
//    public KakaoUserInfo(Map<String, Object> attributes) {
//        this.attributes = attributes;
//    }
//
//    /**
//     * Kakao 제공자에서 제공하는 사용자 ID 반환
//     * 
//     * @return Kakao 사용자 ID
//     */
//    @Override
//    public String getProviderId() {
//        return (String)attributes.get("id").toString();
//    }
//
//    /**
//     * OAuth2 제공자 이름 반환
//     * 
//     * @return "kakao"
//     */
//    @Override
//    public String getProvider() {
//       return "kakao";
//    }
//
//    /**
//     * 사용자 이메일 반환
//     * Kakao 계정 정보에서 이메일을 추출
//     * 
//     * @return 사용자 이메일
//     */
//    @Override
//    public String getEmail() {
//        Map<String, Object> map = ((Map<String, Object>)attributes.get("kakao_account"));
//       String email = (String)map.get("email");
//       return email ;
//    }
//
//    /**
//     * 사용자 이름 반환
//     * Kakao 프로필 정보에서 닉네임을 추출
//     * 
//     * @return 사용자 이름 (닉네임)
//     */
//    @Override
//    public String getName() {
//       Map<String, Object> map = ((Map<String, Object>)attributes.get("properties"));
//       String name = (String)map.get("nickname");
//       return name ;
//    }
//
//}
