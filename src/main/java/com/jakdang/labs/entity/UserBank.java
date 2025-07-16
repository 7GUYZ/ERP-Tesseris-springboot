package com.jakdang.labs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_bank")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_bank_index")
    private Integer userBankIndex;

    @Column(name = "user_bank_name", length = 6)
    private String userBankName;
} 