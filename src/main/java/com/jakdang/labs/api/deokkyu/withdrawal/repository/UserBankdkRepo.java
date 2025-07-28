package com.jakdang.labs.api.deokkyu.withdrawal.repository;

import com.jakdang.labs.entity.UserBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBankdkRepo extends JpaRepository<UserBank, Integer> {
} 