package com.jakdang.labs.api.dabin.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.api.dabin.dto.MemberRecommendationSearchResponseDto;

@Repository("memberRecommendationRepository")
public interface MemberRecommendationJdbRepo extends JpaRepository<com.jakdang.labs.entity.SuggestionUser, Integer> {
    
    @Query("""
        SELECT new com.jakdang.labs.api.dabin.dto.MemberRecommendationSearchResponseDto(
            t2.usersId.id,
            t2.usersId.name,
            t4.userRoleKorNm,
            t5.storeName,
            t3.usersId.id,
            t3.usersId.name,
            t6.userRoleKorNm,
            t1.joinDate
        )
        FROM SuggestionUser t1
        LEFT JOIN UserTesseris t2 ON t1.suggestionUserIndex = t2.userIndex
        LEFT JOIN UserTesseris t3 ON t1.recommendationUserIndex = t3.userIndex
        INNER JOIN UserRole t4 ON t2.userRoleIndex = t4.userRoleIndex
        LEFT JOIN Store t5 ON t1.suggestionUserIndex = t5.userIndex.userIndex
        INNER JOIN UserRole t6 ON t3.userRoleIndex = t6.userRoleIndex
        WHERE (:suggestionUserId IS NULL OR :suggestionUserId = '' OR t2.usersId.id LIKE CONCAT('%', :suggestionUserId, '%'))
        AND (:suggestionUserName IS NULL OR :suggestionUserName = '' OR t2.usersId.name LIKE CONCAT('%', :suggestionUserName, '%'))
        AND (:suggestionUserRole IS NULL OR :suggestionUserRole = 0 OR t4.userRoleIndex = :suggestionUserRole)
        AND (:recommendationUserRole IS NULL OR :recommendationUserRole = 0 OR t3.userRoleIndex = :recommendationUserRole)
        AND (:suggestionStoreName IS NULL OR :suggestionStoreName = '' OR t5.storeName LIKE CONCAT('%', :suggestionStoreName, '%'))
        AND (:joinDateStart IS NULL OR t1.joinDate >= :joinDateStart)
        AND (:joinDateEnd IS NULL OR t1.joinDate <= :joinDateEnd)
        AND (:userName IS NULL OR :userName = '' OR t3.usersId.name LIKE CONCAT('%', :userName, '%'))
        ORDER BY t1.joinDate DESC
    """)
    
    List<MemberRecommendationSearchResponseDto> searchMemberRecommendations(
        @Param("suggestionUserId") String suggestionUserId,
        @Param("suggestionUserName") String suggestionUserName,
        @Param("suggestionUserRole") Integer suggestionUserRole,
        @Param("recommendationUserRole") Integer recommendationUserRole,
        @Param("suggestionStoreName") String suggestionStoreName,
        @Param("joinDateStart") java.time.LocalDateTime joinDateStart,
        @Param("joinDateEnd") java.time.LocalDateTime joinDateEnd,
        @Param("userName") String userName
    );
} 