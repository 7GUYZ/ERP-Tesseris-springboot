package com.jakdang.labs.api.notice.repository;

import com.jakdang.labs.api.notice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("noticeUserRepository")
public interface NoticeUserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserIndex(Integer userIndex);
} 