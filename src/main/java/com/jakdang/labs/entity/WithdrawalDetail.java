package com.jakdang.labs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 출금 디테일 테이블
 * 
 * withdrawal_detail_index - 기본키
 * withdrawal_master_index - 출금 마스터 참조키
 * temporary_store_master_index -- 임시 통장 마스터 참조키
 */
@Entity
@Table(name = "withdrawal_detail")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "withdrawal_detail_index")
    private Integer withdrawalDetailIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "withdrawal_master_index", nullable = false)
    private WithdrawalMaster withdrawalMasterIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "temporary_store_master_index", nullable = false)
    private TemporaryStoreMaster temporaryStoreMasterIndex;
} 