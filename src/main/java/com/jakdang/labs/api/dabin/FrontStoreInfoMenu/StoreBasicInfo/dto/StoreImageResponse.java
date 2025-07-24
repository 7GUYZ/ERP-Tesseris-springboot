package com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreImageResponse {
    private Integer storeImageIndex;
    private String storeImage;
    private String storeMainImageStatus;
} 