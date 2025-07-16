package com.jakdang.labs.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 출금 마스터 테이블
 * 
 * withdrawal_master_index - 기본키
 * withdrawal_master_name - 출금 거래 이름 ( ex) 2024년 1월 1째주 , 2024년 1월 4째주 )
 * withdrawal_master_status - 출금 확정 여부 ( y , n )
 */
@Entity
@Table(name = "withdrawal_master")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "withdrawal_master_index")
    private Integer withdrawalMasterIndex;

    @Column(name = "withdrawal_master_name", nullable = false, length = 200)
    private String withdrawalMasterName;

    @Column(name = "withdrawal_master_status", nullable = false, length = 200)
    private String withdrawalMasterStatus;
} 