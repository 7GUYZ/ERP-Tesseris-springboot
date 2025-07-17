package com.jakdang.labs.api.taekjun.signin.dto;

import lombok.Data;

@Data
public class SignupResponseDTO {
    private String message;
    private String userId;
    private Integer userIndex;
    private boolean success;
    
    public SignupResponseDTO(String message, String userId, Integer userIndex, boolean success) {
        this.message = message;
        this.userId = userId;
        this.userIndex = userIndex;
        this.success = success;
    }
} 