package com.jakdang.labs.api.deokkyu.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.BusinessArea;

@Repository
public interface BusinessAreahdkRepo extends JpaRepository<BusinessArea, Integer> {
} 