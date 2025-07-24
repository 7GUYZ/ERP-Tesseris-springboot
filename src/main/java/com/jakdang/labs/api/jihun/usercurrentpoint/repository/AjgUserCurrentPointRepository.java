package com.jakdang.labs.api.jihun.usercurrentpoint.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.UserCm;

@Repository
public interface AjgUserCurrentPointRepository extends JpaRepository<UserCm, Integer> {
    @Query(value = "SELECT (COALESCE(uc.user_cm_deposit, 0) - COALESCE(uc.user_cm_withdrawal, 0)) AS current_cm FROM user_tesseris ut left join user_cm uc on ut.user_index = uc.user_cm_index where ut.users_id = :userid", nativeQuery = true)
    String findByUserCmId(String userid);
}
