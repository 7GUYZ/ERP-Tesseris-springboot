package com.jakdang.labs.api.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 로그인 요청 DTO
 * 사용자 로그인 시 필요한 사용자명과 비밀번호 정보를 담는 클래스
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginRequest {

    /** 사용자명 (이메일 또는 아이디) */
    private String username;
    
    /** 사용자 비밀번호 */
    private String password;
}
