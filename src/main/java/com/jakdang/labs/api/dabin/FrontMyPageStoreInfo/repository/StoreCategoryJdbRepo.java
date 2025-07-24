package com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.jakdang.labs.entity.StoreCategory;
import java.util.Optional;
 
public interface StoreCategoryJdbRepo extends JpaRepository<StoreCategory, Integer> {
    Optional<StoreCategory> findByStoreCategoryName(String storeCategoryName);
} 