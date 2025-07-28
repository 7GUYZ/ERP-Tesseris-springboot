package com.jakdang.labs.api.taekjun.user_list.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.UserTesseris;

import java.util.List;

public interface UserListJtjRepo extends JpaRepository<UserTesseris, Integer> {
    @Query(value = """
        SELECT DISTINCT
            u.user_index,
            ue.name,
            ue.email,
            ue.phone,
            ue.nickname,
            u.user_birthday,
            u.user_gender_index,
            r.user_role_kor_nm AS user_role,
            b.user_bank_name,
            u.user_bank_number,
            u.user_bank_holder,
            r_ue.name AS recommender_name,
            r_ue.email AS recommender_email,
            su.join_date,
            IFNULL(cm.user_cm_deposit, 0) - IFNULL(cm.user_cm_withdrawal,0) AS cm_balance,
            ue.created_at,
            u.user_zone_code,
            u.user_address,
            u.user_detail_address
        FROM user_tesseris u
        LEFT JOIN users ue ON u.users_id = ue.id
        LEFT JOIN user_gender g ON u.user_gender_index = g.user_gender_index
        LEFT JOIN user_role r ON u.user_role_index = r.user_role_index
        LEFT JOIN user_bank b ON u.user_bank_index = b.user_bank_index
        LEFT JOIN suggestion_user su ON u.user_index = su.recommendation_user_index
        LEFT JOIN user_tesseris ru ON su.suggestion_user_index = ru.user_index
        LEFT JOIN users r_ue ON ru.users_id = r_ue.id
        LEFT JOIN user_cm cm ON u.user_index = cm.user_cm_index
        WHERE u.user_role_index <> 4 AND u.user_role_index <> 7
        AND ue.activated = 1
        ORDER BY u.user_index DESC
    """, nativeQuery = true)
    List<Object[]> findUserListRaw();
    
    @Query(value = """
        SELECT DISTINCT
            u.user_index,
            ue.name,
            ue.email,
            ue.phone,
            ue.nickname,
            u.user_birthday,
            u.user_gender_index,
            r.user_role_kor_nm AS user_role,
            b.user_bank_name,
            u.user_bank_number,
            u.user_bank_holder,
            r_ue.name AS recommender_name,
            r_ue.email AS recommender_email,
            su.join_date,
            IFNULL(cm.user_cm_deposit, 0) - IFNULL(cm.user_cm_withdrawal,0) AS cm_balance,
            ue.created_at,
            u.user_zone_code,
            u.user_address,
            u.user_detail_address
        FROM user_tesseris u
        LEFT JOIN users ue ON u.users_id = ue.id
        LEFT JOIN user_gender g ON u.user_gender_index = g.user_gender_index
        LEFT JOIN user_role r ON u.user_role_index = r.user_role_index
        LEFT JOIN user_bank b ON u.user_bank_index = b.user_bank_index
        LEFT JOIN suggestion_user su ON u.user_index = su.recommendation_user_index
        LEFT JOIN user_tesseris ru ON su.suggestion_user_index = ru.user_index
        LEFT JOIN users r_ue ON ru.users_id = r_ue.id
        LEFT JOIN user_cm cm ON u.user_index = cm.user_cm_index
        WHERE u.user_role_index <> 4 AND u.user_role_index <> 7
        AND ue.activated = 1
        AND (:id IS NULL OR ue.email LIKE CONCAT('%', :id, '%'))
        AND (:name IS NULL OR ue.name LIKE CONCAT('%', :name, '%') OR TRIM(ue.name) LIKE CONCAT('%', :name, '%'))
        AND (:phone IS NULL OR ue.phone LIKE CONCAT('%', :phone, '%'))
        AND (:userRole IS NULL OR r.user_role_kor_nm = :userRole)
        AND (:startDate IS NULL OR ue.created_at >= :startDate)
        AND (:endDate IS NULL OR ue.created_at <= :endDate)
        ORDER BY u.user_index DESC
    """, nativeQuery = true)
    List<Object[]> findUserListWithSearch(
        @Param("id") String id,
        @Param("name") String name,
        @Param("phone") String phone,
        @Param("userRole") String userRole,
        @Param("startDate") String startDate,
        @Param("endDate") String endDate
    );
} 