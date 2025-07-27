package com.jakdang.labs.api.dabin.FrontEventCouponList.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CouponDetailResponse {
    private String couponName;
    private Integer couponPrice;
    private Integer couponLimit;
    private LocalDateTime couponIssuanceTime;
    private String couponIssuanceStatus;
    private Long providedUserIndex;
    private String couponProvidedStatus;
    private LocalDateTime couponProvidedTime;
    private LocalDateTime couponLimitTime;
    private String userName;
} 