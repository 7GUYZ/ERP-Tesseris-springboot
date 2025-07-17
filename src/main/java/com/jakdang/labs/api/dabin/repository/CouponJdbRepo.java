package com.jakdang.labs.api.dabin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jakdang.labs.entity.Coupon;



public interface CouponJdbRepo extends JpaRepository<Coupon, Integer>, CouponJdbRepoCustom {
} 