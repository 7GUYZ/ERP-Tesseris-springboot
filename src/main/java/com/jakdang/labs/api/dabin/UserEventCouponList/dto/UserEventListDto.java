package com.jakdang.labs.api.dabin.UserEventCouponList.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEventListDto {
    private Integer eventMasterIndex;
    private String eventMasterName;
    private String eventMasterCondition;
    private Long totalCouponPrice;
    private String storeAddress;
    private String storeName;
    private Integer eventMasterCount;
    private Integer remainingDownloads; // 개별 사용자의 남은 다운로드 가능 횟수
} 