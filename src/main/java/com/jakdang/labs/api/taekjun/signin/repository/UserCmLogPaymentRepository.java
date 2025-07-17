package com.jakdang.labs.api.taekjun.signin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.UserCmLogPayment;

@Repository
public interface UserCmLogPaymentRepository extends JpaRepository<UserCmLogPayment, Integer> {
    
    @Query("SELECT u FROM UserCmLogPayment u WHERE u.userCmLogPaymentName = :paymentName")
    Optional<UserCmLogPayment> findByPaymentName(@Param("paymentName") String paymentName);
} 