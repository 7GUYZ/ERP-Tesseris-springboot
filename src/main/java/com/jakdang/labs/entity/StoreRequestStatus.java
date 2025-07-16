package com.jakdang.labs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "store_request_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreRequestStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_request_status_index")
    private Integer storeRequestStatusIndex;

    @Column(name = "store_request_status_name", length = 30)
    private String storeRequestStatusName;
} 