package com.jakdang.labs.api.taekjun.passwordfind.dto;

import lombok.Data;

@Data
public class PasswordFindResponseDTO {
    private boolean success;
    private String message;
    private String authToken;
    private boolean isValid;
    
    public PasswordFindResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public PasswordFindResponseDTO(boolean success, String message, String authToken) {
        this.success = success;
        this.message = message;
        this.authToken = authToken;
    }
    
    public PasswordFindResponseDTO(boolean success, String message, boolean isValid) {
        this.success = success;
        this.message = message;
        this.isValid = isValid;
    }
} 