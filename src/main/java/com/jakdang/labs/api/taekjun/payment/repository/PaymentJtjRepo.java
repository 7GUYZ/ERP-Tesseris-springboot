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
    
    // 가맹점 목록 조회 (승인된 가맹점만 조회)
    @Query("SELECT s FROM Store s WHERE s.storeRequestStatusIndex = 2 ORDER BY s.storeName")
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
        AND c.couponProvidedStatusIndex = 1
        AND (:couponName IS NULL OR c.couponName LIKE %:couponName%)
        ORDER BY c.couponIssuanceTime DESC
    """)
    List<Coupon> findStoreCouponsForUser(@Param("userIndex") Integer userIndex, @Param("storeUserIndex") Integer storeUserIndex, @Param("couponName") String couponName);
    
    // 사용자의 CM 정보 조회
    @Query("SELECT uc FROM UserCm uc WHERE uc.userCmIndex = :userIndex")
    Optional<UserCm> findByUserCmIndex(@Param("userIndex") Integer userIndex);
    
    // 월 사용 CM 총합 조회 (사용자 구매 거래만) - user_cm_log 날짜 컬럼 사용
    @Query(value = """
        SELECT COALESCE(SUM(ABS(ucl.user_cm_log_value)), 0) 
        FROM user_cm_log ucl 
        WHERE ucl.user_index_event_trigger = :userIndex 
        AND ucl.user_cm_log_transaction_type_index = 9
        AND ucl.user_cm_log_payment_index = 2
        AND ucl.user_cm_log_value_type_index = 2
        AND ucl.user_cm_log_value < 0
        AND DATE_FORMAT(ucl.user_cm_log_create_time, '%Y-%m') = DATE_FORMAT(CURRENT_DATE, '%Y-%m')
        """, nativeQuery = true)
    Integer getMonthlyUsedCm(@Param("userIndex") Integer userIndex);
    
    // 디버깅용: 해당 사용자의 구매 로그 조회
    @Query(value = """
        SELECT user_cm_log_value, user_cm_log_transaction_type_index, user_cm_log_payment_index, 
               user_cm_log_create_time, user_cm_log_reason,
               DATE_FORMAT(user_cm_log_create_time, '%Y-%m') as log_month
        FROM user_cm_log 
        WHERE user_index_event_trigger = :userIndex 
        AND user_cm_log_transaction_type_index = 9
        AND user_cm_log_payment_index = 2
        AND user_cm_log_value_type_index = 2
        AND DATE_FORMAT(user_cm_log_create_time, '%Y-%m') = DATE_FORMAT(CURRENT_DATE, '%Y-%m')
        ORDER BY user_cm_log_create_time DESC
        """, nativeQuery = true)
    List<Object[]> getDebugLogs(@Param("userIndex") Integer userIndex);
    
    // 디버깅용: 모든 조건 제거 (실제 데이터 확인용)
    @Query(value = """
        SELECT user_cm_log_value, user_cm_log_transaction_type_index, user_cm_log_payment_index, 
               user_cm_log_value_type_index, user_cm_log_create_time, user_cm_log_reason,
               DATE_FORMAT(user_cm_log_create_time, '%Y-%m') as log_month
        FROM user_cm_log 
        WHERE user_index_event_trigger = :userIndex 
        AND DATE_FORMAT(user_cm_log_create_time, '%Y-%m') = DATE_FORMAT(CURRENT_DATE, '%Y-%m')
        ORDER BY user_cm_log_create_time DESC
        """, nativeQuery = true)
    List<Object[]> getAllUserLogs(@Param("userIndex") Integer userIndex);
} 