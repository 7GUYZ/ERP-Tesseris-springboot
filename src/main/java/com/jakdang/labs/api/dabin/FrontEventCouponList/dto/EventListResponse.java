package com.jakdang.labs.api.dabin.FrontEventCouponList.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventListResponse {
    private Integer eventMasterIndex;
    private String eventMasterName;
    private String eventMasterCondition;
    private Long totalCouponPrice;
    private String storeAddress;
    private String storeName;
    private Integer eventMasterCount;
} 