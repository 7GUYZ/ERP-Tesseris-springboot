package com.jakdang.labs.api.dabin.FrontCommissionManage.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import com.jakdang.labs.entity.TemporaryRegularMaster;

@Repository("userCommissionHistoryRepository")
public interface UserCommissionHistoryJdbRepo extends JpaRepository<TemporaryRegularMaster, Integer> {
    // TemporaryRegularMaster의 store_user_index의 추천인인 TemporaryRegularDetail의 user_index가 수당내역을 받음
    // 이때 TemporaryRegularDetail의 user_index가 로그인한 현재 사용자의 user_index임 
    @Query("""
        SELECT 
               us.name as userName,
               t1.temporaryStoreMasterChargeTime as chargeDate,
               t2.description as description,
               t2.temporaryRegularCashValue as commissionAmount,
               t2.paymentStatus as paymentStatus
        FROM TemporaryRegularMaster t1
        INNER JOIN TemporaryRegularDetail t2 ON t1.temporaryStoreMasterIndex = t2.temporaryRegularMasterIndex
        JOIN t1.storeUserIndex ut
        JOIN ut.usersId us
        WHERE t2.userIndex.userIndex = :userIndex
        ORDER BY t1.temporaryStoreMasterChargeTime DESC
    """)
    List<Object[]> getUserCommissionHistory(
        @Param("userIndex") Integer userIndex
    );
    
    @Query("""
        SELECT 
               us.name as userName,
               t1.temporaryStoreMasterChargeTime as chargeDate,
               t2.description as description,
               t2.temporaryRegularCashValue as commissionAmount,
               t2.paymentStatus as paymentStatus
        FROM TemporaryRegularMaster t1
        INNER JOIN TemporaryRegularDetail t2 ON t1.temporaryStoreMasterIndex = t2.temporaryRegularMasterIndex
        JOIN t1.storeUserIndex ut
        JOIN ut.usersId us
        WHERE t2.userIndex.userIndex = :userIndex
        ORDER BY t1.temporaryStoreMasterChargeTime DESC
        LIMIT :limit OFFSET :offset
    """)
    List<Object[]> getUserCommissionHistoryWithPagination(
        @Param("userIndex") Integer userIndex,
        @Param("limit") Integer limit,
        @Param("offset") Integer offset
    );
    
    @Query("""
        SELECT COUNT(*)
        FROM TemporaryRegularMaster t1
        JOIN TemporaryRegularDetail t2 ON t1.temporaryStoreMasterIndex = t2.temporaryRegularMasterIndex
        WHERE t2.userIndex.userIndex = :userIndex
    """)
    Long getTotalCount(@Param("userIndex") Integer userIndex);
} 