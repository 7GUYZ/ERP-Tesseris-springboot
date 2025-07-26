package com.jakdang.labs.api.dabin.FrontEventCouponList.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseInfoResponse {
    private Integer storeIndex;
    private String name;
    private String phone;
    private String address;
    private String detailAddress;
    private String category;
    private String image;
    private Double latitude;
    private Double longitude;
    private String userCmUse;
} 