package com.jakdang.labs.api.alarm.controller;

import java.util.List;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import com.jakdang.labs.api.jungeun.repository.UserTesserisLjeRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {
    private final SimpMessagingTemplate messagingTemplate;
    private final UserTesserisLjeRepo userRepo;

    // 1. 알림 전송 공통 메소드(sendToUserList)
    private void sendToUserList(List<String> userIndexes, Map<String, Object> notification) {
        for (String userIndex : userIndexes) {
            String destination = "/topic/notifications/" + userIndex;
            messagingTemplate.convertAndSend(destination, notification);
            log.info("알림 전송 -> {} : {}", destination, notification);
        }
    }

    // 2-1. 사용자Index 직접 전달하는 API (sendToSingleUser)
    public void sendToSingleUser(String userIndex, Map<String, Object> notification) {
        sendToUserList(List.of(userIndex), notification);
    }

    // 2-2. 사용자Index 직접 전달하는 API (sendToManyUsers)
    public void sendToManyUsers(List<String> userIndexes, Map<String, Object> notification) {
        sendToUserList(userIndexes, notification);
    }

    // 3. user_role_index 기반 사용자 조회 후 전송
    public void sendToUsersByRole(int roleIndex, Map<String, Object> notification) {
        List<String> userIndexes = userRepo.findUserIndexesByRole(roleIndex);
        sendToUserList(userIndexes, notification);
    }

    // 4. 관리자 권한별 전송 (user_role_index = 4, admin_type_index = n)
    public void sendToAdminsByType(int adminTypeIndex, Map<String, Object> notification) {
        List<String> userIndexes = userRepo.findAdminIndexesByType(adminTypeIndex);
        sendToUserList(userIndexes, notification);
    }

} 