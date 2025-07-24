package com.jakdang.labs.api.jihun.memberassetdetails.repository;

import com.jakdang.labs.entity.UserCm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AjgUserCmRepository extends JpaRepository<UserCm, Integer> {
    // user_cm_index(PK)로 조회 및 저장
} 