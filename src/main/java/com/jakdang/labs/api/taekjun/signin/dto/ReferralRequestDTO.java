package com.jakdang.labs.api.taekjun.signin.dto;

import lombok.Data;

@Data
public class ReferralRequestDTO {
    private String referralCode;  // 추천인 코드
    private Integer userIndex;    // 추천받는 사용자 인덱스
} 