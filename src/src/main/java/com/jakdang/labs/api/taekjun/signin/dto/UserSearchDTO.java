package com.jakdang.labs.api.taekjun.signin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchDTO {
    private String searchType; // "email" 또는 "nickname"
    private String searchValue; // 검색할 값
} 