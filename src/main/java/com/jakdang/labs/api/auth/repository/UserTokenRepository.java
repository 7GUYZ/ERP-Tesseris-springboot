package com.jakdang.labs.api.auth.repository;

import com.jakdang.labs.api.auth.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 토큰 데이터 접근 객체 (DAO)
 * UserToken 엔티티에 대한 데이터베이스 CRUD 작업을 제공
 */
@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, String> {
    /**
     * 리프레시 토큰으로 사용자 토큰 조회
     * 
     * @param refreshToken 조회할 리프레시 토큰
     * @return 사용자 토큰 엔티티 (Optional)
     */
    Optional<UserToken> findByRefreshToken(String refreshToken);
}
