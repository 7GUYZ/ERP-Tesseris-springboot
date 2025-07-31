package com.jakdang.labs.api.dabin.UserEventCouponList.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEventDetailDto {
    private Integer storeIndex;
    private String storeName;
    private String storePhone;
    private String storeAddress;
    private String storeCategoryName;
    private String userCmUse;
    private String storeImage;
    private String storeBusinessState;
    private String storeTransactionStatus;
    
    // 쿠폰 정보 (여러 개 지원)
    private List<CouponInfo> coupons;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CouponInfo {
        private Long couponIndex;
        private String couponName;
        private Integer couponPrice;
        private String couponIssuanceStatus;
        private LocalDateTime couponIssuanceTime;
        private Integer couponLimit;
        private LocalDateTime couponLimitTime;
        private String storeName;
    }
} 