package com.jakdang.labs.api.taekjun.signin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.UserGender;

@Repository
public interface UserGenderRepository extends JpaRepository<UserGender, Integer> {
} 