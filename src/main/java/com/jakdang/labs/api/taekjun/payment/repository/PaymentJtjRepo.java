package com.jakdang.labs.api.taekjun.payment.repository;

import com.jakdang.labs.entity.Store;
import com.jakdang.labs.entity.Coupon;
import com.jakdang.labs.entity.UserCm;
import com.jakdang.labs.entity.UserCmLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentJtjRepo extends JpaRepository<Store, Integer> {
    
    // 가맹점 목록 조회 (모든 가맹점 조회)
    @Query("SELECT s FROM Store s ORDER BY s.storeName")
    List<Store> findAllActiveStores();
    
    // 사용자의 쿠폰 목록 조회
    @Query("""
        SELECT c FROM Coupon c 
        WHERE c.providedUser.userIndex = :userIndex 
        AND c.couponProvidedStatusIndex = 1
        AND (:couponName IS NULL OR c.couponName LIKE %:couponName%)
        ORDER BY c.couponIssuanceTime DESC
    """)
    List<Coupon> findUserCoupons(@Param("userIndex") Integer userIndex, @Param("couponName") String couponName);
    
    // 특정 가맹점이 사용자에게 준 쿠폰 목록 조회
    @Query("""
        SELECT c FROM Coupon c 
        WHERE c.providedUser.userIndex = :userIndex 
        AND c.issuanceUser.userIndex = :storeUserIndex
        AND c.couponProvidedStatusIndex IN (1, 2, 3)
        AND (:couponName IS NULL OR c.couponName LIKE %:couponName%)
        ORDER BY c.couponIssuanceTime DESC
    """)
    List<Coupon> findStoreCouponsForUser(@Param("userIndex") Integer userIndex, @Param("storeUserIndex") Integer storeUserIndex, @Param("couponName") String couponName);
    
    // 사용자의 CM 정보 조회
    @Query("SELECT uc FROM UserCm uc WHERE uc.userCmIndex = :userIndex")
    Optional<UserCm> findByUserCmIndex(@Param("userIndex") Integer userIndex);
    
    // 월 사용 CM 총합 조회
    @Query("""
        SELECT COALESCE(SUM(ucl.userCmLogValue), 0) 
        FROM UserCmLog ucl 
        WHERE ucl.userIndexEventParty = :userIndex 
        AND ucl.userCmLogTransactionTypeIndex IN (1, 2, 3)
        AND FUNCTION('DATE_FORMAT', ucl.userCmLogCreateTime, '%Y-%m') = FUNCTION('DATE_FORMAT', CURRENT_DATE, '%Y-%m')
    """)
    Integer getMonthlyUsedCm(@Param("userIndex") Integer userIndex);
} 