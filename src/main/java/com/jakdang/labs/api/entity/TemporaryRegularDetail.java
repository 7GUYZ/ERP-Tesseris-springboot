package com.jakdang.labs.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "temporary_regular_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemporaryRegularDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "temporary_store_detail_index")
    private Integer temporaryStoreDetailIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_index", nullable = false)
    private UserTesseris userIndex;

    @Column(name = "original_cash_value", nullable = false)
    private Integer originalCashValue;

    @Column(name = "tax", length = 8, nullable = false)
    private String tax;

    @Column(name = "account", length = 50)
    private String account;

    @Column(name = "temporary_regular_cash_value", nullable = false)
    private Double temporaryRegularCashValue;

    @Column(name = "payment_status", length = 50, nullable = false)
    private String paymentStatus;

    @Column(name = "description", length = 50, nullable = false)
    private String description;

    @Column(name = "temporary_regular_master_index", nullable = false)
    private Integer temporaryRegularMasterIndex;
} 