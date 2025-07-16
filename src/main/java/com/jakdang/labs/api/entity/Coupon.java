package com.jakdang.labs.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupon")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_index")
    private Integer couponIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issuance_user_index")
    private UserTesseris issuanceUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provided_user_index")
    private UserTesseris providedUser;

    @Column(name = "nonmembers_number")
    private Integer nonmembersNumber;

    @Column(name = "coupon_price")
    private Integer couponPrice;

    @Column(name = "coupon_limit")
    private Integer couponLimit;

    @Column(name = "coupon_issuance_status_index")
    private Integer couponIssuanceStatusIndex;

    @Column(name = "coupon_name", length = 100)
    private String couponName;

    @Column(name = "coupon_issuance_time")
    private LocalDateTime couponIssuanceTime;

    @Column(name = "coupon_provided_status_index")
    private Integer couponProvidedStatusIndex;

    @Column(name = "coupon_provided_time")
    private LocalDateTime couponProvidedTime;

    @Column(name = "coupon_limit_time")
    private LocalDateTime couponLimitTime;

    @Column(name = "coupon_condition", length = 500)
    private String couponCondition;
} 