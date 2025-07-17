package com.jakdang.labs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_deposit_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDepositStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_deposit_status_index")
    private Integer accountDepositStatusIndex;

    @Column(name = "account_deposit_status_mid", length = 200)
    private String accountDepositStatusMid;

    @Column(name = "account_deposit_status_user_index")
    private Integer accountDepositStatusUserIndex;

    @Column(name = "account_bank_code", length = 200)
    private String accountBankCode;

    @Column(name = "account_deposit_status_account", length = 200)
    private String accountDepositStatusAccount;

    @Column(name = "account_deposit_status_date", length = 200)
    private String accountDepositStatusDate;

    @Column(name = "account_deposit_status", length = 200)
    private String accountDepositStatus;

    @Column(name = "account_deposit_insert_date")
    private LocalDateTime accountDepositInsertDate;

    @Column(name = "account_deposit_value")
    private Integer accountDepositValue;

    @Column(name = "account_deposit_tax")
    private Integer accountDepositTax;
} 