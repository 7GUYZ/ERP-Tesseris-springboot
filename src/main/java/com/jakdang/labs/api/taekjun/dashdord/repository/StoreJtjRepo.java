package com.jakdang.labs.api.taekjun.dashdord.repository;

import com.jakdang.labs.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreJtjRepo extends JpaRepository<Store, Integer> {
    // 승인된 가맹점 전체
    @Query(value = "SELECT COUNT(*) FROM store WHERE store_request_status_index = 2", nativeQuery = true)
    Long countApprovedStoreTotal();

    // 승인된 가맹점 어제
    @Query(value = "SELECT COUNT(*) FROM store WHERE store_request_status_index = 2 AND DATE(store_registration_date) = :date", nativeQuery = true)
    Long countApprovedStoreByDate(@Param("date") String date);

    // 승인 대기중인 가맹점 전체
    @Query(value = "SELECT COUNT(*) FROM store WHERE store_request_status_index = 1", nativeQuery = true)
    Long countPendingStoreTotal();
} 