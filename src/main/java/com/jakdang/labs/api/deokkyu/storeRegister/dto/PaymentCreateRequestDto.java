package com.jakdang.labs.api.deokkyu.storeRegister.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateRequestDto {
    
    private String orderId;        // 주문 ID
    private Long amount;           // 결제 금액
    private String orderName;      // 주문명
    private String customerName;   // 고객명
    private String customerEmail;  // 고객 이메일
    private String customerPhone;  // 고객 전화번호
    private String successUrl;     // 결제 성공 시 리다이렉트 URL
    private String failUrl;        // 결제 실패 시 리다이렉트 URL
} 