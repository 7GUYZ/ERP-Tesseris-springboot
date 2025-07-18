package com.jakdang.labs.api.taekjun.adminmypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 패스워드 변경 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequestDto {
    
    // 현재 패스워드 (검증용)
    private String currentPassword;
    
    // 새로운 패스워드
    private String newPassword;
    
    // 새로운 패스워드 확인
    private String confirmPassword;
} 