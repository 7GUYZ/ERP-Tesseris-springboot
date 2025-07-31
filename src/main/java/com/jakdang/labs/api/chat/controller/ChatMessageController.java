package com.jakdang.labs.api.chat.controller;

import com.jakdang.labs.api.chat.dto.*;
import com.jakdang.labs.api.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController {
    
    private final ChatService chatService;
    
    /**
     * 사용자 로그인 처리
     */
    @MessageMapping("/chat/login")
    public void handleUserLogin(@Payload String userId, SimpMessageHeaderAccessor headerAccessor) {
        log.info("사용자 로그인 요청: {}", userId);
        chatService.handleUserLogin(userId);
    }
    
    /**
     * 사용자 로그아웃 처리
     */
    @MessageMapping("/chat/logout")
    public void handleUserLogout(@Payload String userId, SimpMessageHeaderAccessor headerAccessor) {
        log.info("사용자 로그아웃 요청: {}", userId);
        chatService.handleUserLogout(userId);
    }
    
    /**
     * 채팅방 입장
     */
    @MessageMapping("/chat/join-room")
    public void joinRoom(@Payload ChatJoinRoomRequest request) {
        log.info("채팅방 입장 요청: roomId={}, userId={}", request.getRoomId(), request.getUserId());
        chatService.joinRoom(request.getRoomId(), request.getUserId());
    }
    
    /**
     * 채팅 메시지 전송
     */
    @MessageMapping("/chat/room")
    public void sendMessage(@Payload MessageRequestDTO message) {
        log.info("채팅 메시지 전송 요청: roomId={}, userId={}", message.getRoom_index(), message.getUser_id());
        chatService.sendMessage(message);
    }
    
    /**
     * 기존 메시지 조회
     */
    @MessageMapping("/chat/get-messages")
    public void getMessages(@Payload String roomId) {
        log.info("기존 메시지 조회 요청: roomId={}", roomId);
        // 클라이언트에서 직접 /topic/room/{roomId}/messages를 구독하도록 안내
    }
    
    /**
     * 타이핑 상태 전송
     */
    @MessageMapping("/chat/typing")
    public void sendTypingStatus(@Payload ChatTypingDto typingStatus) {
        log.debug("타이핑 상태 전송 요청: roomId={}, userId={}, isTyping={}", 
                typingStatus.getRoomId(), typingStatus.getUserId(), typingStatus.isTyping());
        chatService.sendTypingStatus(typingStatus);
    }
    
    /**
     * 채팅방 입장 요청 DTO
     */
    public static class ChatJoinRoomRequest {
        private String roomId;
        private String userId;
        
        // Getters and Setters
        public String getRoomId() { return roomId; }
        public void setRoomId(String roomId) { this.roomId = roomId; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
    }
} 