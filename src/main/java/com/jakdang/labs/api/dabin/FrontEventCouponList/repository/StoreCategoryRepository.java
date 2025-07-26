package com.jakdang.labs.api.dabin.FrontEventCouponList.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.jakdang.labs.entity.StoreCategory;

import java.util.List;

@Repository
public interface StoreCategoryRepository extends JpaRepository<StoreCategory, Integer> {
    
    /**
     * 모든 가맹점 카테고리 조회
     */
    @Query("SELECT sc FROM StoreCategory sc ORDER BY sc.storeCategoryIndex")
    List<StoreCategory> findAllCategories();
} 