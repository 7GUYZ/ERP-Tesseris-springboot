package com.jakdang.labs.api.jihun.memberassetdetails.repository;

import com.jakdang.labs.entity.UserCmLogPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AjgUserCmLogPaymentRepository extends JpaRepository<UserCmLogPayment, Integer> {
    Optional<UserCmLogPayment> findByUserCmLogPaymentIndex(Integer paymentIndex);
} 