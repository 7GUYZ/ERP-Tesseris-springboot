package com.jakdang.labs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 유저 CM 로그 거래 타입 테이블
 * 
 * user_cm_log_transaction_type_index -- 기본키
 * user_cm_log_transaction_type_name -- 거래 타입 이름
 * 
 * 거래 타입들:
 * - 중개수수료
 * - 본사지급(CM)
 * - 본사회수(CM)
 * - 본사지급(CMP)
 * - 본사회수(CMP)
 * - 가맹비
 * - 충전
 * - 판매
 * - 구매
 * - 선물
 * - 구매(결제 취소)
 * - 판매(결제 취소)
 * - 쿠폰
 * - 쿠폰발행취소
 * - 초기지급(CMP)
 * - 충전 취소
 */
@Entity
@Table(name = "user_cm_log_transaction_type")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCmLogTransactionType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_cm_log_transaction_type_index")
    private Integer userCmLogTransactionTypeIndex;

    @Column(name = "user_cm_log_transaction_type_name", length = 30)
    private String userCmLogTransactionTypeName;
} 