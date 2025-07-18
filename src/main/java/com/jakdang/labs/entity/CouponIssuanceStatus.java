package com.jakdang.labs.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coupon_issuance_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponIssuanceStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_issuance_status_index")
    private Integer couponIssuanceStatusIndex;

    @Column(name = "coupon_issuance_status", length = 50)
    private String couponIssuanceStatus;
} 