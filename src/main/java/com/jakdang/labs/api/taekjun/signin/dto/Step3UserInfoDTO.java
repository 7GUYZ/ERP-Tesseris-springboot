package com.jakdang.labs.api.taekjun.signin.dto;

import lombok.Data;

@Data
public class Step3UserInfoDTO {
    private String name; // 이름 (세션에서 가져옴)
    private String phone; // 휴대폰 번호
    private String pin; // 핀번호 (6자리)
    private String referralId; // 추천인 아이디 (이메일 또는 닉네임)
    private String email; // 이메일 (아이디로 사용)
    private String password; // 비밀번호
    private String nickname; // 닉네임
    
    // 주소 관련 필드 추가
    private String zoneCode; // 우편번호
    private String address; // 기본주소
    private String detailAddress; // 상세주소
    private String roadAddress; // 도로명주소
    private String jibunAddress; // 지번주소
    private String latitude; // 위도
    private String longitude; // 경도
} 