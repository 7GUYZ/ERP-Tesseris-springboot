package com.jakdang.labs.api.taekjun.Permissionsettings.repository;

import java.util.Optional;

import com.jakdang.labs.api.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.UserTesseris;

@Repository
public interface UserTesserisRepository extends JpaRepository<UserTesseris, Integer> {
    // userIndex로 사용자 조회 (비밀번호 확인용)
    Optional<UserTesseris> findByUserIndex(Integer userIndex);

    // UserEntity로 UserTesseris 조회
    Optional<UserTesseris> findByUsersId(UserEntity usersId);

    // userId(UUID)로 UserTesseris 조회
    Optional<UserTesseris> findByUsersId_Id(String id);

    // 직접 SQL 쿼리로 UserTesseris 조회 (디버깅용)
    @Query("SELECT u FROM UserTesseris u WHERE u.usersId.id = :userId")
    Optional<UserTesseris> findByUserIdDirect(@Param("userId") String userId);
}