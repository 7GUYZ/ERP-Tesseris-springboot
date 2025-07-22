package com.jakdang.labs.api.taekjun.dashdord.repository;

import com.jakdang.labs.entity.BusinessMan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessManJtjRepo extends JpaRepository<BusinessMan, Integer> {
    // 전체 사업자 수
    @Query(value = "SELECT COUNT(*) FROM business_man", nativeQuery = true)
    Long countBusinessManTotal();

    // 어제 가입된 사업자 수
    @Query(value = "SELECT COUNT(*) FROM business_man WHERE DATE(business_man_create_date) = :date", nativeQuery = true)
    Long countBusinessManByDate(@Param("date") String date);
} 