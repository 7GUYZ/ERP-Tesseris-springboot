package com.jakdang.labs.api.jungeun.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.BusinessMan;

@Repository
public interface BusinessManLjeRepo extends JpaRepository<BusinessMan, Integer> {

    @Query("SELECT bm.businessGrade.businessGradeIndex FROM BusinessMan bm WHERE bm.userIndex.userIndex = :user_index")
    Integer findBusinessGradeIndexByUserIndex(@Param("user_index") Integer user_index);

    @Query("SELECT bg.businessGradeIndex, bg.businessGradeName FROM BusinessGrade bg WHERE bg.businessGradeIndex > :myGradeIndex ORDER BY bg.businessGradeIndex ASC")
    List<Object[]> findGradeIndexAndNameGreaterThan(@Param("myGradeIndex") Integer myGradeIndex);

    @Query(value = "SELECT u.email, u.name, bg.business_grade_name, boss_u.email as boss_email, " +
            "COALESCE(MAX(cm.total_cm), 0) as total_cm, COALESCE(MAX(s.store_count), 0) as store_count " +
            "FROM business_man bm " +
            "JOIN user_tesseris ut ON bm.user_index = ut.user_index " +
            "JOIN users u ON ut.users_id = u.id " +
            "LEFT JOIN business_grade bg ON bm.business_grade_index = bg.business_grade_index " +
            "LEFT JOIN business_man boss_bm ON bm.boss_user_index = boss_bm.user_index " +
            "LEFT JOIN user_tesseris boss_ut ON boss_bm.user_index = boss_ut.user_index " +
            "LEFT JOIN users boss_u ON boss_ut.users_id = boss_u.id " +
            "LEFT JOIN (   SELECT user_index_event_trigger, SUM(user_cm_log_value) AS total_cm   FROM user_cm_log   WHERE user_cm_log_transaction_type_index = 1   GROUP BY user_index_event_trigger ) cm ON cm.user_index_event_trigger = ut.user_index "
            +
            "LEFT JOIN (   SELECT business_man_user_index, COUNT(store_index) AS store_count   FROM store   WHERE store_request_status_index = 2   GROUP BY business_man_user_index ) s ON s.business_man_user_index = bm.business_man_index "
            +
            "WHERE bm.business_grade_index = :business_grade_index " +
            "GROUP BY u.email, u.name, bg.business_grade_name, boss_u.email", nativeQuery = true)
    List<Object[]> findBusinessListWithBossEmail(@Param("business_grade_index") Integer business_grade_index);
}
