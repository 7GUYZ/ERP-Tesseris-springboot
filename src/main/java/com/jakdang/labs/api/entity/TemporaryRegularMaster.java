package com.jakdang.labs.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "temporary_regular_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemporaryRegularMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "temporary_store_master_index")
    private Integer temporaryStoreMasterIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_user_index")
    private Store storeUserIndex;

    @Column(name = "temporary_store_master_transaction_name", length = 200, nullable = false)
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