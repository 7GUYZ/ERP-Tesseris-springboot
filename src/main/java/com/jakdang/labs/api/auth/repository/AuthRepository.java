package com.jakdang.labs.api.auth.repository;

import com.jakdang.labs.api.auth.dto.UserDTO;
import com.jakdang.labs.api.auth.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 인증 관련 데이터 접근 객체 (DAO)
 * 사용자 인증과 관련된 데이터베이스 작업을 제공
 */
@Repository
public interface AuthRepository extends JpaRepository<UserEntity, String> {
    /**
     * 이메일로 사용자 존재 여부 확인
     * 
     * @param email 확인할 이메일
     * @return 사용자 존재 여부
     */
    boolean existsByEmail(String email);
    
    /**
     * 이메일로 사용자 조회
     * 
     * @param email 조회할 사용자의 이메일
     * @return 사용자 엔티티 (Optional)
     */
    Optional<UserEntity> findByEmail(String email);
    
    /**
     * ID로 사용자 존재 여부 확인
     * 
     * @param id 확인할 사용자 ID
     * @return 사용자 존재 여부
     */
    boolean existsById(String id);

    /**
     * ID 목록으로 사용자 목록 조회
     * 
     * @param likedUserIds 조회할 사용자 ID 목록
     * @return 사용자 엔티티 목록
     */
    List<UserEntity> findByIdIn(List<String> likedUserIds);
}
