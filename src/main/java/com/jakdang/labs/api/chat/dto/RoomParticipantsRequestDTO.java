package com.jakdang.labs.api.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomParticipantsRequestDTO {
    private String roomparticipants_index;
    private String joined_at;
    private String left_at;
    private String notifications_enabled;
    private String user_id;
    private String room_index;
}
