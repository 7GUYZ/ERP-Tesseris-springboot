package com.jakdang.labs.api.dabin.FrontEventCouponList.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDetailResponse {
    private Integer storeIndex;
    private String storeName;
    private String storePhone;
    private String storeAddress;
    private String storeCategoryName;
    private String userCmUse;
    private String storeImage;
    private String storeBusinessState;
    private String storeTransactionStatus;
    
    // 쿠폰 정보
    private Long couponIndex;
    private String couponName;
    private Integer couponPrice;
    private String couponIssuanceStatus;
    private LocalDateTime couponIssuanceTime;
    private Integer couponLimit;
    private LocalDateTime couponLimitTime;
} 