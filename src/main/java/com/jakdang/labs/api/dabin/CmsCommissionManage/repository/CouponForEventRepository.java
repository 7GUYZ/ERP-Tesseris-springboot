package com.jakdang.labs.api.dabin.CmsCommissionManage.repository;

import com.jakdang.labs.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.api.dabin.FrontEventCouponRegistration.dto.CouponForEventResponse;

import java.util.List;

@Repository
public interface CouponForEventRepository extends JpaRepository<Coupon, Integer> {
    
    @Query("""
        SELECT c.couponIndex, c.couponName, c.couponPrice, cis.couponIssuanceStatus,
               c.couponIssuanceTime, c.couponLimit, c.couponLimitTime, s.storeName
        FROM Coupon c
        INNER JOIN CouponIssuanceStatus cis ON c.couponIssuanceStatusIndex = cis.couponIssuanceStatusIndex
        LEFT JOIN Store s ON c.issuanceUser.userIndex = s.userIndex.userIndex
        WHERE c.issuanceUser.userIndex = :userIndex
        AND c.couponIssuanceStatusIndex = 1
        AND (:minPrice = 0 OR c.couponPrice = :minPrice)
        AND c.couponIndex NOT IN (
            SELECT ed.eventCoupon.couponIndex FROM EventDetail ed
        )
        ORDER BY c.couponIssuanceTime DESC, c.couponIndex DESC
    """)
    List<Object[]> findAvailableCouponsForEvent(
        @Param("userIndex") Long userIndex,
        @Param("minPrice") Integer minPrice
    );
} 