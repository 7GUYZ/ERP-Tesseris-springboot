package com.jakdang.labs.api.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmCheckRequestDTO {
    private String user_id;
    private String room_index;
    private String alarm_index;
}
