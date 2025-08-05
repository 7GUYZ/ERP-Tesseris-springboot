package com.jakdang.labs.api.taekjun.user_log.repository;

import com.jakdang.labs.entity.UserCmLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 사용자 CM 사용 내역 조회를 위한 Repository
 * 
 * 주요 기능:
 * 1. 사용자별 CM 로그 조회
 * 2. 페이징 처리
 * 3. 월별 필터링
 * 4. 거래 타입별 필터링
 */
@Repository
public interface UserLogJtjRepo extends JpaRepository<UserCmLog, Integer> {

    /**
     * 사용자별 CM 로그 조회 (페이징)
     * 
     * @param userIndex 사용자 인덱스
     * @param pageable 페이징 정보
     * @return 페이징된 CM 로그 목록
     */
    @Query(value = """
        SELECT ucl FROM UserCmLog ucl 
        WHERE ((ucl.userIndexEventTrigger IS NOT NULL AND ucl.userIndexEventTrigger.userIndex = :userIndex)
        OR (ucl.userIndexEventParty IS NOT NULL AND ucl.userIndexEventParty.userIndex = :userIndex))
        AND ucl.userCmLogValueTypeIndex = 2
        ORDER BY ucl.userCmLogCreateTime DESC
        """)
    Page<UserCmLog> findByUserIndex(@Param("userIndex") Integer userIndex, Pageable pageable);

    /**
     * 사용자별 CM 로그 조회 (디버깅용 - 모든 로그)
     */
    @Query(value = """
        SELECT ucl FROM UserCmLog ucl 
        WHERE ucl.userCmLogValueTypeIndex = 2
        ORDER BY ucl.userCmLogCreateTime DESC
        """)
    Page<UserCmLog> findAllLogs(Pageable pageable);

    /**
     * 사용자별 월별 CM 로그 조회
     * 
     * @param userIndex 사용자 인덱스
     * @param year 년도
     * @param month 월
     * @param pageable 페이징 정보
     * @return 해당 월의 CM 로그 목록
     */
    @Query(value = """
        SELECT ucl FROM UserCmLog ucl 
        WHERE ((ucl.userIndexEventTrigger IS NOT NULL AND ucl.userIndexEventTrigger.userIndex = :userIndex)
        OR (ucl.userIndexEventParty IS NOT NULL AND ucl.userIndexEventParty.userIndex = :userIndex))
        AND ucl.userCmLogValueTypeIndex = 2
        AND YEAR(ucl.userCmLogCreateTime) = :year 
        AND MONTH(ucl.userCmLogCreateTime) = :month
        ORDER BY ucl.userCmLogCreateTime DESC
        """)
    Page<UserCmLog> findByUserIndexAndMonth(@Param("userIndex") Integer userIndex, 
                                           @Param("year") int year, 
                                           @Param("month") int month,
                                           Pageable pageable);

    /**
     * 사용자별 월별 CM 로그 조회 (페이징 없음)
     * 
     * @param userIndex 사용자 인덱스
     * @param year 년도
     * @param month 월
     * @return 해당 월의 CM 로그 목록
     */
    @Query(value = """
        SELECT ucl FROM UserCmLog ucl 
        WHERE ((ucl.userIndexEventTrigger IS NOT NULL AND ucl.userIndexEventTrigger.userIndex = :userIndex)
        OR (ucl.userIndexEventParty IS NOT NULL AND ucl.userIndexEventParty.userIndex = :userIndex))
        AND ucl.userCmLogValueTypeIndex = 2
        AND YEAR(ucl.userCmLogCreateTime) = :year 
        AND MONTH(ucl.userCmLogCreateTime) = :month
        ORDER BY ucl.userCmLogCreateTime DESC
        """)
    List<UserCmLog> findByUserIndexAndMonth(@Param("userIndex") Integer userIndex, 
                                           @Param("year") int year, 
                                           @Param("month") int month);

