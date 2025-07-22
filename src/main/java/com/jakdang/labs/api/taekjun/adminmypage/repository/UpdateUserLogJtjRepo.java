package com.jakdang.labs.api.taekjun.adminmypage.repository;

import com.jakdang.labs.entity.UpdateUserLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UpdateUserLogJtjRepo extends JpaRepository<UpdateUserLog, Integer> {
} 
