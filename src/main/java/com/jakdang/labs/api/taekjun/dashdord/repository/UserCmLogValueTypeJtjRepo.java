package com.jakdang.labs.api.taekjun.dashdord.repository;

import com.jakdang.labs.entity.UserCmLogValueType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCmLogValueTypeJtjRepo extends JpaRepository<UserCmLogValueType, Integer> {
} 