package com.jakdang.labs.api.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 채팅 메시지 전송 처리
     * 클라이언트에서 /app/adminchat.sendMessage/{roomId}로 전송
     * 해당 방의 모든 구독자에게 /queue/{roomId}로 브로드캐스트
     */
    @MessageMapping("/adminchat.sendMessage/{roomId}")
    @SendTo("/queue/{roomId}")
    public Map<String, Object> sendMessage(@Payload Map<String, Object> messageData,
            SimpMessageHeaderAccessor headerAccessor) {

        log.info("채팅 메시지 수신: {}", messageData);

        // 메시지에 타임스탬프 추가
        messageData.put("timestamp", System.currentTimeMillis());

        // 발신자 정보 추가
        if (headerAccessor.getUser() != null) {
            messageData.put("sender", headerAccessor.getUser().getName());
        }

        log.info("채팅 메시지 브로드캐스트: {}", messageData);

        return messageData;
    }

    /**
     * 채팅방 구독 처리
     * 클라이언트가 /queue/{roomId}를 구독할 때 호출
     */
    @MessageMapping("/adminchat.joinRoom/{roomId}")
    @SendTo("/queue/{roomId}")
    public Map<String, Object> joinRoom(@Payload Map<String, Object> joinMessage,
            SimpMessageHeaderAccessor headerAccessor) {

        log.info("채팅방 입장: roomId={}, user={}",
                joinMessage.get("roomId"),
                headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : "unknown");

        // 입장 메시지 브로드캐스트
        Map<String, Object> systemMessage = Map.of(
                "type", "system",
                "message", "새로운 사용자가 입장했습니다.",
                "timestamp", System.currentTimeMillis());

        return systemMessage;
    }

    /**
     * 채팅방 퇴장 처리
     */
    @MessageMapping("/adminchat.leaveRoom/{roomId}")
    @SendTo("/queue/{roomId}")
    public Map<String, Object> leaveRoom(@Payload Map<String, Object> leaveMessage,
            SimpMessageHeaderAccessor headerAccessor) {

        log.info("채팅방 퇴장: roomId={}, user={}",
                leaveMessage.get("roomId"),
                headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : "unknown");

        // 퇴장 메시지 브로드캐스트
        Map<String, Object> systemMessage = Map.of(
                "type", "system",
                "message", "사용자가 퇴장했습니다.",
                "timestamp", System.currentTimeMillis());

        return systemMessage;
    }
}