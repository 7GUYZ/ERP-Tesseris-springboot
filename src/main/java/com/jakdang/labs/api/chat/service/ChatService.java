package com.jakdang.labs.api.chat.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.jakdang.labs.api.chat.repository.AjgChatServiceRepository;
import com.jakdang.labs.api.chat.dto.AdminListDTO;
import com.jakdang.labs.api.common.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {
    private final AjgChatServiceRepository ajgChatServiceRepository;

    public ResponseDTO<?> Adminlist() {
        List<AdminListDTO> adminList = ajgChatServiceRepository.findAdminList();
        return ResponseDTO.createSuccessResponse("조회 성공", adminList);
    }
}
