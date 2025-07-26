package com.jakdang.labs.api.dabin.FrontEventCouponList.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyFranchiseRequest {
    private Double latitude;
    private Double longitude;
    private Integer franType; // 카테고리 인덱스 (null이면 전체)
    private Double radius; // 반경 (km)
} 