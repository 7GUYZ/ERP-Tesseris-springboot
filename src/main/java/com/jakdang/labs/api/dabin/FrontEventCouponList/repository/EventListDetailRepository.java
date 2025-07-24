package com.jakdang.labs.api.dabin.FrontEventCouponList.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.jakdang.labs.entity.Store;

import java.util.List;

@Repository
public interface EventListDetailRepository extends JpaRepository<Store, Integer> {
    
    @Query("""
        SELECT DISTINCT t1.storeIndex, t1.storeName, t1.storePhone, t1.storeAddress,
               t1.storeCategory.storeCategoryName, (t3.userCmpInit * 2) - (t3.userCmpDeposit - t3.userCmpWithdrawal),
               CONCAT(TRUNCATE(((t3.userCmpInit * 2) - (t3.userCmpDeposit + t3.userCmpWithdrawal)) / 10000, 0), '만') as userCmUse,
               t5.storeImage, 
               CASE 
                   WHEN t4.storeBusinessDate IS NOT NULL THEN 
                       CASE WHEN FUNCTION('TIME_FORMAT', FUNCTION('NOW'), '%H:%i') BETWEEN t4.storeStartBusinessHour AND t4.storeEndBusinessHour THEN '1' ELSE '0' END
                   ELSE '2' 
               END as storeBusinessState,
               t1.storeTransactionStatus
        FROM Store t1
        INNER JOIN UserCm t3 ON t1.userIndex.userIndex = t3.userCmIndex
        LEFT JOIN StoreBusinessHours t4 ON t1.storeIndex = t4.storeUserIndex.storeIndex 
        AND t4.storeBusinessDate LIKE CONCAT('%', LOWER(FUNCTION('DAYNAME', FUNCTION('NOW'))), '%')
        LEFT JOIN StoreImage t5 ON t1.storeIndex = t5.storeUserIndex.storeIndex AND t5.storeMainImageStatus = 'T'
        INNER JOIN UserTesseris t6 ON t1.userIndex.userIndex = t6.userIndex
        WHERE t6.userRoleIndex = 3 AND t1.storeRequestStatusIndex = 2
        AND t1.userIndex.userIndex = :eventMasterUserIndex
    """)
    List<Object[]> findStoreInfoByEventMasterUserIndex(@Param("eventMasterUserIndex") Integer eventMasterUserIndex);
    
    @Query("""
        SELECT t1.couponIndex, t1.couponName, t1.couponPrice, t2.couponIssuanceStatus,
               t1.couponIssuanceTime, t1.couponLimit, t1.couponLimitTime
        FROM Coupon t1
        INNER JOIN CouponIssuanceStatus t2 ON t1.couponIssuanceStatusIndex = t2.couponIssuanceStatusIndex
        WHERE t1.issuanceUser.userIndex = :eventMasterUserIndex
        AND t1.couponIssuanceStatusIndex = 1
        AND t1.couponIndex IN (
            SELECT ed.eventCoupon.couponIndex 
            FROM EventDetail ed 
            WHERE ed.eventMaster.eventMasterIndex = :eventMasterIndex
        )
        ORDER BY t1.couponIssuanceTime DESC, t1.couponIndex DESC
    """)
    List<Object[]> findEventCoupons(@Param("eventMasterUserIndex") Integer eventMasterUserIndex, 
                                   @Param("eventMasterIndex") Integer eventMasterIndex);
} 