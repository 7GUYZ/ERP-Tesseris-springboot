package com.jakdang.labs.api.taekjun.signin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.entity.UserTesseris;

@Repository
public interface SignupRepository extends JpaRepository<UserEntity, String> {
    
    // 이메일 중복 체크
    @Query("SELECT COUNT(u) > 0 FROM UserEntity u WHERE u.email = :email")
    boolean existsByEmail(@Param("email") String email);
    
    // 닉네임 중복 체크
    @Query("SELECT COUNT(u) > 0 FROM UserEntity u WHERE u.nickname = :nickname")
    boolean existsByNickname(@Param("nickname") String nickname);
    
    // 전화번호 중복 체크
    @Query("SELECT COUNT(u) > 0 FROM UserEntity u WHERE u.phone = :phone")
    boolean existsByPhone(@Param("phone") String phone);
} 