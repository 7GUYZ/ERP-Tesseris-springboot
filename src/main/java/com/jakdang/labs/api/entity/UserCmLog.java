package com.jakdang.labs.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 유저 CM 로그 테이블
 * 
 * user_cm_log_index - 기본키값
 * user_cm_log_payment_index - 거래의 종류 (입금,출금)
 * user_cmp_log_payment_index - CMP 거래의 종류 ( 회수, 지급, 충전 )
 * user_cm_log_transaction_type_index - 거래 타입 (중개수수료,본사CM지급 등등)
 * user_cm_log_value_type_index - 화폐 단위 (CM,CMP,쿠폰)
 * user_index_event_trigger - 거래 요청인
 * user_index_event_party - 거래 상대방
 * user_cm_log_value - 거래에 사용된 단위
 * user_cm_log_reason - 거래에 대한 메모
 * user_cm_log_create_time - 거래 발생 시간
 * user_cm_log_transaction_cancel - 판매 취소를 할때 사용되는 컬럼
 * user_coupon_value - 쿠폰으로 사용된 금액
 */
@Entity
@Table(name = "user_cm_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCmLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_cm_log_index")
    private Integer userCmLogIndex;

    @Column(name = "user_cm_log_payment_index")
    private Integer userCmLogPaymentIndex;

    @Column(name = "user_cmp_log_payment_index")
    private Integer userCmpLogPaymentIndex;

    @Column(name = "user_cm_log_transaction_type_index")
    private Integer userCmLogTransactionTypeIndex;

    @Column(name = "user_cm_log_value_type_index")
    private Integer userCmLogValueTypeIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_index_event_trigger")
    private UserTesseris userIndexEventTrigger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_index_event_party")
    private UserTesseris userIndexEventParty;

    @Column(name = "user_cm_log_value")
    private Integer userCmLogValue;

    @Column(name = "user_cm_log_reason", length = 90)
    private String userCmLogReason;

    @Column(name = "user_cm_log_create_time")
    private LocalDateTime userCmLogCreateTime;

    @Column(name = "user_cm_log_transaction_cancel", length = 30)
    private String userCmLogTransactionCancel;

    @Column(name = "user_coupon_value")
    private Integer userCouponValue;
} 