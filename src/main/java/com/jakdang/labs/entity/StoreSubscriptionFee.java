package com.jakdang.labs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "store_subscription_fee")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreSubscriptionFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_subscription_fee_index")
    private Integer storeSubscriptionFeeIndex;

    @Column(name = "store_subscription_fee_transaction_name", length = 200)
    private String storeSubscriptionFeeTransactionName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_man_user_index", nullable = false)
    private BusinessMan businessManUserIndex;

    @Column(name = "store_subscription_fee_time", nullable = false)
    private LocalDateTime storeSubscriptionFeeTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_user_index")
    private Store storeUserIndex;

    @Column(name = "store_subscription_fee_payment_method", length = 200)
    private String storeSubscriptionFeePaymentMethod;

    @Column(name = "store_subscription_fee_commission_check", length = 200)
    private String storeSubscriptionFeeCommissionCheck;

    @Column(name = "store_subscription_fee_value")
    private Integer storeSubscriptionFeeValue;
} 