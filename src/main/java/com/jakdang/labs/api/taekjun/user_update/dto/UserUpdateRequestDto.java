package com.jakdang.labs.api.taekjun.user_update.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDto {
    private String userName;
    private String userPhone;
    private String userEmail;
    private String userAddress;
    private String userDetailAddress;
    private String userZipCode;
    private Long userBankIndex;
    private String userBankNumber;
    private String userBankHolder;
} 