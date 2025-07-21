package com.jakdang.labs.api.jungeun.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.StoreCategory;

@Repository
public interface StoreCategoryLjeRepo extends JpaRepository<StoreCategory, Integer> {
    
}
