package com.jakdang.labs.api.deokkyu.businessman.repository;

import com.jakdang.labs.entity.TemporaryStoreMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TemporaryStoreMasterRepository extends JpaRepository<TemporaryStoreMaster, Integer> {
    
    // 모든 데이터 조회 (최신순)
    List<TemporaryStoreMaster> findAllByOrderByTemporaryStoreMasterIndexDesc();
} 