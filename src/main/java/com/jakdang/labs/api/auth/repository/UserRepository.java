package com.jakdang.labs.api.auth.repository;

import com.jakdang.labs.api.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 데이터 접근 객체 (DAO)
 * UserEntity에 대한 데이터베이스 CRUD 작업을 제공
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    /**
     * 이메일로 사용자 조회
     * 
     * @param email 조회할 사용자의 이메일
     * @return 사용자 엔티티 (Optional)
     */
    Optional<UserEntity> findByEmail(String email);
}
