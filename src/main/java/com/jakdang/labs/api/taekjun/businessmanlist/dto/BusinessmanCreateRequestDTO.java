package com.jakdang.labs.api.taekjun.businessmanlist.dto;

import lombok.Data;

@Data
public class BusinessmanCreateRequestDTO {
    private String email;
    private String userName;
    private String userPw;
    private String userPhone;
    private String userBirthday;
    private Integer userGenderIndex;
    private String userZoneCode;
    private String userAddress;         // 전체 주소 (address_name)
    private String userDetailAddress;   // 상세주소 (직접입력)
    private String region1DepthName;    // 시/도
    private String region2DepthName;    // 구/군
    private String region3DepthName;    // 동/읍/면
    private Integer userBankIndex;
    private String userBankHolder;
    private String userBankNumber;
    private String bossEmail;
    private String businessManRegistrationDate;
    private Integer businessGradeIndex;
    private Integer businessAreaIndex;
    private String businessManDistributionFlag;
} 