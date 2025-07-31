package com.jakdang.labs.api.chat.controller;

import com.jakdang.labs.api.chat.dto.MessageRequestDTO;
import com.jakdang.labs.api.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ChatRestController {
    
    private final ChatService chatService;
    
    /**
     * 채팅방 기존 메시지 조회
     */
    @GetMapping("/messages/{roomId}")
    public ResponseEntity<List<MessageRequestDTO>> getMessages(@PathVariable String roomId) {
        try {
            log.info("채팅방 메시지 조회 요청: roomId={}", roomId);
            List<MessageRequestDTO> messages = chatService.getMessages(roomId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            log.error("채팅방 메시지 조회 중 오류: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 온라인 사용자 수 조회
     */
    @GetMapping("/online-users/count")
    public ResponseEntity<Integer> getOnlineUsersCount() {
        try {
            // 실제로는 ChatService에서 온라인 사용자 수를 반환하는 메서드가 필요합니다
            log.info("온라인 사용자 수 조회 요청");
            return ResponseEntity.ok(0); // 임시로 0 반환
        } catch (Exception e) {
            log.error("온라인 사용자 수 조회 중 오류: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
} 