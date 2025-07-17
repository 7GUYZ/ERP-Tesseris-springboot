package com.jakdang.labs.api.taekjun.signin.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.SuggestionUser;

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
} 