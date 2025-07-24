package com.jakdang.labs.api.dabin.CmsMemberRecommendation.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRecommendationSearchRequestDto {
    private String suggestionUserId;
    private String suggestionUserName;
    private Integer suggestionUserRole;
    private String suggestionStoreName;
    private String recommendationUserRole;
    private LocalDate joinDateStart;
    private LocalDate joinDateEnd;
    private String userName;

} 