package com.jakdang.labs.api.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageReadsRequestDTO {
    private String messagereads_index;
    private String read_at;
    private String user_id;
    private String message_index;
}
