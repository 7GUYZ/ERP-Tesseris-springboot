package com.jakdang.labs.api.taekjun.dashdord.repository;

import com.jakdang.labs.entity.UserTesseris;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface UserTesserisJtjRepo extends JpaRepository<UserTesseris, Integer> {
    @Query("SELECT COUNT(u) FROM UserTesseris u")
    Long countUserTotal();

    @Query("SELECT COUNT(u) FROM UserTesseris u WHERE DATE(u.usersId.createdAt) = :date")
    Long countUserByDate(@Param("date") LocalDate date);
} 