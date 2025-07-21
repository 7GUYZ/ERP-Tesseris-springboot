package com.jakdang.labs.api.jungeun.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStoreDetailDTO {
    private Integer storeIndex;
    private String storeName;
    private String storePhone;
    private String storeAddress;
    private String storeCategoryName;
    private Integer userCmUse; // CM 금액 (잔액 표시)
    private String storeImage; // 이미지 url
    private Integer storeBusinessState; 
}
