package com.jakdang.labs.api.jiyun.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.api.entity.UserTesseris;

import java.util.Optional;

@Repository("noticeUserRepository")
public interface NoticeUserRepository extends JpaRepository<UserTesseris, Integer> {
    Optional<UserTesseris> findByUserIndex(Integer userIndex);
} 