package com.jakdang.labs.api.dabin.FrontEventCouponList.repository;

import com.jakdang.labs.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("frontEventCouponJdbRepo")
public interface FrontEventCouponRepository extends JpaRepository<Coupon, Integer> {
    
    @Query("""
        SELECT 
            c.couponName,
            c.couponPrice,
            c.couponLimit,
            c.couponIssuanceTime,
            '보유중',
            c.providedUser.userIndex,
            '지급완료',
            c.couponProvidedTime,
            c.couponLimitTime,
            '테스트 사용자'
        FROM Coupon c
        LEFT JOIN c.providedUser
        WHERE c.issuanceUser.userIndex = :userIndex AND c.couponIndex = :couponIndex
        """)
    Optional<Object> findCouponDetailByUserIndexAndCouponIndex(
        @Param("userIndex") Integer userIndex,
        @Param("couponIndex") Integer couponIndex
    );
} 