package com.jakdang.labs.api.jihun.charge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.UserCmLogValueType;

@Repository
public interface AjgUserCmLogValueType extends JpaRepository<UserCmLogValueType, Integer> {
    
    // 값 타입 이름으로 조회 (CM, CMP, Cash)
    UserCmLogValueType findByUserCmLogValueTypeName(String userCmLogValueTypeName);
} 