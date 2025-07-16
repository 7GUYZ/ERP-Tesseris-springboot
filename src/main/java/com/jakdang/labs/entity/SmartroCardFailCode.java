package com.jakdang.labs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "smartro_card_fail_code")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmartroCardFailCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fail_code")
    private Integer failCode;

    @Column(name = "fail_msg", length = 100)
    private String failMsg;
} 