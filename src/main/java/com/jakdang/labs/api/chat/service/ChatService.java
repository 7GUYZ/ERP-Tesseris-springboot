package com.jakdang.labs.api.chat.service;

import com.jakdang.labs.api.chat.dto.*;
import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.api.auth.repository.UserRepository;
import com.jakdang.labs.api.deokkyu.store.repository.UserTesserishdkRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final UserTesserishdkRepo userTesserisRepository;
    
    // 온라인 사용자 목록 (실제로는 Redis나 DB를 사용하는 것이 좋습니다)
    private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();
    
    // 채팅방별 참여자 목록
    private final Map<String, Set<String>> roomParticipants = new ConcurrentHashMap<>();
    
    // 채팅방별 메시지 히스토리 (실제로는 DB를 사용하는 것이 좋습니다)
    private final Map<String, List<MessageRequestDTO>> messageHistory = new ConcurrentHashMap<>();
    
    /**
     * 사용자 로그인 처리
     */
    public void handleUserLogin(String userId) {
        try {
            UserEntity userEntity = userRepository.findById(userId).orElse(null);
            if (userEntity == null) {
                log.warn("사용자를 찾을 수 없습니다: {}", userId);
                return;
            }
            
            // 온라인 사용자 목록에 추가
            onlineUsers.add(userId);
            
            // ChatUserDto 생성
            ChatUserDto chatUser = ChatUserDto.builder()
                .id(userId)
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .role(userEntity.getRole() != null ? userEntity.getRole().toString() : "")
                .build();
            
            // 로그인 알림 전송
            ChatLoginLogoutDto loginMessage = ChatLoginLogoutDto.builder()
                .user(chatUser)
                .timestamp(LocalDateTime.now())
                .build();
            
            // 전역 알림
            messagingTemplate.convertAndSend("/topic/user-join", loginMessage);
            
            // 온라인 사용자 목록 업데이트
            updateOnlineUsersList();
            
            log.info("사용자 로그인: {}", userId);
            
        } catch (Exception e) {
            log.error("사용자 로그인 처리 중 오류: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 사용자 로그아웃 처리
     */
    public void handleUserLogout(String userId) {
        try {
            UserEntity userEntity = userRepository.findById(userId).orElse(null);
            if (userEntity == null) {
                log.warn("사용자를 찾을 수 없습니다: {}", userId);
                return;
            }
            
            // 온라인 사용자 목록에서 제거
            onlineUsers.remove(userId);
            
            // 모든 채팅방에서 제거
            roomParticipants.values().forEach(participants -> participants.remove(userId));
            
            // ChatUserDto 생성
            ChatUserDto chatUser = ChatUserDto.builder()
                .id(userId)
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .role(userEntity.getRole() != null ? userEntity.getRole().toString() : "")
                .build();
            
            // 로그아웃 알림 전송
            ChatLoginLogoutDto logoutMessage = ChatLoginLogoutDto.builder()
                .user(chatUser)
                .timestamp(LocalDateTime.now())
                .build();
            
            // 전역 알림
            messagingTemplate.convertAndSend("/topic/user-leave", logoutMessage);
            
            // 온라인 사용자 목록 업데이트
            updateOnlineUsersList();
            
            log.info("사용자 로그아웃: {}", userId);
            
        } catch (Exception e) {
            log.error("사용자 로그아웃 처리 중 오류: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 채팅방 입장
     */
    public void joinRoom(String roomId, String userId) {
        try {
            UserEntity userEntity = userRepository.findById(userId).orElse(null);
            if (userEntity == null) {
                log.warn("사용자를 찾을 수 없습니다: {}", userId);
                return;
            }
            
            // 채팅방 참여자 목록에 추가
            roomParticipants.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(userId);
            
            // 입장 메시지 생성
            MessageRequestDTO joinMessage = new MessageRequestDTO();
            joinMessage.setMessage_index(UUID.randomUUID().toString());
            joinMessage.setRoom_index(roomId);
            joinMessage.setMessage(userEntity.getName() + "님이 입장하셨습니다.");
            joinMessage.setUser_id(userId);
            joinMessage.setSent_at(LocalDateTime.now().toString());
            joinMessage.setActive("Y");
            
            // 채팅방에 메시지 전송
            messagingTemplate.convertAndSend("/topic/room/" + roomId, joinMessage);
            
            // 참여자 목록 업데이트
            updateRoomParticipants(roomId);
            
            log.info("사용자 {}가 채팅방 {}에 입장", userId, roomId);
            
        } catch (Exception e) {
            log.error("채팅방 입장 처리 중 오류: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 채팅 메시지 전송
     */
    public void sendMessage(MessageRequestDTO message) {
        try {
            // 메시지 ID 설정
            if (message.getMessage_index() == null) {
                message.setMessage_index(UUID.randomUUID().toString());
            }
            
            // 전송 시간 설정
            if (message.getSent_at() == null) {
                message.setSent_at(LocalDateTime.now().toString());
            }
            
            // 활성 상태 설정
            if (message.getActive() == null) {
                message.setActive("Y");
            }
            
            // 메시지 히스토리에 추가
            messageHistory.computeIfAbsent(message.getRoom_index(), k -> new ArrayList<>()).add(message);
            
            // 채팅방에 메시지 전송
            messagingTemplate.convertAndSend("/topic/room/" + message.getRoom_index(), message);
            
            log.info("채팅 메시지 전송: roomId={}, userId={}, message={}", 
                    message.getRoom_index(), message.getUser_id(), message.getMessage());
            
        } catch (Exception e) {
            log.error("채팅 메시지 전송 중 오류: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 기존 메시지 조회
     */
    public List<MessageRequestDTO> getMessages(String roomId) {
        return messageHistory.getOrDefault(roomId, new ArrayList<>());
    }
    
    /**
     * 타이핑 상태 전송
     */
    public void sendTypingStatus(ChatTypingDto typingStatus) {
        try {
            messagingTemplate.convertAndSend("/topic/room/" + typingStatus.getRoomId() + "/typing", typingStatus);
            
            log.debug("타이핑 상태 전송: roomId={}, userId={}, isTyping={}", 
                    typingStatus.getRoomId(), typingStatus.getUserId(), typingStatus.isTyping());
            
        } catch (Exception e) {
            log.error("타이핑 상태 전송 중 오류: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 온라인 사용자 목록 업데이트
     */
    private void updateOnlineUsersList() {
        try {
            List<ChatUserDto> onlineUserList = new ArrayList<>();
            
            for (String userId : onlineUsers) {
                UserEntity userEntity = userRepository.findById(userId).orElse(null);
                if (userEntity != null) {
                    onlineUserList.add(ChatUserDto.builder()
                        .id(userId)
                        .name(userEntity.getName())
                        .email(userEntity.getEmail())
                        .role(userEntity.getRole() != null ? userEntity.getRole().toString() : "")
                        .build());
                }
            }
            
            messagingTemplate.convertAndSend("/topic/users", onlineUserList);
            
        } catch (Exception e) {
            log.error("온라인 사용자 목록 업데이트 중 오류: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 채팅방 참여자 목록 업데이트
     */
    private void updateRoomParticipants(String roomId) {
        try {
            Set<String> participants = roomParticipants.get(roomId);
            if (participants != null) {
                List<ChatUserDto> participantList = new ArrayList<>();
                
                for (String userId : participants) {
                    UserEntity userEntity = userRepository.findById(userId).orElse(null);
                    if (userEntity != null) {
                        participantList.add(ChatUserDto.builder()
                            .id(userId)
                            .name(userEntity.getName())
                            .email(userEntity.getEmail())
                            .role(userEntity.getRole() != null ? userEntity.getRole().toString() : "")
                            .build());
                    }
                }
                
                messagingTemplate.convertAndSend("/topic/room/" + roomId + "/participants", participantList);
            }
            
        } catch (Exception e) {
            log.error("채팅방 참여자 목록 업데이트 중 오류: {}", e.getMessage(), e);
        }
    }
}
