package com.jakdang.labs.api.taekjun.dashdord.repository;

import com.jakdang.labs.entity.UserCmLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface UserCmLogJtjRepo extends JpaRepository<UserCmLog, Integer> {
    // CM 관련 쿼리
    @Query(value = "SELECT COALESCE(SUM(t1.user_cm_log_value), 0) FROM user_cm_log t1 " +
            "INNER JOIN user_cm_log_transaction_type t2 ON t1.user_cm_log_transaction_type_index = t2.user_cm_log_transaction_type_index " +
            "WHERE t2.user_cm_log_transaction_type_name IN ('충전(CM)', '충전 취소(CM)')", nativeQuery = true)
    Long getChargedCmTotal();

    @Query(value = "SELECT COALESCE(SUM(t1.user_cm_log_value), 0) FROM user_cm_log t1 " +
            "INNER JOIN user_cm_log_transaction_type t2 ON t1.user_cm_log_transaction_type_index = t2.user_cm_log_transaction_type_index " +
            "WHERE t2.user_cm_log_transaction_type_name IN ('충전(CM)', '충전 취소(CM)') " +
            "AND DATE(t1.user_cm_log_create_time) = :date", nativeQuery = true)
    Long getChargedCmByDate(@Param("date") String date);

    @Query(value = "SELECT COALESCE(SUM(t1.user_cm_log_value), 0) FROM user_cm_log t1 " +
            "INNER JOIN user_cm_log_transaction_type t2 ON t1.user_cm_log_transaction_type_index = t2.user_cm_log_transaction_type_index " +
            "WHERE t2.user_cm_log_transaction_type_name = '본사지급(CM)'", nativeQuery = true)
    Long getCompanyPaidCmTotal();

    @Query(value = "SELECT COALESCE(SUM(t1.user_cm_log_value), 0) FROM user_cm_log t1 " +
            "INNER JOIN user_cm_log_transaction_type t2 ON t1.user_cm_log_transaction_type_index = t2.user_cm_log_transaction_type_index " +
            "WHERE t2.user_cm_log_transaction_type_name = '본사지급(CM)' " +
            "AND DATE(t1.user_cm_log_create_time) = :date", nativeQuery = true)
    Long getCompanyPaidCmByDate(@Param("date") String date);

    @Query(value = "SELECT COALESCE(SUM(t1.user_cm_log_value) * -1, 0) FROM user_cm_log t1 " +
            "INNER JOIN user_cm_log_transaction_type t2 ON t1.user_cm_log_transaction_type_index = t2.user_cm_log_transaction_type_index " +
            "WHERE t2.user_cm_log_transaction_type_name = '본사회수(CM)'", nativeQuery = true)
    Long getCompanyCollectedCmTotal();

    @Query(value = "SELECT COALESCE(SUM(t1.user_cm_log_value) * -1, 0) FROM user_cm_log t1 " +
            "INNER JOIN user_cm_log_transaction_type t2 ON t1.user_cm_log_transaction_type_index = t2.user_cm_log_transaction_type_index " +
            "WHERE t2.user_cm_log_transaction_type_name = '본사회수(CM)' " +
            "AND DATE(t1.user_cm_log_create_time) = :date", nativeQuery = true)
    Long getCompanyCollectedCmByDate(@Param("date") String date);

    // 수수료 관련 쿼리
    @Query(value = "SELECT COALESCE(SUM(t1.user_cm_log_value), 0) FROM user_cm_log t1 " +
            "INNER JOIN user_cm_log_transaction_type t2 ON t1.user_cm_log_transaction_type_index = t2.user_cm_log_transaction_type_index " +
            "INNER JOIN user_cm_log_value_type t3 ON t1.user_cm_log_value_type_index = t3.user_cm_log_value_type_index " +
            "WHERE t2.user_cm_log_transaction_type_name = '중개수수료' " +
            "AND t3.user_cm_log_value_type_name = 'Cash' " +
            "AND t1.user_index_event_party <> 1 " +
            "AND t1.user_cm_log_value >= 0", nativeQuery = true)
    Long getBusinessCashCommissionTotal();

    @Query(value = "SELECT COALESCE(SUM(t1.user_cm_log_value), 0) FROM user_cm_log t1 " +
            "INNER JOIN user_cm_log_transaction_type t2 ON t1.user_cm_log_transaction_type_index = t2.user_cm_log_transaction_type_index " +
            "INNER JOIN user_cm_log_value_type t3 ON t1.user_cm_log_value_type_index = t3.user_cm_log_value_type_index " +
            "WHERE t2.user_cm_log_transaction_type_name = '중개수수료' " +
            "AND t3.user_cm_log_value_type_name = 'Cash' " +
            "AND t1.user_index_event_party <> 1 " +
            "AND t1.user_cm_log_value >= 0 " +
            "AND DATE(t1.user_cm_log_create_time) = :date", nativeQuery = true)
    Long getBusinessCashCommissionByDate(@Param("date") String date);

    @Query(value = "SELECT COALESCE(SUM(t1.user_cm_log_value), 0) FROM user_cm_log t1 " +
            "INNER JOIN user_cm_log_transaction_type t2 ON t1.user_cm_log_transaction_type_index = t2.user_cm_log_transaction_type_index " +
            "INNER JOIN user_cm_log_value_type t3 ON t1.user_cm_log_value_type_index = t3.user_cm_log_value_type_index " +
            "WHERE t2.user_cm_log_transaction_type_name = '중개수수료' " +
            "AND t3.user_cm_log_value_type_name = 'CM' " +
            "AND t1.user_index_event_party <> 1 " +
            "AND t1.user_cm_log_value >= 0", nativeQuery = true)
    Long getBusinessCmCommissionTotal();

    @Query(value = "SELECT COALESCE(SUM(t1.user_cm_log_value), 0) FROM user_cm_log t1 " +
            "INNER JOIN user_cm_log_transaction_type t2 ON t1.user_cm_log_transaction_type_index = t2.user_cm_log_transaction_type_index " +
            "INNER JOIN user_cm_log_value_type t3 ON t1.user_cm_log_value_type_index = t3.user_cm_log_value_type_index " +
            "WHERE t2.user_cm_log_transaction_type_name = '중개수수료' " +
            "AND t3.user_cm_log_value_type_name = 'CM' " +
            "AND t1.user_index_event_party <> 1 " +
            "AND t1.user_cm_log_value >= 0 " +
            "AND DATE(t1.user_cm_log_create_time) = :date", nativeQuery = true)
    Long getBusinessCmCommissionByDate(@Param("date") String date);

    @Query(value = "SELECT COALESCE(SUM(t1.user_cm_log_value), 0) FROM user_cm_log t1 " +
            "INNER JOIN user_cm_log_transaction_type t2 ON t1.user_cm_log_transaction_type_index = t2.user_cm_log_transaction_type_index " +
            "INNER JOIN user_cm_log_value_type t3 ON t1.user_cm_log_value_type_index = t3.user_cm_log_value_type_index " +
            "WHERE t2.user_cm_log_transaction_type_name = '중개수수료' " +
            "AND t3.user_cm_log_value_type_name = 'Cash' " +
            "AND t1.user_index_event_party = 1", nativeQuery = true)
    Long getCompanyCashCommissionTotal();

    @Query(value = "SELECT COALESCE(SUM(t1.user_cm_log_value), 0) FROM user_cm_log t1 " +
            "INNER JOIN user_cm_log_transaction_type t2 ON t1.user_cm_log_transaction_type_index = t2.user_cm_log_transaction_type_index " +
            "INNER JOIN user_cm_log_value_type t3 ON t1.user_cm_log_value_type_index = t3.user_cm_log_value_type_index " +
            "WHERE t2.user_cm_log_transaction_type_name = '중개수수료' " +
            "AND t3.user_cm_log_value_type_name = 'Cash' " +
            "AND t1.user_index_event_party = 1 " +
            "AND DATE(t1.user_cm_log_create_time) = :date", nativeQuery = true)
    Long getCompanyCashCommissionByDate(@Param("date") String date);

    @Query(value = "SELECT COALESCE(SUM(t1.user_cm_log_value), 0) FROM user_cm_log t1 " +
            "INNER JOIN user_cm_log_transaction_type t2 ON t1.user_cm_log_transaction_type_index = t2.user_cm_log_transaction_type_index " +
            "INNER JOIN user_cm_log_value_type t3 ON t1.user_cm_log_value_type_index = t3.user_cm_log_value_type_index " +
            "WHERE t2.user_cm_log_transaction_type_name = '중개수수료' " +
            "AND t3.user_cm_log_value_type_name = 'CM' " +
            "AND t1.user_index_event_party <> 1", nativeQuery = true)
    Long getCompanyCmCashTotal();

    @Query(value = "SELECT COALESCE(SUM(t1.user_cm_log_value), 0) FROM user_cm_log t1 " +
            "INNER JOIN user_cm_log_transaction_type t2 ON t1.user_cm_log_transaction_type_index = t2.user_cm_log_transaction_type_index " +
            "INNER JOIN user_cm_log_value_type t3 ON t1.user_cm_log_value_type_index = t3.user_cm_log_value_type_index " +
            "WHERE t2.user_cm_log_transaction_type_name = '중개수수료' " +
            "AND t3.user_cm_log_value_type_name = 'CM' " +
            "AND t1.user_index_event_party <> 1 " +
            "AND DATE(t1.user_cm_log_create_time) = :date", nativeQuery = true)
    Long getCompanyCmCashByDate(@Param("date") String date);

    // 기타 쿼리
    @Query(value = "SELECT COALESCE(SUM(t1.user_cm_log_value), 0) FROM user_cm_log t1 " +
            "INNER JOIN user_cm_log_transaction_type t2 ON t1.user_cm_log_transaction_type_index = t2.user_cm_log_transaction_type_index " +
            "WHERE t2.user_cm_log_transaction_type_name = '선물' " +
            "AND t1.user_cm_log_value > 0", nativeQuery = true)
    Long getGiftCmTotal();

    @Query(value = "SELECT COALESCE(SUM(t1.user_cm_log_value), 0) FROM user_cm_log t1 " +
            "INNER JOIN user_cm_log_transaction_type t2 ON t1.user_cm_log_transaction_type_index = t2.user_cm_log_transaction_type_index " +
            "WHERE t2.user_cm_log_transaction_type_name = '선물' " +
            "AND t1.user_cm_log_value > 0 " +
            "AND DATE(t1.user_cm_log_create_time) = :date", nativeQuery = true)
    Long getGiftCmByDate(@Param("date") String date);

    @Query(value = "SELECT COALESCE(SUM(t1.user_cm_log_value), 0) FROM user_cm_log t1 " +
            "INNER JOIN user_cm_log_transaction_type t2 ON t1.user_cm_log_transaction_type_index = t2.user_cm_log_transaction_type_index " +
            "INNER JOIN user_cm_log_value_type t3 ON t1.user_cm_log_value_type_index = t3.user_cm_log_value_type_index " +
            "WHERE t2.user_cm_log_transaction_type_name = '중개수수료' " +
            "AND t3.user_cm_log_value_type_name = 'Cash' " +
            "AND t1.user_index_event_party <> 1", nativeQuery = true)
    Long getWithdrawalCompletedTotal();

    @Query(value = "SELECT COALESCE(SUM(t1.user_cm_log_value), 0) FROM user_cm_log t1 " +
            "INNER JOIN user_cm_log_transaction_type t2 ON t1.user_cm_log_transaction_type_index = t2.user_cm_log_transaction_type_index " +
            "INNER JOIN user_cm_log_value_type t3 ON t1.user_cm_log_value_type_index = t3.user_cm_log_value_type_index " +
            "WHERE t2.user_cm_log_transaction_type_name = '중개수수료' " +
            "AND t3.user_cm_log_value_type_name = 'Cash' " +
            "AND t1.user_index_event_party <> 1 " +
            "AND DATE(t1.user_cm_log_create_time) = :date", nativeQuery = true)
    Long getWithdrawalCompletedByDate(@Param("date") String date);
} 