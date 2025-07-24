package com.jakdang.labs.api.dabin.CmsMemberRecommendation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jakdang.labs.entity.UserRole;

public interface UserRoleJdbRepo extends JpaRepository<UserRole, Integer> {
} 