package com.jakdang.labs.api.dabin.FrontEventCouponList.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.jakdang.labs.entity.EventMaster;

import java.util.List;

@Repository
public interface EventListRepository extends JpaRepository<EventMaster, Integer> {
    
    @Query("""
        SELECT t1.eventMasterIndex, t1.eventMasterName, t1.eventMasterCondition,
               SUM(t3.couponPrice) as totalCouponPrice, t4.storeAddress, t4.storeName,
               t1.eventMasterCount
        FROM EventMaster t1
        INNER JOIN EventDetail t2 ON t1.eventMasterIndex = t2.eventMaster.eventMasterIndex
        INNER JOIN Coupon t3 ON t2.eventCoupon.couponIndex = t3.couponIndex 
        INNER JOIN Store t4 ON t1.eventMasterUserIndex = t4.userIndex.userIndex
        WHERE t1.eventMasterCount > 0 AND t3.couponIssuanceStatusIndex = 1
        GROUP BY t1.eventMasterIndex, t1.eventMasterName, t1.eventMasterCondition, 
                 t4.storeAddress, t4.storeName, t1.eventMasterCount
        ORDER BY t1.eventMasterIndex DESC
    """)
    List<Object[]> findActiveEvents();
    
    @Query("""
        SELECT t1.eventMasterIndex, t1.eventMasterName, t1.eventMasterCondition,
               0 as totalCouponPrice, t4.storeAddress, t4.storeName,
               t1.eventMasterCount
        FROM EventMaster t1
        INNER JOIN EventDetail t2 ON t1.eventMasterIndex = t2.eventMaster.eventMasterIndex
        INNER JOIN Coupon t3 ON t2.eventCoupon.couponIndex = t3.couponIndex
        INNER JOIN Store t4 ON t1.eventMasterUserIndex = t4.userIndex.userIndex
        WHERE t1.eventMasterCount = 0
        GROUP BY t1.eventMasterIndex, t1.eventMasterName, t1.eventMasterCondition, 
                 t4.storeAddress, t4.storeName, t1.eventMasterCount
        ORDER BY t1.eventMasterIndex DESC
    """)
    List<Object[]> findEndedEvents();
} 