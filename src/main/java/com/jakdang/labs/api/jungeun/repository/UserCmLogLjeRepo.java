package com.jakdang.labs.api.jungeun.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.UserCmLog;

@Repository
public interface UserCmLogLjeRepo extends JpaRepository<UserCmLog, Integer>{
    
}
