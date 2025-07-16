package com.jakdang.labs.api.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "distribution_rate")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistributionRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "distribution_rate_index")
    private Integer distributionRateIndex;

    @Column(name = "user_role_index")
    private Integer userRoleIndex;

    @Column(name = "distribution_cm_rate")
    private Float distributionCmRate;

    @Column(name = "distribution_cash_rate")
    private Float distributionCashRate;
} 