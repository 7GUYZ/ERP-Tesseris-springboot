package com.jakdang.labs.api.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebSocket으로 받는 채팅 메시지 DTO
 * 프론트엔드 ChatWebSocketContext에서 전송하는 형식과 일치
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatWebSocketMessageDto {
    private String id;           // ${currentUser.id}_${Date.now()}
    private String roomId;       // 채팅방 ID
    private String text;         // 메시지 내용
    private ChatUserDto sender;  // 발신자 정보
    private String timestamp;    // ISO 문자열 형태
    private String type;         // "chat"
    
    /**
     * 내부 사용자 정보 클래스
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatUserDto {
        private String id;
        private String name;
        private String avatar;
    }
}