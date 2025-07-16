package com.jakdang.labs.api.auth.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * SNS 로그인 요청 DTO
 * 소셜 네트워크 서비스를 통한 로그인 요청 정보를 담는 클래스
 */
@Valid
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SnsSignInRequest {
    /** SNS 로그인 타입 (기본값: local) */
    private String type = "local";
    
    /** SNS 제공자로부터 받은 액세스 토큰 */
    private String accessToken;
    
    /** 사용자 로그인 정보 */
    private UserLoginRequest userInfo;
    
    /** 사용자 에이전트 정보 */
    
    private String userAgent;
}
