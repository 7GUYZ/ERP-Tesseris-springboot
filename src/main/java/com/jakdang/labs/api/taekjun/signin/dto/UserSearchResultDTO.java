package com.jakdang.labs.api.taekjun.signin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchResultDTO {
    private String userId;
    private String userName;
    private String userNickname;
    private String userEmail;
    private String referralCode;
    private boolean found;
    private String message;
} 