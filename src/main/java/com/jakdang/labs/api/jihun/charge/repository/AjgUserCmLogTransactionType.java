package com.jakdang.labs.api.jihun.charge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.UserCmLogTransactionType;

@Repository
public interface AjgUserCmLogTransactionType extends JpaRepository<UserCmLogTransactionType, Integer> {
    
    // 거래 타입 이름으로 조회 (충전, 판매, 구매 등)
    UserCmLogTransactionType findByUserCmLogTransactionTypeName(String userCmLogTransactionTypeName);
} 