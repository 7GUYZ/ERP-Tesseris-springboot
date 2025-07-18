package com.jakdang.labs.api.jiyun.commissionSetting.repository;

import com.jakdang.labs.entity.BusinessGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessGradekjyRepository extends JpaRepository<BusinessGrade, Integer> {
} 