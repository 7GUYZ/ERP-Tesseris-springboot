package com.jakdang.labs.api.deokkyu.withdrawal.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalDetailsRequestDto {
    private String startDate; // 시작일 (YYYY-MM-DD 형식)
    private String endDate;   // 종료일 (YYYY-MM-DD 형식)
} 