package com.jakdang.labs.api.taekjun.passwordfind.dto;

import lombok.Data;

@Data
public class PasswordFindRequestDTO {
    private String email; // 이메일 주소
    private String name; // 사용자 이름
    private String authCode; // 인증 코드
    private String authToken; // 인증 토큰
    private String newPassword; // 새 비밀번호
} 