package com.jakdang.labs.api.dabin.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.BusinessGrade;

@Repository
public interface BusinessGradeJdbRepo extends JpaRepository<BusinessGrade, Integer> {
}
