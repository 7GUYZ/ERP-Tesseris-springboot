package com.jakdang.labs.api.taekjun.signin.dto;

import lombok.Data;

@Data
public class Step2PhoneAuthDTO {
    private String phone; // 휴대폰 번호
    private String authCode; // 인증 코드
    private String impUid; // 아임포트 UID
} 