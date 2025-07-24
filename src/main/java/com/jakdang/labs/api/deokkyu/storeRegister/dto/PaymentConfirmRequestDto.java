package com.jakdang.labs.api.deokkyu.storeRegister.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmRequestDto {
    
    private String paymentKey;  // 토스페이먼츠에서 발급받은 결제 키
    private String orderId;     // 주문 ID
    private Long amount;        // 결제 금액
} 