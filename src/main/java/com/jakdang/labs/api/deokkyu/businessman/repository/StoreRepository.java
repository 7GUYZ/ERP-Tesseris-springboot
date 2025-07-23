package com.jakdang.labs.api.deokkyu.businessman.repository;

import com.jakdang.labs.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Integer> {
    // store_index로 Store 조회
} 