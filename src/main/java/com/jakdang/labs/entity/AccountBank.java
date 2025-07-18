package com.jakdang.labs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_bank")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountBank {

    @Id
    @Column(name = "account_bank_code", length = 200)
    private String accountBankCode;

    @Column(name = "account_bank_name", length = 200)
    private String accountBankName;
} 