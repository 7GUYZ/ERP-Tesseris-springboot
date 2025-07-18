package com.jakdang.labs.api.deokkyu.store.repository;

import com.jakdang.labs.entity.UserTesseris;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface HDKUserTesserisRepository extends JpaRepository<UserTesseris, Integer> {
    Optional<UserTesseris> findByUserIndex(Integer userIndex);
} 