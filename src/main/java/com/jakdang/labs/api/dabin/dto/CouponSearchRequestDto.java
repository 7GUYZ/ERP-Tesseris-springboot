package com.jakdang.labs.api.dabin.dto;


import java.time.LocalDateTime;

public class CouponSearchRequestDto {
    private LocalDateTime issuanceStart;
    private LocalDateTime issuanceEnd;
    private LocalDateTime providedStart;
    private LocalDateTime providedEnd;
    private LocalDateTime limitStart;
    private LocalDateTime limitEnd;
    private String issuanceUserId;
    private String providedUserId;
    private Integer issuanceStatusIndex;
    private Integer providedStatusIndex;
    private String couponName;
    private Integer couponPrice;

    // getter, setter
    public LocalDateTime getIssuanceStart() { return issuanceStart; }
    public void setIssuanceStart(LocalDateTime issuanceStart) { this.issuanceStart = issuanceStart; }
    public LocalDateTime getIssuanceEnd() { return issuanceEnd; }
    public void setIssuanceEnd(LocalDateTime issuanceEnd) { this.issuanceEnd = issuanceEnd; }
    public LocalDateTime getProvidedStart() { return providedStart; }
    public void setProvidedStart(LocalDateTime providedStart) { this.providedStart = providedStart; }
    public LocalDateTime getProvidedEnd() { return providedEnd; }
    public void setProvidedEnd(LocalDateTime providedEnd) { this.providedEnd = providedEnd; }
    public LocalDateTime getLimitStart() { return limitStart; }
    public void setLimitStart(LocalDateTime limitStart) { this.limitStart = limitStart; }
    public LocalDateTime getLimitEnd() { return limitEnd; }
    public void setLimitEnd(LocalDateTime limitEnd) { this.limitEnd = limitEnd; }
    public String getIssuanceUserId() { return issuanceUserId; }
    public void setIssuanceUserId(String issuanceUserId) { this.issuanceUserId = issuanceUserId; }
    public String getProvidedUserId() { return providedUserId; }
    public void setProvidedUserId(String providedUserId) { this.providedUserId = providedUserId; }
    public Integer getIssuanceStatusIndex() { return issuanceStatusIndex; }
    public void setIssuanceStatusIndex(Integer issuanceStatusIndex) { this.issuanceStatusIndex = issuanceStatusIndex; }
    public Integer getProvidedStatusIndex() { return providedStatusIndex; }
    public void setProvidedStatusIndex(Integer providedStatusIndex) { this.providedStatusIndex = providedStatusIndex; }
    public String getCouponName() { return couponName; }
    public void setCouponName(String couponName) { this.couponName = couponName; }
    public Integer getCouponPrice() { return couponPrice; }
    public void setCouponPrice(Integer couponPrice) { this.couponPrice = couponPrice; }
} 