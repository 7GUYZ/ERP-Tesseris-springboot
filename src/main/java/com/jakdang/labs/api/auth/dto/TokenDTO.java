package com.jakdang.labs.api.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JWT 토큰 정보 DTO
 * 액세스 토큰과 리프레시 토큰을 담는 클래스
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDTO {

    /** 액세스 토큰 (API 인증용, 짧은 만료시간) */
    private String accessToken;
    
    /** 리프레시 토큰 (토큰 갱신용, 긴 만료시간) */
    private String refreshToken;
}
