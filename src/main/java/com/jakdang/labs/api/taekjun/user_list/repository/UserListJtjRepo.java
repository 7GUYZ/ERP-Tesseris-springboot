package com.jakdang.labs.api.taekjun.user_list.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.UserTesseris;

import java.util.List;

public interface UserListJtjRepo extends JpaRepository<UserTesseris, Integer> {
    @Query(value = """
        SELECT
            u.user_index,
            ue.name,
            ue.email,
            ue.phone,
            ue.nickname,
            u.user_birthday,
            g.user_gender_name,
            b.user_bank_name,
            u.user_bank_number,
            u.user_bank_holder,
            s.store_name,
            r_ue.name AS recommender_name,
            su.recommendation_user_index AS recommender_id,
            su.join_date,
            IFNULL(cm.user_cm_deposit,0) - IFNULL(cm.user_cm_withdrawal,0) AS cm_balance,
            u.created_at
        FROM user_tesseris u
        LEFT JOIN users ue ON u.users_id = ue.id
        LEFT JOIN user_gender g ON u.user_gender_index = g.user_gender_index
        LEFT JOIN user_bank b ON u.user_bank_index = b.user_bank_index
        LEFT JOIN store s ON u.user_index = s.user_index
        LEFT JOIN suggestion_user su ON u.user_index = su.suggestion_user_index
        LEFT JOIN user_tesseris ru ON su.recommendation_user_index = ru.user_index
        LEFT JOIN users r_ue ON ru.users_id = r_ue.id
        LEFT JOIN user_cm cm ON u.user_index = cm.user_cm_index
        WHERE u.user_role_index <> 4
        ORDER BY u.user_index DESC
    """, nativeQuery = true)
    List<Object[]> findUserListRaw();
} 