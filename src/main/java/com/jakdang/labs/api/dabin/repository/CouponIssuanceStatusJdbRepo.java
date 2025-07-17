package com.jakdang.labs.api.dabin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jakdang.labs.entity.CouponIssuanceStatus;



public interface CouponIssuanceStatusJdbRepo extends JpaRepository<CouponIssuanceStatus, Integer> {
} 