package com.jakdang.labs.api.taekjun.customermanagement.repository;

import com.jakdang.labs.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Integer> {
} 