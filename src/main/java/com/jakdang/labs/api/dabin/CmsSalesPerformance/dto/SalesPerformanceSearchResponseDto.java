package com.jakdang.labs.api.dabin.CmsSalesPerformance.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesPerformanceSearchResponseDto {
    private String businessUserId;
    private String businessGradeName;
    private String businessAreaName;
    private String businessUserName;
    private String businessManDistributionFlag;
    private String storeUserId;
    private String storeName;
    private String storeRequestStatusName;
    private String storeTransactionStatus;
    private String cmrockStatus;
    private String sellrockStatus;
    private LocalDateTime storeRegistrationDate;

    
    // 포맷된 날짜 문자열을 반환하는 메서드
    public String getFormattedStoreRegistrationDate() {
        if (storeRegistrationDate == null) return "";
        return storeRegistrationDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분"));
    }
} 