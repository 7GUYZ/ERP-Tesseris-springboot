package com.jakdang.labs.api.taekjun.Permissionsettings.repository;


import java.util.Optional;

import com.jakdang.labs.api.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.UserTesseris;



@Repository
public interface UserTesserisRepository extends JpaRepository<UserTesseris, Integer> {
    // userIndex로 사용자 조회 (비밀번호 확인용)
    Optional<UserTesseris> findByUserIndex(Integer userIndex);
    
    // UserEntity로 UserTesseris 조회
    Optional<UserTesseris> findByUsersId(UserEntity usersId);
}