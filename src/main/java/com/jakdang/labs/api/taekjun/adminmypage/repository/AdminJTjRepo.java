package com.jakdang.labs.api.taekjun.adminmypage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.Admin;

@Repository
public interface AdminJTjRepo extends JpaRepository<Admin, Integer> {
    
    @Query("SELECT a FROM Admin a "+
    "LEFT JOIN FETCH a.adminTypeIndex " +
    "WHERE a.userIndex.userIndex = :userIndex")
    Admin findByUserIndex(@Param("userIndex") Integer userIndex);
} 