package com.jakdang.labs.api.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coupon_provided_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponProvidedStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_provided_status_index")
    private Integer couponProvidedStatusIndex;

    @Column(name = "coupon_provided_status", length = 50, nullable = false)
    private String couponProvidedStatus;
} 