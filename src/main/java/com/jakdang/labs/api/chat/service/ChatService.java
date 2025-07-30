package com.jakdang.labs.api.chat.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jakdang.labs.api.chat.dto.ChatAdminListResponseDto;
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


    /**
     * 채팅용 관리자 리스트 조회
     * 
     * @return 채팅용 관리자 리스트
     */
    public List<ChatAdminListResponseDto> getChatAllAdminList() {
        try {
            log.info("채팅용 관리자 리스트 조회 시작");
            
            List<ChatAdminListResponseDto> adminList = ajgChatServiceRepository.findAllChatAdminList();
            
            log.info("채팅용 관리자 리스트 조회 완료 - 결과 개수: {}", adminList.size());
            
            // 로그로 첫 번째 결과 확인 (디버깅용)
            if (!adminList.isEmpty()) {
                ChatAdminListResponseDto firstAdmin = adminList.get(0);
                log.info("첫 번째 관리자 정보: adminName={}, adminUserIndex={}, adminTypeName={}, adminRankName={}", 
                        firstAdmin.getAdminName(), firstAdmin.getAdminUserIndex(), 
                        firstAdmin.getAdminTypeName(), firstAdmin.getAdminRankName());
            }
            
            return adminList;
            
        } catch (Exception e) {
            log.error("채팅용 관리자 리스트 조회 중 오류 발생", e);
            throw new RuntimeException("채팅용 관리자 리스트 조회에 실패했습니다.", e);
        }
    }

    
}
