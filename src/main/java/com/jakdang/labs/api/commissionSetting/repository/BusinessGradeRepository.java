package com.jakdang.labs.api.commissionSetting.repository;

import com.jakdang.labs.api.commissionSetting.entity.BusinessGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessGradeRepository extends JpaRepository<BusinessGrade, Integer> {
}
