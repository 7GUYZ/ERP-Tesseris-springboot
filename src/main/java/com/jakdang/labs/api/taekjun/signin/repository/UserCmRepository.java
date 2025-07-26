package com.jakdang.labs.api.taekjun.signin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.UserCm;

import java.time.Instant;

@Repository
public interface UserCmRepository extends JpaRepository<UserCm, Integer> {
    Optional<UserCm> findByUserCmIndex(Integer userCmIndex);
    
    // UserCm의 created_at, updated_at 값 설정
    @Modifying
    @Query(value = "UPDATE user_cm SET created_at = :createdAt, updated_at = :updatedAt WHERE user_cm_index = :id", nativeQuery = true)
    void updateUserCmTimestamps(@Param("id") Integer id, @Param("createdAt") Instant createdAt, @Param("updatedAt") Instant updatedAt);
    
    // UserCm의 updated_at 값만 설정
    @Modifying
    @Query(value = "UPDATE user_cm SET updated_at = :updatedAt WHERE user_cm_index = :id", nativeQuery = true)
    void updateUserCmTimestamp(@Param("id") Integer id, @Param("updatedAt") Instant updatedAt);
} 