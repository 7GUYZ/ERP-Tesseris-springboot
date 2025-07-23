package com.jakdang.labs.api.jiyun.mypage.dto;

import lombok.Data;

@Data
public class SuggestionUserListDto {
    private String suggestionUserId;
    private String suggestionUserName;
    private String suggestionUserRole;
    private String suggestionStoreName;
    private String recommendationUserId;
    private String recommendationUserName;
    private String recommendationUserRole;
    private String joinDate;
} 