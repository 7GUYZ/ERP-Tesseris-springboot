package com.jakdang.labs.api.jiyun.mypage.repository;

import com.jakdang.labs.entity.UserTesseris;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MypageGeneralRepository extends JpaRepository<UserTesseris, Integer> {
    Optional<UserTesseris> findByUsersId_Id(String id);
} 