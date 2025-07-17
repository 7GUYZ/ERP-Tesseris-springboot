package com.jakdang.labs.api.dabin.repository.CmsSalesPerformance;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.StoreRequestStatus;

@Repository("salesStoreRequestStatusRepository")
public interface StoreRequestStatusJdbRepo extends JpaRepository<StoreRequestStatus, Integer> {
    // 기본 CRUD 메서드 사용
} 