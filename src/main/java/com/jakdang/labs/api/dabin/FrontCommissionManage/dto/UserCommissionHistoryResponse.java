package com.jakdang.labs.api.dabin.FrontCommissionManage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCommissionHistoryResponse {
    private Integer rowNumber;
    private String userName;
    private String chargeDate;
    private String description;
    private Double commissionAmount;
    private String paymentStatus;
    

} 