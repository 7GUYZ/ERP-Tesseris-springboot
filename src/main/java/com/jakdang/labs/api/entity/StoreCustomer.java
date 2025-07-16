package com.jakdang.labs.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 가맹점 고객관리 테이블
 * 
 * store_customer_index -- 기본키
 * store_store_user_index -- 가맹점 유저 인덱스
 * store_customer_user_index -- 고객 유저 인덱스
 * store_customer_status -- 고객 상태
 * store_customer_insert_date -- 등록일시
 */
@Entity
@Table(name = "store_customer", 
       uniqueConstraints = {
           @UniqueConstraint(name = "unique_store_customer_pair", 
                           columnNames = {"store_store_user_index", "store_customer_user_index"})
       })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_customer_index")
    private Integer storeCustomerIndex;

    @Column(name = "store_store_user_index", nullable = false, length = 50)
    private String storeStoreUserIndex;

    @Column(name = "store_customer_user_index", nullable = false, length = 50)
    private String storeCustomerUserIndex;

    @Column(name = "store_customer_status", length = 6)
    private String storeCustomerStatus;

    @Column(name = "store_customer_insert_date")
    private LocalDateTime storeCustomerInsertDate;

    @PrePersist
    protected void onCreate() {
        if (storeCustomerInsertDate == null) {
            storeCustomerInsertDate = LocalDateTime.now();
        }
    }
} 