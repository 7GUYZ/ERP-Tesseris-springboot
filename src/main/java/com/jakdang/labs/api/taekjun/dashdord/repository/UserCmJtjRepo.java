package com.jakdang.labs.api.taekjun.dashdord.repository;

import com.jakdang.labs.entity.UserCm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCmJtjRepo extends JpaRepository<UserCm, Integer> {
} 