package com.jakdang.labs.api.chat.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatLoginLogoutDto {
    private ChatUserDto user;       // 사용자 정보
    private LocalDateTime timestamp; // 시간
} 