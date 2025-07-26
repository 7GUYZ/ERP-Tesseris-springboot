package com.jakdang.labs.api.taekjun.businessmanlist.dto;

import lombok.Data;

@Data
public class BusinessmanListSearchDTO {
    private String email;
    private String userName;
    private String userPhone;
    private Integer businessGradeIndex;
    private Integer businessGradeLevel;
    private String businessGradeName;
    private String bossEmail;
    private Integer businessAreaIndex;
    private String businessAreaId;
    private String businessAreaPid;
    private String businessAreaName;
    private Integer businessAreaLevel;
    private String businessManDistributionFlag;
} 