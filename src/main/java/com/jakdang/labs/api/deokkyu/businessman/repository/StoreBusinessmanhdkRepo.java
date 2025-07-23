package com.jakdang.labs.api.deokkyu.businessman.repository;

import com.jakdang.labs.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoreBusinessmanhdkRepo extends JpaRepository<Store, Integer> {
    // store_index로 Store 조회
    
    // business_man_user_index로 매장 수 세기
    @Query("SELECT COUNT(s) FROM Store s WHERE s.businessManUserIndex = :businessManUserIndex")
    Integer countByBusinessManUserIndex(@Param("businessManUserIndex") Integer businessManUserIndex);
} 