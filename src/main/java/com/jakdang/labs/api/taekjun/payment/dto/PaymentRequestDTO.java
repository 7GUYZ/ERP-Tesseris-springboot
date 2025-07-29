package com.jakdang.labs.api.taekjun.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDTO {
    private Integer targetUserIndex; // 결제할 가맹점 user_index
    private Integer amount; // 결제 금액 (CM)
    private Integer actualCmAmount; // 실제 차감되는 CM 금액
    private List<Integer> couponIndexes; // 사용할 쿠폰 인덱스 리스트
    private Integer couponTotal; // 쿠폰 총 금액
    private String pinCode; // 핀번호
} 