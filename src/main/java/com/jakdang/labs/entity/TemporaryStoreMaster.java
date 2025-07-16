package com.jakdang.labs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 임시 통장 마스터 테이블
 * 
 * temporary_store_master_index -- 임시 통장 기본키값
 * store_user_index -- 결제를 진행한 가맹점 유저Index
 * temporary_store_master_transaction_name -- 거래이름
 * temporary_store_master_charge_time -- 충전한 시간
 * temporary_store_master_distribution_time -- 분배가 될 시간
 * temporary_store_cm_value -- 가맹점이 충전한 cm
 * temporary_store_cash_value -- 가맹점이 충전한 실제 돈
 * temporary_store_master_distribution_status -- 분배 여부 ('y','n','w','c')
 * temporary_store_fee_value -- 수수료 값
 */
@Entity
@Table(name = "temporary_store_master")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemporaryStoreMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "temporary_store_master_index")
    private Integer temporaryStoreMasterIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_user_index")
    private Store storeUserIndex;

    @Column(name = "temporary_store_master_transaction_name", nullable = false, length = 200)
    private String temporaryStoreMasterTransactionName;

    @Column(name = "temporary_store_master_charge_time")
    private LocalDateTime temporaryStoreMasterChargeTime;

    @Column(name = "temporary_store_master_distribution_time")
    private LocalDateTime temporaryStoreMasterDistributionTime;

    @Column(name = "temporary_store_master_distribution_status", length = 30)
    private String temporaryStoreMasterDistributionStatus;

    @Column(name = "temporary_store_cm_value")
    private Integer temporaryStoreCmValue;

    @Column(name = "temporary_store_cash_value")
    private Integer temporaryStoreCashValue;

    @Column(name = "temporary_store_fee_value")
    private Integer temporaryStoreFeeValue;


} 