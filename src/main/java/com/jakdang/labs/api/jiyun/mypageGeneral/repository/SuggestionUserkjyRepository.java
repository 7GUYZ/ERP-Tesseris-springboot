package com.jakdang.labs.api.jiyun.mypageGeneral.repository;

import com.jakdang.labs.entity.SuggestionUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SuggestionUserkjyRepository extends JpaRepository<SuggestionUser, Long> {
    List<SuggestionUser> findBySuggestionUserIndexOrderByJoinDateDesc(Integer suggestionUserIndex);
} 