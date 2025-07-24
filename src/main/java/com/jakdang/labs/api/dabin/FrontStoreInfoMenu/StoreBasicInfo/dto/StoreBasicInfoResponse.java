package com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreBasicInfoResponse {
    private Integer storeIndex;
    private String storeName;
    private String storeCategoryName;
    private String storePhone;
    private String storeSite;
    private String storeZoneCode;
    private String storeAddress;
    private String storeDetailAddress;
    private String storeMemo;
    private String storePos1;
    private String storePos2;
} 