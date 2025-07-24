package com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreOperation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessHoursDto {
    private Integer storeBusinessHoursIndex;
    private String workStartTime;
    private String workEndTime;
    private String restTime; // Y: 휴게시간 있음, N: 휴게시간 없음
    private String restStartTime;
    private String restEndTime;
    private List<String> businessDays; // ["sunday", "monday", "tuesday", ...]
} 