package com.jakdang.labs.api.alarm.controller;

import java.util.List;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 알림 전송 공통 메소드
     */
    private void sendToUserList(List<String> userIndexes, Map<String, Object> notification) {
        for (String userIndex : userIndexes) {
            String destination = "/topic/notifications/" + userIndex;
            messagingTemplate.convertAndSend(destination, notification);
            log.info("알림 전송 -> {} : {}", destination, notification);
        }
    }

    /**
     * 여러 사용자에게 알림 전송 (실제 사용 중)
     */
    public void sendToManyUsers(List<String> userIndexes, Map<String, Object> notification) {
        sendToUserList(userIndexes, notification);
    }
} 