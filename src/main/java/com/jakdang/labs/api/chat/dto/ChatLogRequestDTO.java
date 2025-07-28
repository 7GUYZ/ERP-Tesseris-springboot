package com.jakdang.labs.api.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatLogRequestDTO {
    private String log_index;
    private String message;
    private String log_type;
    private String sent_at;
    private String room_index;
    private String user_id;
}
