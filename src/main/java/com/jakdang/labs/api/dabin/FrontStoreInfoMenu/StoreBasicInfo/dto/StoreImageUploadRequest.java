package com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreImageUploadRequest {
    private Integer userIndex;
    private String imageUrl;
    private String mainImageStatus; // "T" for main, "N" for sub
} 