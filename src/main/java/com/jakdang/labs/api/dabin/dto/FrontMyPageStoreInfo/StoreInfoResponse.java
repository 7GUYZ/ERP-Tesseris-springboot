package com.jakdang.labs.api.dabin.dto.FrontMyPageStoreInfo;

public class StoreInfoResponse {
    private String storeName;
    private String storeCategoryName;
    private String storeAddress;
    private String storePhone;
    private String storeSite;
    private String busineeUserId;

    public StoreInfoResponse(String storeName, String storeCategoryName, String storeAddress, String storePhone, String storeSite, String busineeUserId) {
        this.storeName = storeName;
        this.storeCategoryName = storeCategoryName;
        this.storeAddress = storeAddress;
        this.storePhone = storePhone;
        this.storeSite = storeSite;
        this.busineeUserId = busineeUserId;
    }

    public String getStoreName() { return storeName; }
    public String getStoreCategoryName() { return storeCategoryName; }
    public String getStoreAddress() { return storeAddress; }
    public String getStorePhone() { return storePhone; }
    public String getStoreSite() { return storeSite; }
    public String getBusineeUserId() { return busineeUserId; }

    public void setStoreName(String storeName) { this.storeName = storeName; }
    public void setStoreCategoryName(String storeCategoryName) { this.storeCategoryName = storeCategoryName; }
    public void setStoreAddress(String storeAddress) { this.storeAddress = storeAddress; }
    public void setStorePhone(String storePhone) { this.storePhone = storePhone; }
    public void setStoreSite(String storeSite) { this.storeSite = storeSite; }
    public void setBusineeUserId(String busineeUserId) { this.busineeUserId = busineeUserId; }
} 