package com.jakdang.labs.api.deokkyu.businessman.repository;

import com.jakdang.labs.entity.TemporaryStoreDetail;
import com.jakdang.labs.entity.TemporaryStoreMaster;
import com.jakdang.labs.entity.UserTesseris;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TemporaryStoreDetailhdkRepo extends JpaRepository<TemporaryStoreDetail, Integer> {
    List<TemporaryStoreDetail> findByTemporaryStoreMasterIndex(TemporaryStoreMaster temporaryStoreMasterIndex);
    
    // user_index로 조회
    List<TemporaryStoreDetail> findByUserIndex(UserTesseris userIndex);
} 