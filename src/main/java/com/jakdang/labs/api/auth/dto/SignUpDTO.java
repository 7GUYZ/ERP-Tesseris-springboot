package com.jakdang.labs.api.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 회원가입 요청 DTO
 * 새로운 사용자 등록 시 필요한 정보를 담는 클래스
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpDTO {

    /** 사용자 이메일 (로그인 ID로 사용) */
    private String email;
    
    /** 사용자 비밀번호 */
    private String password;
    
    /** 사용자 이름 */
    private String name;
    
    /** 사용자 전화번호 */
    private String phone;
    
    /** 사용자 생년월일 */
    private String birth;
    
    /** 로그인 제공자 (기본값: local) */
    private String provider = "local";

}
