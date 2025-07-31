package com.jakdang.labs.api.deokkyu.store.repository;

import com.jakdang.labs.api.auth.entity.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserhdkRepo extends JpaRepository<UserEntity, String> {
    // findByUserId 삭제, findById만 사용
    
    /**
     * 사용자 이름으로 UserEntity 조회 (첫 번째 결과만 반환)
     * name은 중복 가능하므로 First 사용
     */
    Optional<UserEntity> findFirstByName(String name);
    
    /**
     * 사용자 이메일로 UserEntity 조회 (단일 결과)
     * email은 UNIQUE 제약조건으로 중복 불가
     */
    Optional<UserEntity> findByEmail(String email);
    
    /**
     * 사용자 이름으로 UserEntity 목록 조회 (모든 결과 반환)
     * name 중복 시 모든 결과가 필요한 경우 사용
     */
    List<UserEntity> findAllByName(String name);
}

