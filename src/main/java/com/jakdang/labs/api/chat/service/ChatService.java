package com.jakdang.labs.api.chat.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jakdang.labs.api.chat.dto.UserListDTO;
import com.jakdang.labs.api.chat.repository.AjgChatServiceRepository;
import com.jakdang.labs.entity.UserTesseris;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {
    private final AjgChatServiceRepository ajgChatServiceRepository;

    public List<UserListDTO> Adminlist() {
        List<UserTesseris> userList = ajgChatServiceRepository.findAllAdmin();
        return userList.stream()
                .map(user -> new UserListDTO(
                        String.valueOf(user.getUserIndex()),
                        user.getUsersId() != null ? user.getUsersId().getId() : null,
                        String.valueOf(user.getUserRoleIndex()),
                        user.getUsersId() != null ? user.getUsersId().getName() : "Unknown"))
                .collect(Collectors.toList());
    }
}
