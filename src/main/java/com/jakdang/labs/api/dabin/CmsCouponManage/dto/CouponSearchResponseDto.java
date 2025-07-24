package com.jakdang.labs.api.dabin.CmsCouponManage.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponSearchResponseDto {
    private String issuanceUserRole;
    private String issuanceUser;
    private String couponPrice;
    private Integer couponLimit;
    private String couponName;
    private String couponIssuanceStatus;
    private LocalDateTime couponIssuanceTime;
    private String providedUserRole;
    private String providedUser;
    private String couponProvidedStatus;
    private LocalDateTime couponProvidedTime;
    private LocalDateTime couponLimitTime;

    // JPQL DTO 생성자 추가
    public CouponSearchResponseDto(
        Integer couponIndex, 
        String couponName,
        Integer couponPrice,
        Integer couponLimit,
        String issuanceUser,
        String issuanceUserRole,
        String providedUser,
        String providedUserRole,
        String couponIssuanceStatus,
        String couponProvidedStatus,
        LocalDateTime couponIssuanceTime,
        LocalDateTime couponProvidedTime,
        LocalDateTime couponLimitTime
    ) {
        this.couponName = couponName;
        this.couponPrice = couponPrice != null ? couponPrice.toString() : null;
        this.couponLimit = couponLimit;
        this.issuanceUser = issuanceUser;
        this.issuanceUserRole = issuanceUserRole;
        this.providedUser = providedUser;
        this.providedUserRole = providedUserRole;
        this.couponIssuanceStatus = couponIssuanceStatus;
        this.couponProvidedStatus = couponProvidedStatus;
        this.couponIssuanceTime = couponIssuanceTime;
        this.couponProvidedTime = couponProvidedTime;
        this.couponLimitTime = couponLimitTime;
    }

} 