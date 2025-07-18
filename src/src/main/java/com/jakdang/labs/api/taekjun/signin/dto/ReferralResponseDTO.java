package com.jakdang.labs.api.taekjun.signin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReferralResponseDTO {
    private String message;
    private boolean success;
    private String referralCode;
    private Integer referralCount;
    private String referrerName;
    private String referrerNickname;
} 