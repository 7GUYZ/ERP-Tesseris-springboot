package com.jakdang.labs.api.taekjun.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInfoDTO {
    private Integer monthlyLimit; // 월 결제 한도
    private Integer monthlyUsed; // 이번 달 사용 금액
    private Integer currentCm; // 현재 보유 CM
    private String currentMonth; // 현재 월 (YYYY년 MM월)
} 