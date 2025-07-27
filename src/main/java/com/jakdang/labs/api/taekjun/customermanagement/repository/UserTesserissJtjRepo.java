package com.jakdang.labs.api.taekjun.customermanagement.repository;

import com.jakdang.labs.entity.UserTesseris;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserTesserissJtjRepo extends JpaRepository<UserTesseris, Integer> {
    Optional<UserTesseris> findByUserIndex(Integer userIndex);
} 