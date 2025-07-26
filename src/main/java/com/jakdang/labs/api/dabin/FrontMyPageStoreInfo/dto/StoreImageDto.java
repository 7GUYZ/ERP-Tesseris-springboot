package com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.dto;

public class StoreImageDto {
    private Integer storeImageIndex;
    private String storeImage;
    private String storeMainImageStatus;
    public StoreImageDto(Integer storeImageIndex, String storeImage, String storeMainImageStatus) {
        this.storeImageIndex = storeImageIndex;
        this.storeImage = storeImage;
        this.storeMainImageStatus = storeMainImageStatus;
    }
    public Integer getStoreImageIndex() { return storeImageIndex; }
    public String getStoreImage() { return storeImage; }
    public String getStoreMainImageStatus() { return storeMainImageStatus; }
} 