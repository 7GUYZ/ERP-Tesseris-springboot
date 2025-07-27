package com.jakdang.labs.api.taekjun.payment.repository;

import com.jakdang.labs.entity.StoreCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreCustomerRepository extends JpaRepository<StoreCustomer, Integer> {
    
    // 가맹점과 고객 간의 관계가 이미 존재하는지 확인
    @Query("SELECT sc FROM StoreCustomer sc WHERE sc.storeStoreUserIndex = :storeUserIndex AND sc.storeCustomerUserIndex = :customerUserIndex")
    Optional<StoreCustomer> findByStoreAndCustomer(@Param("storeUserIndex") String storeUserIndex, @Param("customerUserIndex") String customerUserIndex);
    
    // 가맹점의 고객 목록 조회
    @Query("SELECT sc FROM StoreCustomer sc WHERE sc.storeStoreUserIndex = :storeUserIndex")
    List<StoreCustomer> findByStoreUserIndex(@Param("storeUserIndex") String storeUserIndex);
    
    // 고객이 이용한 가맹점 목록 조회
    @Query("SELECT sc FROM StoreCustomer sc WHERE sc.storeCustomerUserIndex = :customerUserIndex")
    List<StoreCustomer> findByCustomerUserIndex(@Param("customerUserIndex") String customerUserIndex);
} 