package com.jakdang.labs.api.jihun.charge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.RegularPayment;

@Repository
public interface AjgRegularPayment extends JpaRepository<RegularPayment, Integer> {
    
} 