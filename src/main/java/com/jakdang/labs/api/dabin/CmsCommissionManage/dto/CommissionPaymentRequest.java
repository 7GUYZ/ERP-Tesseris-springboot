package com.jakdang.labs.api.dabin.CmsCommissionManage.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommissionPaymentRequest {
    private String userId;
    private String userName;
    private String userPhone;
    private String storeName;
    private String chargeTimeStart;
    private String chargeTimeEnd;
    private String startDate;  // 추가된 필드
    private String endDate;    // 추가된 필드
    private String transactionName;
    private String suggestionUserId;
    private String suggestionUserName;
    private Integer userRoleIndex;
    private String paymentStatus;
    private String description;
} 