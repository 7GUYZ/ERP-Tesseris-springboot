package com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jakdang.labs.entity.Store;

import java.util.Optional;

public interface FrontMyPageStoreInfoJdbRepo extends JpaRepository<Store, Integer> {
    @Query("SELECT s FROM Store s WHERE s.userIndex.userIndex = :userIndex ORDER BY s.storeIndex DESC")
    Optional<Store> findFirstByUserIndex(@Param("userIndex") Integer userIndex);
    
    // 사업자 사용자 ID 조회
    @Query("SELECT u.usersId.id FROM UserTesseris u WHERE u.userIndex = :businessManUserIndex")
    Optional<String> findBusinessUserIdByUserIndex(@Param("businessManUserIndex") Integer businessManUserIndex);
} 