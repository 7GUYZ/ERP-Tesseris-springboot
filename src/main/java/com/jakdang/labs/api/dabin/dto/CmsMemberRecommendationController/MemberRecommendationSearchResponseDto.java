package com.jakdang.labs.api.dabin.dto.CmsMemberRecommendationController;

import java.time.LocalDateTime;

public class MemberRecommendationSearchResponseDto {
    private String suggestionUserId;
    private String suggestionUserName;
    private String suggestionUserRole;
    private String suggestionStoreName;
    private String recommendationUserId;
    private String recommendationUserName;
    private String recommendationUserRole;
    private LocalDateTime joinDate;

    // JPQL DTO 생성자 추가
    public MemberRecommendationSearchResponseDto(
        String suggestionUserId,
        String suggestionUserName,
        String suggestionUserRole,
        String suggestionStoreName,
        String recommendationUserId,
        String recommendationUserName,
        String recommendationUserRole,
        LocalDateTime joinDate
    ) {
        this.suggestionUserId = suggestionUserId;
        this.suggestionUserName = suggestionUserName;
        this.suggestionUserRole = suggestionUserRole;
        this.suggestionStoreName = suggestionStoreName;
        this.recommendationUserId = recommendationUserId;
        this.recommendationUserName = recommendationUserName;
        this.recommendationUserRole = recommendationUserRole;
        this.joinDate = joinDate;
    }

    // getter, setter
    public String getSuggestionUserId() { return suggestionUserId; }
    public void setSuggestionUserId(String suggestionUserId) { this.suggestionUserId = suggestionUserId; }
    
    public String getSuggestionUserName() { return suggestionUserName; }
    public void setSuggestionUserName(String suggestionUserName) { this.suggestionUserName = suggestionUserName; }
    
    public String getSuggestionUserRole() { return suggestionUserRole; }
    public void setSuggestionUserRole(String suggestionUserRole) { this.suggestionUserRole = suggestionUserRole; }
    
    public String getSuggestionStoreName() { return suggestionStoreName; }
    public void setSuggestionStoreName(String suggestionStoreName) { this.suggestionStoreName = suggestionStoreName; }
    
    public String getRecommendationUserId() { return recommendationUserId; }
    public void setRecommendationUserId(String recommendationUserId) { this.recommendationUserId = recommendationUserId; }
    
    public String getRecommendationUserName() { return recommendationUserName; }
    public void setRecommendationUserName(String recommendationUserName) { this.recommendationUserName = recommendationUserName; }
    
    public String getRecommendationUserRole() { return recommendationUserRole; }
    public void setRecommendationUserRole(String recommendationUserRole) { this.recommendationUserRole = recommendationUserRole; }
    
    public LocalDateTime getJoinDate() { return joinDate; }
    public void setJoinDate(LocalDateTime joinDate) { this.joinDate = joinDate; }
} 