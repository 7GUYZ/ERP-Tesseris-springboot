package com.jakdang.labs.api.taekjun.signin.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.SuggestionUser;

import java.time.Instant;

@Repository
public interface SuggestionUserRepository extends JpaRepository<SuggestionUser, Long> {
    
    // 추천인으로 등록된 사용자들 조회
    @Query("SELECT su FROM SuggestionUser su WHERE su.recommendationUserIndex = :recommendationUserIndex")
    List<SuggestionUser> findByRecommendationUserIndex(@Param("recommendationUserIndex") Integer recommendationUserIndex);
    
    // 특정 사용자가 추천받은 사용자인지 확인
    @Query("SELECT su FROM SuggestionUser su WHERE su.suggestionUserIndex = :suggestionUserIndex")
    Optional<SuggestionUser> findBySuggestionUserIndex(@Param("suggestionUserIndex") Integer suggestionUserIndex);
    
    // 추천인 관계가 이미 존재하는지 확인
    @Query("SELECT COUNT(su) > 0 FROM SuggestionUser su WHERE su.suggestionUserIndex = :suggestionUserIndex AND su.recommendationUserIndex = :recommendationUserIndex")
    boolean existsBySuggestionUserIndexAndRecommendationUserIndex(
        @Param("suggestionUserIndex") Integer suggestionUserIndex, 
        @Param("recommendationUserIndex") Integer recommendationUserIndex
    );
    
    // 특정 사용자의 추천인 수 조회
    @Query("SELECT COUNT(su) FROM SuggestionUser su WHERE su.recommendationUserIndex = :recommendationUserIndex")
    long countByRecommendationUserIndex(@Param("recommendationUserIndex") Integer recommendationUserIndex);
    
    // SuggestionUser의 created_at, updated_at 값 설정
    @Modifying
    @Query(value = "UPDATE suggestion_user SET created_at = :createdAt, updated_at = :updatedAt WHERE suggestion_user_index = :id", nativeQuery = true)
    void updateSuggestionUserTimestamps(@Param("id") Integer id, @Param("createdAt") Instant createdAt, @Param("updatedAt") Instant updatedAt);
    
    // SuggestionUser의 updated_at 값만 설정
    @Modifying
    @Query(value = "UPDATE suggestion_user SET updated_at = :updatedAt WHERE suggestion_user_index = :id", nativeQuery = true)
    void updateSuggestionUserTimestamp(@Param("id") Integer id, @Param("updatedAt") Instant updatedAt);
} 