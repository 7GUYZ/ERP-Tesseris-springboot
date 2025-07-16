package com.jakdang.labs.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 유저 CM 로그 결제 타입 테이블
 * 
 * user_cm_log_payment_index -- 기본키
 * user_cm_log_payment_name -- 결제 타입 이름 (입금, 출금)
 */
@Entity
@Table(name = "user_cm_log_payment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCmLogPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_cm_log_payment_index")
    private Integer userCmLogPaymentIndex;

    @Column(name = "user_cm_log_payment_name", length = 30)
    private String userCmLogPaymentName;
} 