package com.jakdang.labs.api.dabin.CmsSalesPerformance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesPerformanceSearchRequestDto {
    private String businessUserId;
    private Integer businessGradeIndex;
    private String userName;
    private Integer businessManDistributionFlag;
    private String storeUserId;
    private String storeName;
    private Integer storeRequestStatusIndex;
    private Integer storeTransactionStatus;
} 