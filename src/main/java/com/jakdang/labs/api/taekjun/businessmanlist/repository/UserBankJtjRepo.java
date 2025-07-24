package com.jakdang.labs.api.taekjun.businessmanlist.repository;

import com.jakdang.labs.entity.UserBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBankJtjRepo extends JpaRepository<UserBank, Integer> {
} 