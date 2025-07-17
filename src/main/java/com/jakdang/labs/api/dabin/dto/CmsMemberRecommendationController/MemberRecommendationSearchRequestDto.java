package com.jakdang.labs.api.dabin.dto.CmsMemberRecommendationController;

import java.time.LocalDate;

public class MemberRecommendationSearchRequestDto {
    private String suggestionUserId;
    private String suggestionUserName;
    private Integer suggestionUserRole;
    private String suggestionStoreName;
    private String recommendationUserRole;
    private LocalDate joinDateStart;
    private LocalDate joinDateEnd;
    private String userName;

    // getter, setter
    public String getSuggestionUserId() { return suggestionUserId; }
    public void setSuggestionUserId(String suggestionUserId) { this.suggestionUserId = suggestionUserId; }
    
    public String getSuggestionUserName() { return suggestionUserName; }
    public void setSuggestionUserName(String suggestionUserName) { this.suggestionUserName = suggestionUserName; }
    
    public Integer getSuggestionUserRole() { return suggestionUserRole; }
    public void setSuggestionUserRole(Integer suggestionUserRole) { this.suggestionUserRole = suggestionUserRole; }
    
    public String getSuggestionStoreName() { return suggestionStoreName; }
    public void setSuggestionStoreName(String suggestionStoreName) { this.suggestionStoreName = suggestionStoreName; }
    
    public String getRecommendationUserRole() { return recommendationUserRole; }
    public void setRecommendationUserRole(String recommendationUserRole) { this.recommendationUserRole = recommendationUserRole; }
    
    public LocalDate getJoinDateStart() { return joinDateStart; }
    public void setJoinDateStart(LocalDate joinDateStart) { this.joinDateStart = joinDateStart; }
    
    public LocalDate getJoinDateEnd() { return joinDateEnd; }
    public void setJoinDateEnd(LocalDate joinDateEnd) { this.joinDateEnd = joinDateEnd; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
} 