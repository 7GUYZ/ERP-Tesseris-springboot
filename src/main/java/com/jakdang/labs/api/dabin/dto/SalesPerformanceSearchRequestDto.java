package com.jakdang.labs.api.dabin.dto;

public class SalesPerformanceSearchRequestDto {
    private String businessUserId;
    private Integer businessGradeIndex;
    private String userName;
    private Integer businessManDistributionFlag;
    private String storeUserId;
    private String storeName;
    private Integer storeRequestStatusIndex;
    private Integer storeTransactionStatus;

    // getter, setter
    public String getBusinessUserId() { return businessUserId; }
    public void setBusinessUserId(String businessUserId) { this.businessUserId = businessUserId; }
    public Integer getBusinessGradeIndex() { return businessGradeIndex; }
    public void setBusinessGradeIndex(Integer businessGradeIndex) { this.businessGradeIndex = businessGradeIndex; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public Integer getBusinessManDistributionFlag() { return businessManDistributionFlag; }
    public void setBusinessManDistributionFlag(Integer businessManDistributionFlag) { this.businessManDistributionFlag = businessManDistributionFlag; }
    public String getStoreUserId() { return storeUserId; }
    public void setStoreUserId(String storeUserId) { this.storeUserId = storeUserId; }
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    public Integer getStoreRequestStatusIndex() { return storeRequestStatusIndex; }
    public void setStoreRequestStatusIndex(Integer storeRequestStatusIndex) { this.storeRequestStatusIndex = storeRequestStatusIndex; }
    public Integer getStoreTransactionStatus() { return storeTransactionStatus; }
    public void setStoreTransactionStatus(Integer storeTransactionStatus) { this.storeTransactionStatus = storeTransactionStatus; }
} 