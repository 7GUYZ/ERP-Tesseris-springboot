package com.jakdang.labs.api.taekjun.businessmanlist.repository;

import com.jakdang.labs.entity.BusinessArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessAreaJtjRepo extends JpaRepository<BusinessArea, Integer> {
} 