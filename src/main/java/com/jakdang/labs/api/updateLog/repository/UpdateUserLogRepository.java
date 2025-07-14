package com.jakdang.labs.api.updateLog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jakdang.labs.api.updateLog.entity.UpdateUserLog;

public interface UpdateUserLogRepository extends JpaRepository<UpdateUserLog, Integer>{
  
}
