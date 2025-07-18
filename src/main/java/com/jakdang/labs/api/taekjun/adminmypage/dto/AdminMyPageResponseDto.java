package com.jakdang.labs.api.taekjun.adminmypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 어드민 마이페이지 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminMyPageResponseDto {
    
    // 기본 사용자 정보
    private String userId;
    private String userName;
    private String userBirthday;
    private String userGender;
    private String userPhone;
    
    // 계정 정보
    private String userEmail;
    private String userAddress;
    private String userDetailAddress;
    
    // 어드민 정보
    private String adminTypeName;
    private LocalDateTime adminRegistrationDate;
} 