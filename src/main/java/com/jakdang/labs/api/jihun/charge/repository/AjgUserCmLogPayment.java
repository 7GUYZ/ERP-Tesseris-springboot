package com.jakdang.labs.api.jihun.charge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.UserCmLogPayment;

@Repository
public interface AjgUserCmLogPayment extends JpaRepository<UserCmLogPayment, Integer> {
    
    // 결제 타입 이름으로 조회 (입금, 출금)
    UserCmLogPayment findByUserCmLogPaymentName(String userCmLogPaymentName);
} 