package com.jakdang.labs.api.dabin.dto;


import java.time.LocalDateTime;

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

    // getter, setter
    public String getIssuanceUserRole() { return issuanceUserRole; }
    public void setIssuanceUserRole(String issuanceUserRole) { this.issuanceUserRole = issuanceUserRole; }
    public String getIssuanceUser() { return issuanceUser; }
    public void setIssuanceUser(String issuanceUser) { this.issuanceUser = issuanceUser; }
    public String getCouponPrice() { return couponPrice; }
    public void setCouponPrice(String couponPrice) { this.couponPrice = couponPrice; }
    public Integer getCouponLimit() { return couponLimit; }
    public void setCouponLimit(Integer couponLimit) { this.couponLimit = couponLimit; }
    public String getCouponName() { return couponName; }
    public void setCouponName(String couponName) { this.couponName = couponName; }
    public String getCouponIssuanceStatus() { return couponIssuanceStatus; }
    public void setCouponIssuanceStatus(String couponIssuanceStatus) { this.couponIssuanceStatus = couponIssuanceStatus; }
    public LocalDateTime getCouponIssuanceTime() { return couponIssuanceTime; }
    public void setCouponIssuanceTime(LocalDateTime couponIssuanceTime) { this.couponIssuanceTime = couponIssuanceTime; }
    public String getProvidedUserRole() { return providedUserRole; }
    public void setProvidedUserRole(String providedUserRole) { this.providedUserRole = providedUserRole; }
    public String getProvidedUser() { return providedUser; }
    public void setProvidedUser(String providedUser) { this.providedUser = providedUser; }
    public String getCouponProvidedStatus() { return couponProvidedStatus; }
    public void setCouponProvidedStatus(String couponProvidedStatus) { this.couponProvidedStatus = couponProvidedStatus; }
    public LocalDateTime getCouponProvidedTime() { return couponProvidedTime; }
    public void setCouponProvidedTime(LocalDateTime couponProvidedTime) { this.couponProvidedTime = couponProvidedTime; }
    public LocalDateTime getCouponLimitTime() { return couponLimitTime; }
    public void setCouponLimitTime(LocalDateTime couponLimitTime) { this.couponLimitTime = couponLimitTime; }
} 