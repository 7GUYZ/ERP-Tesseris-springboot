package com.jakdang.labs.api.dabin.CmsCommissionManage.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommissionPaymentResponse {
    private String userId;
    private Integer userIndex;
    private String userName;
    private String userPhone;
    private String transactionName;
    private LocalDateTime chargeTime;
    private Integer cmValue;
    private Integer cashValue;
    private Integer masterIndex;
    private Integer detailIndex;
    private Integer detailUserIndex;
    private Double regularCashValue;
    private String description;
    private String paymentStatus;
    private String advanceMsg;
    private String suggestionUserId;
    private String suggestionUserName;
    private String suggestionUserPhone;
    private String userRoleKorNm;
    private String userBankNumber;
    private String userBankName;
    private String userJumin;
    private String userBankHolder;
     } 