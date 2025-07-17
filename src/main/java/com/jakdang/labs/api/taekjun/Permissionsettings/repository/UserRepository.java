package com.jakdang.labs.api.taekjun.Permissionsettings.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.api.auth.entity.UserEntity;

import java.util.Optional;

@Repository("permissionsettingsUserRepository")
public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findById(String id);
    
    // JPQL 사용 (엔티티 참조)
    @Query("SELECT u.password FROM UserEntity u WHERE u.id = :id")
    Optional<String> findPasswordById(@Param("id") String id);
    
    // 비밀번호만 조회하는 메서드
    @Query("SELECT u.password FROM UserEntity u WHERE u.id = :id")
    Optional<String> getPasswordById(@Param("id") String id);
    
    // 추천인 코드로 사용자 찾기
    Optional<UserEntity> findByReferralCode(String referralCode);
    
    // 닉네임으로 사용자 찾기
    Optional<UserEntity> findByNickname(String nickname);
    
    // 이메일 또는 닉네임으로 사용자 검색
    @Query("SELECT u FROM UserEntity u WHERE u.email = :searchValue OR u.nickname = :searchValue")
    Optional<UserEntity> findByEmailOrNickname(@Param("searchValue") String searchValue);
} 