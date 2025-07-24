package com.jakdang.labs.api.taekjun.businessmanlist.dto;

import lombok.Data;

@Data
public class BusinessmanListResponseDTO {
    private Integer userIndex;
    private String email;
    private String userName;
    private String userPhone;
    private String bossEmail;
    private String businessGradeName;
    private String businessManDistributionFlag;
    private String userBankName;
    private String userBankNumber;
    private String userBankHolder;
    private String businessAreaName;
    private Integer businessAreaLevel;
    private Integer businessGradeIndex;
    private Integer businessAreaIndex;
} 