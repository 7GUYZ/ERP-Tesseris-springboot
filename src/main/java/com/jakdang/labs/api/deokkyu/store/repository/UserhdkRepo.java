package com.jakdang.labs.api.deokkyu.store.repository;

import com.jakdang.labs.api.auth.entity.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserhdkRepo extends JpaRepository<UserEntity, String> {
    // findByUserId 삭제, findById만 사용
    
    /**
     * 사용자 이름으로 UserEntity 조회
     */
    Optional<UserEntity> findByName(String name);
}

