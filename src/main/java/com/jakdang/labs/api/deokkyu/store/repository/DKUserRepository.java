package com.jakdang.labs.api.deokkyu.store.repository;

import com.jakdang.labs.api.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DKUserRepository extends JpaRepository<User, Integer> {
    // findByUserId 삭제, findById만 사용
}

