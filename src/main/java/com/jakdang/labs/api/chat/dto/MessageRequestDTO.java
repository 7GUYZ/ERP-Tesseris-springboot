package com.jakdang.labs.api.chat.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequestDTO {
<<<<<<< HEAD
    private String message_index;
    private String user_id; 
    private String sent_at;
    private String message;
    private String room_index;
    private String active;

        // 방 생성 관련 필드
    // 방 생성 관련 필드 추가
    private String room_name;        // 방 이름 (첫 메시지 시 필요)
    private List<String> participants;     // 참여자 목록 (JSON 문자열)
=======
    private String message; // 메시지 내용
    private String room_index; // 방 인덱스
    private String room_name; // 방 이름
    private String user_id; // 사용자 ID (프론트엔드에서 보내는 필드)
    private String timestamp; // 타임스탬프 (프론트엔드에서 보내는 필드)
    private List<String> participants; // 참여자 목록
>>>>>>> jihun
}
