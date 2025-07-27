package com.jakdang.labs.api.taekjun.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponDTO {
    private Integer couponIndex;
    private String couponName;
    private Integer couponPrice;
    private String storeName;
    private String couponLimitTime;
    private String couponProvidedStatus;
    private LocalDateTime couponIssuanceTime;
} 