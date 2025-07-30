package com.jakdang.labs.api.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FrontendMessageRequestDTO {
    private String user_id;
    private String sent_at;
    private String message;
    private Integer room_index;  // null일 수 있음 (새로운 채팅방인 경우)
} 