package com.jakdang.labs.api.deokkyu.store.repository;

import com.jakdang.labs.entity.BusinessGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessGradehdkRepo extends JpaRepository<BusinessGrade, Integer> {
    
    /**
     * 모든 사업자 등급을 business_grade_level 순으로 정렬하여 조회
     */
    @Query("SELECT bg FROM BusinessGrade bg ORDER BY bg.businessGradeLevel ASC")
    List<BusinessGrade> findAllOrderByLevel();
    
    /**
     * business_grade_name으로 BusinessGrade 조회
     */
    BusinessGrade findByBusinessGradeName(String businessGradeName);
}

