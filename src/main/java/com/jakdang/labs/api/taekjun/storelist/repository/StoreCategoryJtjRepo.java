package com.jakdang.labs.api.taekjun.storelist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.StoreCategory;

@Repository
public interface StoreCategoryJtjRepo extends JpaRepository<StoreCategory, Integer> {
    
} 