    /**
     * 사용자별 거래 타입별 CM 로그 조회 (페이징)
     * 
     * @param userIndex 사용자 인덱스
     * @param transactionType 거래 타입
     * @param pageable 페이징 정보
     * @return 거래 타입별 CM 로그 목록
     */
    @Query(value = """
        SELECT ucl FROM UserCmLog ucl 
        WHERE ((ucl.userIndexEventTrigger IS NOT NULL AND ucl.userIndexEventTrigger.userIndex = :userIndex)
        OR (ucl.userIndexEventParty IS NOT NULL AND ucl.userIndexEventParty.userIndex = :userIndex))
        AND ucl.userCmLogValueTypeIndex = 2
        AND ucl.userCmLogTransactionTypeIndex = :transactionType
        ORDER BY ucl.userCmLogCreateTime DESC
        """)
    Page<UserCmLog> findByUserIndexAndTransactionType(@Param("userIndex") Integer userIndex,
                                                      @Param("transactionType") Integer transactionType,
                                                      Pageable pageable);

    /**
     * 내가 쓴 금액 조회 (user_index_event_trigger가 내 IDX이고 음수인 경우)
     * 
     * @param userIndex 사용자 인덱스
     * @param pageable 페이징 정보
     * @return 내가 쓴 금액 로그 목록
     */
    @Query(value = """
        SELECT ucl FROM UserCmLog ucl 
        WHERE ucl.userIndexEventTrigger IS NOT NULL 
        AND ucl.userIndexEventTrigger.userIndex = :userIndex
        AND ucl.userCmLogValueTypeIndex = 2
        AND ucl.userCmLogValue < 0
        ORDER BY ucl.userCmLogCreateTime DESC
        """)
    Page<UserCmLog> findSpentLogs(@Param("userIndex") Integer userIndex, Pageable pageable);

    /**
     * 내가 받은 금액 조회 (user_index_event_party가 내 IDX이고 양수인 경우)
     * 
     * @param userIndex 사용자 인덱스
     * @param pageable 페이징 정보
     * @return 내가 받은 금액 로그 목록
     */
    @Query(value = """
        SELECT ucl FROM UserCmLog ucl 
        WHERE ucl.userIndexEventParty IS NOT NULL 
        AND ucl.userIndexEventParty.userIndex = :userIndex
        AND ucl.userCmLogValueTypeIndex = 2
        AND ucl.userCmLogValue > 0
        ORDER BY ucl.userCmLogCreateTime DESC
        """)
    Page<UserCmLog> findReceivedLogs(@Param("userIndex") Integer userIndex, Pageable pageable);

    /**
     * 돈이 들어오는 거래 조회 (수입) - 양수 값
     * 
     * @param userIndex 사용자 인덱스
     * @param pageable 페이징 정보
     * @return 수입 거래 목록
     */
    @Query(value = """
        SELECT ucl FROM UserCmLog ucl 
        WHERE ((ucl.userIndexEventTrigger IS NOT NULL AND ucl.userIndexEventTrigger.userIndex = :userIndex)
        OR (ucl.userIndexEventParty IS NOT NULL AND ucl.userIndexEventParty.userIndex = :userIndex))
        AND ucl.userCmLogValueTypeIndex = 2
        AND ucl.userCmLogValue > 0
        ORDER BY ucl.userCmLogCreateTime DESC
        """)
    Page<UserCmLog> findIncomeLogs(@Param("userIndex") Integer userIndex, Pageable pageable);

    /**
     * 돈이 빠져나가는 거래 조회 (지출) - 음수 값
     * 
     * @param userIndex 사용자 인덱스
     * @param pageable 페이징 정보
     * @return 지출 거래 목록
     */
    @Query(value = """
        SELECT ucl FROM UserCmLog ucl 
        WHERE ((ucl.userIndexEventTrigger IS NOT NULL AND ucl.userIndexEventTrigger.userIndex = :userIndex)
        OR (ucl.userIndexEventParty IS NOT NULL AND ucl.userIndexEventParty.userIndex = :userIndex))
        AND ucl.userCmLogValueTypeIndex = 2
        AND ucl.userCmLogValue < 0
        ORDER BY ucl.userCmLogCreateTime DESC
        """)
    Page<UserCmLog> findExpenseLogs(@Param("userIndex") Integer userIndex, Pageable pageable);

