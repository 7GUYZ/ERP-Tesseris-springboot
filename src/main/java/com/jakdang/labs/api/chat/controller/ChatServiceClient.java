package com.jakdang.labs.api.chat.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jakdang.labs.api.chat.dto.UserListDTO;
import com.jakdang.labs.api.chat.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatServiceClient {
    private final ChatService chatService;

    @GetMapping("/adminlist")
    public ResponseEntity<List<UserListDTO>> Adminlist() {
        try {
            List<UserListDTO> userList = chatService.Adminlist();
            log.info("User list: {}", userList);
            return ResponseEntity.ok(userList);
        } catch (Exception e) {
            log.error("Error fetching user list: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
}