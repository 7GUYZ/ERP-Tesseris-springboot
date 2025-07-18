package com.jakdang.labs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 유저 통장 테이블
 * 
 * user_cm_index -- 유저 테이블 index 참조키 값
 * user_cm_deposit -- CM 총 입금량
 * user_cm_withdrawal -- CM 총 출금량
 * user_cash_deposit -- 현금 총 입금량
 * user_cash_withdrawal -- 현금 총 출금량
 * user_cmp_deposit -- CMP 총 입금량
 * user_cmp_withdrawal -- CMP 총 출금량
 * user_cmp_init -- 초기 지급 CMP양
 * user_cm_pincode -- 유저의 pin code
 */
@Entity
@Table(name = "user_cm")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_cm_index")
    private Integer userCmIndex;

    @Column(name = "user_cm_deposit")
    private Integer userCmDeposit;

    @Column(name = "user_cm_withdrawal")
    private Integer userCmWithdrawal;

    @Column(name = "user_cash_deposit")
    private Integer userCashDeposit;

    @Column(name = "user_cash_withdrawal")
    private Integer userCashWithdrawal;

    @Column(name = "user_cmp_deposit")
    private Integer userCmpDeposit;

    @Column(name = "user_cmp_withdrawal")
    private Integer userCmpWithdrawal;

    @Column(name = "user_cmp_init")
    private Integer userCmpInit;

    @Column(name = "user_cm_pincode", length = 6)
    private String userCmPincode;
} 