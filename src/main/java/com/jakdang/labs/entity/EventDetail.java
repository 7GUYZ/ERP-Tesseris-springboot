package com.jakdang.labs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_detail_index")
    private Integer eventDetailIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_master_index")
    private EventMaster eventMaster;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_coupon_index")
    private Coupon eventCoupon;
} 