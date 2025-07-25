package com.jakdang.labs.api.taekjun.businessmanlist.repository;

import com.jakdang.labs.entity.UserBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface UserBankJtjRepo extends JpaRepository<UserBank, Integer> {
    
    @Modifying
    @Query(value = "UPDATE user_bank SET created_at = :createdAt, updated_at = :updatedAt WHERE user_bank_index = :id", nativeQuery = true)
    void updateTimestamps(@Param("id") Integer id, @Param("createdAt") Instant createdAt, @Param("updatedAt") Instant updatedAt);
    
    @Modifying
    @Query(value = "UPDATE user_bank SET updated_at = :updatedAt WHERE user_bank_index = :id", nativeQuery = true)
    void updateTimestamp(@Param("id") Integer id, @Param("updatedAt") Instant updatedAt);
} 