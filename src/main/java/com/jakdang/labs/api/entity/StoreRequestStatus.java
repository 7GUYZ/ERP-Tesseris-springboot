package com.jakdang.labs.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "store_request_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreRequestStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_request_status_index")
    private Integer storeRequestStatusIndex;

    @Column(name = "store_request_status_name")
    private String storeRequestStatusName;
}
