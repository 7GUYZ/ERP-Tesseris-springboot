package com.jakdang.labs.api.dabin.CmsMemberRecommendation.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;


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
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime joinDate;
} 