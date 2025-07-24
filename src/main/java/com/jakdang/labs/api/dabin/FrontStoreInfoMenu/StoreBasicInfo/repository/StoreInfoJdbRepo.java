package com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.dto.StoreInfoResponse;
import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.dto.StoreImageResponse;
import com.jakdang.labs.entity.Store;
import com.jakdang.labs.entity.StoreImage;

@Repository
public interface StoreInfoJdbRepo extends JpaRepository<Store, Integer> {
    
    @Query("""
        SELECT new com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.dto.StoreInfoResponse(
            s.storeIndex,
            s.storeName,
            sc.storeCategoryName,
            s.storePhone,
            s.storeSite,
            s.storeZoneCode,
            s.storeAddress,
            s.storeDetailAddress,
            s.storeMemo,
            s.storePos1,
            s.storePos2
        )
        FROM Store s
        JOIN s.storeCategory sc
        WHERE s.userIndex.userIndex = :userIndex
    """)
    Optional<StoreInfoResponse> getStoreInfoByUserIndex(@Param("userIndex") Integer userIndex);
    
    @Query("""
        SELECT new com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.dto.StoreImageResponse(
            si.storeImageIndex,
            si.storeImage,
            si.storeMainImageStatus
        )
        FROM StoreImage si
        WHERE si.storeUserIndex.userIndex.userIndex = :userIndex
        ORDER BY si.storeMainImageStatus DESC
    """)
    List<StoreImageResponse> getStoreImagesByUserIndex(@Param("userIndex") Integer userIndex);
    
    @Query("SELECT s FROM Store s WHERE s.userIndex.userIndex = :userIndex")
    Optional<Store> findByUserIndex(@Param("userIndex") Integer userIndex);
} 