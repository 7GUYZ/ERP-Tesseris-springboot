package com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreOperation.repository;

import com.jakdang.labs.entity.StoreBusinessHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreBusinessHoursJdbRepo extends JpaRepository<StoreBusinessHours, Integer> {
    
    @Query("SELECT sbh FROM StoreBusinessHours sbh WHERE sbh.storeUserIndex.userIndex.userIndex = :userIndex ORDER BY sbh.storeBusinessHoursIndex ASC")
    List<StoreBusinessHours> findByUserIndex(@Param("userIndex") Integer userIndex);
    
    @Query("SELECT COUNT(sbh) FROM StoreBusinessHours sbh WHERE sbh.storeUserIndex.userIndex.userIndex = :userIndex")
    Long countByUserIndex(@Param("userIndex") Integer userIndex);
} 