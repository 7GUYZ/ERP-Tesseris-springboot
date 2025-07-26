package com.jakdang.labs.api.jihun.charge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.UserCmLog;

@Repository
public interface AjgUserCmLog extends JpaRepository<UserCmLog, Integer> {
    
} 