package com.jakdang.labs.api.deokkyu.businessman.repository;

import com.jakdang.labs.entity.TemporaryStoreMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TemporaryStoreMasterhdkRepo extends JpaRepository<TemporaryStoreMaster, Integer> {
    List<TemporaryStoreMaster> findAllByOrderByTemporaryStoreMasterIndexDesc();
} 