    /**
     * 사용자별 CM 사용 통계 조회
     * 
     * @param userIndex 사용자 인덱스
     * @return CM 사용 통계 정보
     */
    @Query(value = """
        SELECT 
            COUNT(ucl) as totalTransactions,
            SUM(CASE WHEN ucl.userCmLogValue < 0 THEN ABS(ucl.userCmLogValue) ELSE 0 END) as totalSpent,
            SUM(CASE WHEN ucl.userCmLogValue > 0 THEN ucl.userCmLogValue ELSE 0 END) as totalReceived,
            SUM(CASE WHEN ucl.userCouponValue IS NOT NULL THEN ucl.userCouponValue ELSE 0 END) as totalCouponUsed
        FROM UserCmLog ucl 
        WHERE ((ucl.userIndexEventTrigger IS NOT NULL AND ucl.userIndexEventTrigger.userIndex = :userIndex)
        OR (ucl.userIndexEventParty IS NOT NULL AND ucl.userIndexEventParty.userIndex = :userIndex))
        AND ucl.userCmLogValueTypeIndex = 2
        """)
    Object[] getUserLogStatistics(@Param("userIndex") Integer userIndex);

    /**
     * 사용자별 월별 CM 사용 금액 조회
     * 
     * @param userIndex 사용자 인덱스
     * @param year 년도
     * @param month 월
     * @return 해당 월의 CM 사용 금액
     */
    @Query(value = """
        SELECT COALESCE(SUM(ABS(ucl.userCmLogValue)), 0) 
        FROM UserCmLog ucl 
        WHERE ucl.userIndexEventTrigger.userIndex = :userIndex 
        AND ucl.userCmLogTransactionTypeIndex = 9
        AND ucl.userCmLogPaymentIndex = 2
        AND ucl.userCmLogValueTypeIndex = 2
        AND ucl.userCmLogValue < 0
        AND YEAR(ucl.userCmLogCreateTime) = :year 
        AND MONTH(ucl.userCmLogCreateTime) = :month
        """)
    Integer getMonthlyUsedAmount(@Param("userIndex") Integer userIndex,
                                @Param("year") int year,
                                @Param("month") int month);

    /**
     * 사용자별 월별 지출 로그 조회 (페이징)
     * 
     * @param userIndex 사용자 인덱스
     * @param year 년도
     * @param month 월
     * @param pageable 페이징 정보
     * @return 해당 월의 지출 로그 목록
     */
    @Query(value = """
        SELECT ucl FROM UserCmLog ucl 
        WHERE ((ucl.userIndexEventTrigger IS NOT NULL AND ucl.userIndexEventTrigger.userIndex = :userIndex)
        OR (ucl.userIndexEventParty IS NOT NULL AND ucl.userIndexEventParty.userIndex = :userIndex))
        AND ucl.userCmLogValueTypeIndex = 2
        AND ucl.userCmLogValue < 0
        AND YEAR(ucl.userCmLogCreateTime) = :year 
        AND MONTH(ucl.userCmLogCreateTime) = :month
        ORDER BY ucl.userCmLogCreateTime DESC
        """)
    Page<UserCmLog> findSpentLogsByMonth(@Param("userIndex") Integer userIndex,
                                         @Param("year") int year,
                                         @Param("month") int month,
                                         Pageable pageable);

    /**
     * 사용자별 월별 수입 로그 조회 (페이징)
     * 
     * @param userIndex 사용자 인덱스
     * @param year 년도
     * @param month 월
     * @param pageable 페이징 정보
     * @return 해당 월의 수입 로그 목록
     */
    @Query(value = """
        SELECT ucl FROM UserCmLog ucl 
        WHERE ((ucl.userIndexEventTrigger IS NOT NULL AND ucl.userIndexEventTrigger.userIndex = :userIndex)
        OR (ucl.userIndexEventParty IS NOT NULL AND ucl.userIndexEventParty.userIndex = :userIndex))
        AND ucl.userCmLogValueTypeIndex = 2
        AND ucl.userCmLogValue > 0
        AND YEAR(ucl.userCmLogCreateTime) = :year 
        AND MONTH(ucl.userCmLogCreateTime) = :month
        ORDER BY ucl.userCmLogCreateTime DESC
        """)
    Page<UserCmLog> findReceivedLogsByMonth(@Param("userIndex") Integer userIndex,
                                            @Param("year") int year,
                                            @Param("month") int month,
                                            Pageable pageable);
} 