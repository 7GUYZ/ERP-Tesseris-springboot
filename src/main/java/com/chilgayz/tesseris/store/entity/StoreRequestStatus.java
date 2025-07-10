package com.chilgayz.tesseris.store.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Store_Request_Status")
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
