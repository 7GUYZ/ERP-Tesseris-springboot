package com.jakdang.labs.api.pinChange.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jakdang.labs.api.pinChange.entity.UserCm;

public interface PinChangeRepository extends JpaRepository<UserCm, Integer>{
  
}
