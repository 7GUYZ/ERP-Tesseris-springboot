package com.jakdang.labs.api.dabin.repository.FrontMyPageStoreInfo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jakdang.labs.entity.Store;

import java.util.Optional;
 
public interface FrontMyPageStoreInfoJdbRepo extends JpaRepository<Store, Integer> {
    Optional<Store> findByUserIndex_UserIndex(Integer userIndex);
    
    // 더 명확한 쿼리 메서드 추가
    @Query("SELECT s FROM Store s WHERE s.userIndex.userIndex = :userIndex")
    Optional<Store> findByUserIndex(@Param("userIndex") Integer userIndex);
} 