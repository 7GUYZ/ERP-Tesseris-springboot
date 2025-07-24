package com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreMyPageInfoResponse {
    private String storeName;
    private String storeCategoryName;
    private String storeAddress;
    private String storePhone;
    private String storeSite;
    private String storeZoneCode;
    private String storeDetailAddress;
    private String storeMemo;
    private String busineeUserId; // SQL 쿼리와 일치하도록 원래 이름으로 복원

} 