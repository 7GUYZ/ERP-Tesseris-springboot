package com.jakdang.labs.api.dabin.FrontEventCouponRegistration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
public class CouponForEventResponse {
    private Long couponIndex;
    private String couponName;
    private Integer couponPrice;
    private String couponIssuanceStatus;
    private LocalDateTime couponIssuanceTime;
    private Integer couponLimit;
    private LocalDateTime couponLimitTime;
    
    // JPQL에서 사용할 생성자
    public CouponForEventResponse(Long couponIndex, String couponName, Integer couponPrice, 
                                 String couponIssuanceStatus, LocalDateTime couponIssuanceTime, 
                                 Integer couponLimit, LocalDateTime couponLimitTime) {
        this.couponIndex = couponIndex;
        this.couponName = couponName;
        this.couponPrice = couponPrice;
        this.couponIssuanceStatus = couponIssuanceStatus;
        this.couponIssuanceTime = couponIssuanceTime;
        this.couponLimit = couponLimit;
        this.couponLimitTime = couponLimitTime;
    }
} 