package com.jakdang.labs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "store_business_hours")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreBusinessHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_business_hours_index")
    private Integer storeBusinessHoursIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_user_index")
    private Store storeUserIndex;

    @Column(name = "store_start_business_hour", length = 100)
    private String storeStartBusinessHour;

    @Column(name = "store_end_business_hour", length = 100)
    private String storeEndBusinessHour;

    @Column(name = "store_rest_status", length = 30)
    private String storeRestStatus;

    @Column(name = "store_rest_start_hour", length = 100)
    private String storeRestStartHour;

    @Column(name = "store_rest_end_hour", length = 100)
    private String storeRestEndHour;

    @Column(name = "store_business_date", length = 100)
    private String storeBusinessDate;
} 