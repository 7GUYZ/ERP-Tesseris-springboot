package com.jakdang.labs.api.deokkyu.store.repository;

import com.jakdang.labs.entity.BusinessMan;
import com.jakdang.labs.entity.UserTesseris;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusinessManhdkRepo extends JpaRepository<BusinessMan, Integer> {

    Optional<BusinessMan> findByUserIndex(UserTesseris userIndex);
    
}

