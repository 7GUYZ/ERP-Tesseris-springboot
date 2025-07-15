package com.jakdang.labs.api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "store_customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreCustomer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_customer_index")
    private Integer storeCustomerIndex;

    @Column(name = "store_store_user_index")
    private Integer storeStoreUserIndex; // 가맹점 계정 index (Store.user_index)

    @Column(name = "store_customer_user_index")
    private Integer storeCustomerUserIndex; // 고객 계정 index (User.user_index)

    @Column(name = "store_customer_status")
    private String storeCustomerStatus; // 고객 등급 ('일반 고객', '단골 고객')

    @Column(name = "store_customer_insert_date")
    private LocalDateTime storeCustomerInsertDate; // 등록일
}
