package com.jakdang.labs.api.chat.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatTypingDto {
    private String roomId;    // room_id
    private String userId;    // user_index
    private String userName;  // 사용자명
    private boolean isTyping; // 타이핑 상태
} 