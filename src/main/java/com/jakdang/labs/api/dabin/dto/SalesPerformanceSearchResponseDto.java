package com.jakdang.labs.api.dabin.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SalesPerformanceSearchResponseDto {
    private String businessUserId;
    private String businessGradeName;
    private String businessAreaName;
    private String businessUserName;
    private String businessManDistributionFlag;
    private String storeUserId;
    private String storeName;
    private String storeRequestStatusName;
    private String storeTransactionStatus;
    private String cmrockStatus;
    private String sellrockStatus;
    private LocalDateTime storeRegistrationDate;

    // JPQL DTO 생성자
    public SalesPerformanceSearchResponseDto(
        String businessUserId,
        String businessGradeName,
        String businessAreaName,
        String businessUserName,
        String businessManDistributionFlag,
        String storeUserId,
        String storeName,
        String storeRequestStatusName,
        String storeTransactionStatus,
        String cmrockStatus,
        String sellrockStatus,
        LocalDateTime storeRegistrationDate
    ) {
        this.businessUserId = businessUserId;
        this.businessGradeName = businessGradeName;
        this.businessAreaName = businessAreaName;
        this.businessUserName = businessUserName;
        this.businessManDistributionFlag = businessManDistributionFlag;
        this.storeUserId = storeUserId;
        this.storeName = storeName;
        this.storeRequestStatusName = storeRequestStatusName;
        this.storeTransactionStatus = storeTransactionStatus;
        this.cmrockStatus = cmrockStatus;
        this.sellrockStatus = sellrockStatus;
        this.storeRegistrationDate = storeRegistrationDate;
    }

    // getter, setter
    public String getBusinessUserId() { return businessUserId; }
    public void setBusinessUserId(String businessUserId) { this.businessUserId = businessUserId; }
    public String getBusinessGradeName() { return businessGradeName; }
    public void setBusinessGradeName(String businessGradeName) { this.businessGradeName = businessGradeName; }
    public String getBusinessAreaName() { return businessAreaName; }
    public void setBusinessAreaName(String businessAreaName) { this.businessAreaName = businessAreaName; }
    public String getBusinessUserName() { return businessUserName; }
    public void setBusinessUserName(String businessUserName) { this.businessUserName = businessUserName; }
    public String getBusinessManDistributionFlag() { return businessManDistributionFlag; }
    public void setBusinessManDistributionFlag(String businessManDistributionFlag) { this.businessManDistributionFlag = businessManDistributionFlag; }
    public String getStoreUserId() { return storeUserId; }
    public void setStoreUserId(String storeUserId) { this.storeUserId = storeUserId; }
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    public String getStoreRequestStatusName() { return storeRequestStatusName; }
    public void setStoreRequestStatusName(String storeRequestStatusName) { this.storeRequestStatusName = storeRequestStatusName; }
    public String getStoreTransactionStatus() { return storeTransactionStatus; }
    public void setStoreTransactionStatus(String storeTransactionStatus) { this.storeTransactionStatus = storeTransactionStatus; }
    public String getCmrockStatus() { return cmrockStatus; }
    public void setCmrockStatus(String cmrockStatus) { this.cmrockStatus = cmrockStatus; }
    public String getSellrockStatus() { return sellrockStatus; }
    public void setSellrockStatus(String sellrockStatus) { this.sellrockStatus = sellrockStatus; }
    public LocalDateTime getStoreRegistrationDate() { return storeRegistrationDate; }
    public void setStoreRegistrationDate(LocalDateTime storeRegistrationDate) { this.storeRegistrationDate = storeRegistrationDate; }
    
    // 포맷된 날짜 문자열을 반환하는 메서드
    public String getFormattedStoreRegistrationDate() {
        if (storeRegistrationDate == null) return "";
        return storeRegistrationDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분"));
    }
} 