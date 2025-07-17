package com.jakdang.labs.api.taekjun.signin.dto;

import lombok.Data;

@Data
public class Step1AgreementDTO {
    private String allAgree; // 전체 동의
    private String serviceAgree; // 서비스 이용약관 동의
    private String privacyAgree; // 개인정보 수집 및 이용 동의
    private String marketingAgree; // 마케팅 정보 수집/이용 동의
    private String advertisementAgree; // 광고성 정보 수신 동의
    private String locationAgree; // 위치기반서비스 이용약관 동의
} 