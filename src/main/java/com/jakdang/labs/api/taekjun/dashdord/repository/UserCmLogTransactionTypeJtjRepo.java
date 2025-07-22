package com.jakdang.labs.api.taekjun.dashdord.repository;

import com.jakdang.labs.entity.UserCmLogTransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCmLogTransactionTypeJtjRepo extends JpaRepository<UserCmLogTransactionType, Integer> {
} 