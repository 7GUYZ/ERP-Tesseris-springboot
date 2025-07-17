package com.jakdang.labs.api.deokkyu.store.repository;

import com.jakdang.labs.api.auth.entity.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HDKUserRepository extends JpaRepository<UserEntity, String> {
    // findByUserId 삭제, findById만 사용
}

