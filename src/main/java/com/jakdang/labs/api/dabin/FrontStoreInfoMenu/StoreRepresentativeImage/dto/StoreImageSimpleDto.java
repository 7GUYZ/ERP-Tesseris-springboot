package com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreRepresentativeImage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreImageSimpleDto {
    private Integer storeImageIndex;
    private String storeImage;
    private String storeMainImageStatus;
} 