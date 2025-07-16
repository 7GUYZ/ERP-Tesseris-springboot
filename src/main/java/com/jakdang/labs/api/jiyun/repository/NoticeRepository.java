package com.jakdang.labs.api.jiyun.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jakdang.labs.api.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Integer> {
  
} 