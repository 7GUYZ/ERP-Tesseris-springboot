package com.jakdang.labs.api.chat.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatUserDto {
    private String id;        // user_index
    private String name;      // 사용자명
    private String email;     // 이메일
    private String role;      // user_role_index
} 