package com.jakdang.labs.api.jihun.charge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.SuggestionUser;

@Repository
public interface AjgSuggestionUser extends JpaRepository<SuggestionUser, Long> {
    
    // 추천인 조회 (suggestion_user_index로 조회)
    java.util.Optional<SuggestionUser> findBySuggestionUserIndex(Integer suggestionUserIndex);
} 