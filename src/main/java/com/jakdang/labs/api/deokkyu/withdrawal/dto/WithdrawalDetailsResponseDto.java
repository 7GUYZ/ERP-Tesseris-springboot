package com.jakdang.labs.api.deokkyu.withdrawal.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawalDetailsResponseDto {
    private String userId;           // 사용자 ID
    private String userName;         // 사용자 이름
    private String userPhone;        // 사용자 전화번호
    private String userBankName;     // 은행명
    private String userBankNumber;   // 계좌번호
    private Integer chargeAmount;    // 출금 금액
    private String transactionName;  // 거래명 (출금)
    private LocalDate chargeDate;    // 출금 날짜
    private Integer cmValue;         // CM 값
} 