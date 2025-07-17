package com.jakdang.labs.api.jihun.memberaccount.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.jakdang.labs.entity.UserTesseris;

@Repository
public interface AjhUserRepository extends JpaRepository<UserTesseris, Long> {
} 