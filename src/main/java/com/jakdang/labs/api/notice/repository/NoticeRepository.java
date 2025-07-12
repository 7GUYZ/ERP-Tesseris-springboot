package com.jakdang.labs.api.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jakdang.labs.api.notice.entity.Notice;

public interface NoticeRepository extends JpaRepository<Notice,Integer>{
  
}
