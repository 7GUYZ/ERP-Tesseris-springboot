package com.jakdang.labs.api.taekjun.signin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReferralListDTO {
    private Integer userIndex;
    private String userName;
    private String userNickname;
    private String userEmail;
    private LocalDateTime joinDate;
    private String referralCode;
} 