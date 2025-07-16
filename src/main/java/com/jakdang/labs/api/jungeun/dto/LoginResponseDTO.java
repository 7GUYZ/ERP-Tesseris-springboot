package com.jakdang.labs.api.jungeun.dto;

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
public class LoginResponseDTO {

    private String id;
    private String email;
    private String name;
    private String phone;
    private String createdAt;
    private String user_role_index;
    private String admin_type_index;
    private String user_index;
}