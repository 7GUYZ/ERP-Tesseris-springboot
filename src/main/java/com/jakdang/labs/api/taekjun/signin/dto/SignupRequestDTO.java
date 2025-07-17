package com.jakdang.labs.api.taekjun.signin.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class SignupRequestDTO {
    // Users 테이블용 필드
    private String email;
    private String password;
    private String name;
    private String nickname;
    private String phone;
    private String provider;
    private String referralCode;
    private String referralIdentifier; // 추천인 아이디 (이메일 또는 닉네임)
    private String role;  // role 필드 추가
    
    // User 테이블용 필드
    private LocalDate userBirthday;
    private Integer userGenderIndex;
    private Integer userRoleIndex;
    private String userZoneCode;
    private String userAddress;
    private String userDetailAddress;
    private String userJumin;
    private String signupPath;
} 