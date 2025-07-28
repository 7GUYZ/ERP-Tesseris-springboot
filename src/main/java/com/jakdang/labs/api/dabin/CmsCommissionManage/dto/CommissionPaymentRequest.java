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
    private String chargeTimeStart;
    private String chargeTimeEnd;
    private String transactionName;
    private String suggestionUserId;
    private String suggestionUserName;
    private Integer userRoleIndex;
} 