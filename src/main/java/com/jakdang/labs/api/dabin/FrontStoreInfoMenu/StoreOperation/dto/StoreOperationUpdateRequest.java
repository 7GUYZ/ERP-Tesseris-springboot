package com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreOperation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreOperationUpdateRequest {
    private List<BusinessHoursDto> businessHours;
    private String holidayStatus; // Y: 공휴일/국경일 휴무, N: 정상영업
    private String regularClosingInterval; // 매주, 격주
    private String regularClosingWeek; // 월요일, 화요일, ...
    private String temporaryClosingDate; // 임시 휴무 날짜
    private String temporaryClosingComment; // 임시 휴무 코멘트
    private List<Integer> removeList; // 삭제할 영업시간 인덱스들
} 