package com.jakdang.labs.api.dabin.CmsMemberRecommendation.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberRecommendationSearchResponseDto {
    private String suggestionUserId;
    private String suggestionUserName;
    private String suggestionUserRole;
    private String suggestionStoreName;
    private String recommendationUserId;
    private String recommendationUserName;
    private String recommendationUserRole;
    private LocalDateTime joinDate;
} 