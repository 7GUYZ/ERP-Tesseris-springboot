package com.jakdang.labs.api.taekjun.businessmanlist.dto;

import lombok.Data;

@Data
public class BusinessmanListResponseDTO {
    private Integer userIndex;
    private String email;
    private String userName;
    private String userPhone;
    private String userBirthday;
    private Integer userGenderIndex;
    private String bossEmail;
    private String bossName;
    private String businessGradeName;
    private String businessManDistributionFlag;
    private String userBankName;
    private String userBankNumber;
    private String userBankHolder;
    private Integer userBankIndex;
    private String businessAreaName;
    private Integer businessAreaLevel;
    private Integer businessGradeIndex;
    private Integer businessAreaIndex;
    private String userZoneCode;
    private String userAddress;
    private String userDetailAddress;
} 