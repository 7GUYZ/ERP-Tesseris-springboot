package com.jakdang.labs.api.taekjun.coponlist.repository;

import com.jakdang.labs.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CouponListJtjRepo extends JpaRepository<Coupon, Integer> {
    
    /**
     * 내가 받은 쿠폰 리스트 조회
     */
    @Query("SELECT c FROM Coupon c WHERE c.providedUser.userIndex = :userIndex ORDER BY c.couponIssuanceTime DESC")
    List<Coupon> findMyCoupons(@Param("userIndex") Integer userIndex);
    
    /**
     * 만료된 쿠폰 상태를 3번(기한 경과)으로 변경
     */
    @Modifying
    @Query("UPDATE Coupon c SET c.couponProvidedStatusIndex = 3 WHERE c.providedUser.userIndex = :userIndex AND c.couponLimitTime < :now")
    void updateExpiredCoupons(@Param("userIndex") Integer userIndex, @Param("now") LocalDateTime now);
    
    /**
     * 만료된 쿠폰 조회
     */
    @Query("SELECT c FROM Coupon c WHERE c.providedUser.userIndex = :userIndex AND c.couponLimitTime < :now")
    List<Coupon> findExpiredCoupons(@Param("userIndex") Integer userIndex, @Param("now") LocalDateTime now);
} 