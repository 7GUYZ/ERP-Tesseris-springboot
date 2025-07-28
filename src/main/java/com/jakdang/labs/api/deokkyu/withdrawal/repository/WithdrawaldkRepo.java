package com.jakdang.labs.api.deokkyu.withdrawal.repository;

import com.jakdang.labs.entity.UserCmLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WithdrawaldkRepo extends JpaRepository<UserCmLog, Integer> {
    
    /**
     * 출금 거래 조회 (user_cm_log_payment_index = 2)
     * 시작일과 종료일 사이의 출금 거래를 조회
     * user_index_event_party가 null이 아닌 경우만 조회
     */
    @Query("SELECT ucl FROM UserCmLog ucl " +
           "LEFT JOIN FETCH ucl.userIndexEventParty ut " +
           "LEFT JOIN FETCH ut.usersId ue " +
           "LEFT JOIN FETCH ut.userBank ub " +
           "WHERE ucl.userCmLogPaymentIndex = 2 " +
           "AND ucl.userIndexEventParty IS NOT NULL " +
           "AND ucl.userCmLogCreateTime BETWEEN :startDate AND :endDate " +
           "ORDER BY ucl.userCmLogCreateTime DESC")
    List<UserCmLog> findWithdrawalsByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
} 