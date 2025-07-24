package com.jakdang.labs.api.deokkyu.businessman.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.BusinessMan;
import com.jakdang.labs.entity.UserTesseris;

import java.util.List;

@Repository
public interface BusinessManBusinessmanhdkRepo extends JpaRepository<BusinessMan, Integer> {
    
    // boss_user_index로 하위 사업자들 조회
    List<BusinessMan> findByBossUserIndex(Integer bossUserIndex);
    
    // user_index로 UserTesseris 조회
    @Query("SELECT u FROM UserTesseris u WHERE u.userIndex = :userIndex")
    UserTesseris findUserTesserisByUserIndex(@Param("userIndex") Integer userIndex);
} 