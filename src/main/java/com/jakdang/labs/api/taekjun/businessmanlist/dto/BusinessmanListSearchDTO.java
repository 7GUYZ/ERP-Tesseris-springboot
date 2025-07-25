package com.jakdang.labs.api.taekjun.businessmanlist.dto;

import lombok.Data;

@Data
public class BusinessmanListSearchDTO {
    private String email;
    private String userName;
    private String userPhone;
    private Integer businessGradeIndex;
    private String bossEmail;
    private String businessAreaName;
    private String businessManDistributionFlag;
} 