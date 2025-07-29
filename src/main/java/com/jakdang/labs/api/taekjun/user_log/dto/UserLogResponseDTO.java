package com.jakdang.labs.api.taekjun.user_log.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 CM 사용 내역 응답 DTO
 * 
 * 주요 정보:
 * - 거래 금액 및 타입
 * - 거래 상대방 정보
 * - 거래 시간 및 사유
 * - 쿠폰 사용 정보
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLogResponseDTO {

    /**
     * CM 로그 인덱스
     */
    private Integer userCmLogIndex;

    /**
     * 거래 금액 (양수: 입금, 음수: 출금)
     */
    private Integer userCmLogValue;

    /**
     * 거래 사유
     */
    private String userCmLogReason;

    /**
     * 거래 생성 시간
     */
    private LocalDateTime userCmLogCreateTime;

    /**
     * 쿠폰 사용 금액
     */
    private Integer userCouponValue;

    /**
     * 거래 타입 인덱스
     */
    private Integer userCmLogTransactionTypeIndex;

    /**
     * 거래 타입 이름
     */
    private String transactionTypeName;

    /**
     * 결제 타입 인덱스
     */
    private Integer userCmLogPaymentIndex;

    /**
     * 결제 타입 이름
     */
    private String paymentTypeName;

    /**
     * 값 타입 인덱스
     */
    private Integer userCmLogValueTypeIndex;

    /**
     * 값 타입 이름
     */
    private String valueTypeName;

    /**
     * 거래 요청인 사용자 인덱스
     */
    private Integer userIndexEventTrigger;

    /**
     * 거래 요청인 사용자 이메일
     */
    private String triggerUserEmail;

    /**
     * 거래 요청인 사용자 이름
     */
    private String triggerUserName;

    /**
     * 거래 상대방 사용자 인덱스
     */
    private Integer userIndexEventParty;

    /**
     * 거래 상대방 사용자 이메일
     */
    private String partyUserEmail;

    /**
     * 거래 상대방 사용자 이름
     */
    private String partyUserName;

    /**
     * 거래 상대방 사용자 역할
     */
    private String partyUserRole;

    /**
     * 거래 취소 여부
     */
    private String userCmLogTransactionCancel;

    /**
     * 거래 금액 (포맷팅된 문자열)
     */
    private String formattedAmount;

    /**
     * 거래 시간 (포맷팅된 문자열)
     */
    private String formattedCreateTime;
} 