package com.jakdang.labs.api.taekjun.signin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.UserCm;

@Repository
public interface UserCmRepository extends JpaRepository<UserCm, Integer> {
    Optional<UserCm> findByUserCmIndex(Integer userCmIndex);
} 