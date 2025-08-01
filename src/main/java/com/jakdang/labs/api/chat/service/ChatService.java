// package com.jakdang.labs.api.chat.service;

<<<<<<< HEAD
// import com.jakdang.labs.api.chat.dto.*;
// import com.jakdang.labs.api.auth.entity.UserEntity;
// import com.jakdang.labs.entity.UserTesseris;
// import com.jakdang.labs.api.auth.repository.UserRepository;
// import com.jakdang.labs.api.deokkyu.store.repository.UserTesserishdkRepo;
// import com.jakdang.labs.api.deokkyu.admin.dto.AdminListResponseDto;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.messaging.simp.SimpMessagingTemplate;
// import org.springframework.stereotype.Service;

// import java.time.LocalDateTime;
// import java.time.ZoneId;
// import java.util.*;
// import java.util.concurrent.ConcurrentHashMap;

// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class ChatService {
    
//     private final SimpMessagingTemplate messagingTemplate;
//     private final UserRepository userRepository;
//     private final UserTesserishdkRepo userTesserisRepository;
    
//     // 온라인 사용자 목록 (실제로는 Redis나 DB를 사용하는 것이 좋습니다)
//     private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();
    
//     // 채팅방별 참여자 목록
//     private final Map<String, Set<String>> roomParticipants = new ConcurrentHashMap<>();
    
//     // 채팅방별 메시지 히스토리 (실제로는 DB를 사용하는 것이 좋습니다)
//     private final Map<String, List<MessageRequestDTO>> messageHistory = new ConcurrentHashMap<>();
    
//     /**
//      * 사용자 로그인 처리
//      */
//     public void handleUserLogin(String userId) {
//         try {
//             UserEntity userEntity = userRepository.findById(userId).orElse(null);
//             if (userEntity == null) {
//                 log.warn("사용자를 찾을 수 없습니다: {}", userId);
//                 return;
//             }
            
//             // 온라인 사용자 목록에 추가
//             onlineUsers.add(userId);
            
//             // ChatUserDto 생성
//             ChatUserDto chatUser = ChatUserDto.builder()
//                 .id(userId)
//                 .name(userEntity.getName())
//                 .email(userEntity.getEmail())
//                 .role(userEntity.getRole() != null ? userEntity.getRole().toString() : "")
//                 .build();
            
//             // 로그인 알림 전송
//             ChatLoginLogoutDto loginMessage = ChatLoginLogoutDto.builder()
//                 .user(chatUser)
//                 .timestamp(LocalDateTime.now())
//                 .build();
            
//             // 전역 알림
//             messagingTemplate.convertAndSend("/topic/user-join", loginMessage);
            
//             // 온라인 사용자 목록 업데이트
//             updateOnlineUsersList();
            
//             log.info("사용자 로그인: {}", userId);
            
//         } catch (Exception e) {
//             log.error("사용자 로그인 처리 중 오류: {}", e.getMessage(), e);
//         }
//     }
    
//     /**
//      * 사용자 로그아웃 처리
//      */
//     public void handleUserLogout(String userId) {
//         try {
//             UserEntity userEntity = userRepository.findById(userId).orElse(null);
//             if (userEntity == null) {
//                 log.warn("사용자를 찾을 수 없습니다: {}", userId);
//                 return;
//             }
            
//             // 온라인 사용자 목록에서 제거
//             onlineUsers.remove(userId);
            
//             // 모든 채팅방에서 제거
//             roomParticipants.values().forEach(participants -> participants.remove(userId));
            
//             // ChatUserDto 생성
//             ChatUserDto chatUser = ChatUserDto.builder()
//                 .id(userId)
//                 .name(userEntity.getName())
//                 .email(userEntity.getEmail())
//                 .role(userEntity.getRole() != null ? userEntity.getRole().toString() : "")
//                 .build();
            
//             // 로그아웃 알림 전송
//             ChatLoginLogoutDto logoutMessage = ChatLoginLogoutDto.builder()
//                 .user(chatUser)
//                 .timestamp(LocalDateTime.now())
//                 .build();
            
//             // 전역 알림
//             messagingTemplate.convertAndSend("/topic/user-leave", logoutMessage);
            
//             // 온라인 사용자 목록 업데이트
//             updateOnlineUsersList();
            
//             log.info("사용자 로그아웃: {}", userId);
            
//         } catch (Exception e) {
//             log.error("사용자 로그아웃 처리 중 오류: {}", e.getMessage(), e);
//         }
//     }
    
//     /**
//      * 채팅방 입장
//      */
//     public void joinRoom(String roomId, String userId) {
//         try {
//             UserEntity userEntity = userRepository.findById(userId).orElse(null);
//             if (userEntity == null) {
//                 log.warn("사용자를 찾을 수 없습니다: {}", userId);
//                 return;
//             }
            
//             // 채팅방 참여자 목록에 추가
//             roomParticipants.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(userId);
            
//             // 입장 메시지 생성
//             MessageRequestDTO joinMessage = new MessageRequestDTO();
//             joinMessage.setMessage_index(UUID.randomUUID().toString());
//             joinMessage.setRoom_index(roomId);
//             joinMessage.setMessage(userEntity.getName() + "님이 입장하셨습니다.");
//             joinMessage.setUser_id(userId);
//             joinMessage.setSent_at(LocalDateTime.now().toString());
//             joinMessage.setActive("Y");
            
//             // 채팅방에 메시지 전송
//             messagingTemplate.convertAndSend("/topic/room/" + roomId, joinMessage);
            
//             // 참여자 목록 업데이트
//             updateRoomParticipants(roomId);
            
//             log.info("사용자 {}가 채팅방 {}에 입장", userId, roomId);
            
//         } catch (Exception e) {
//             log.error("채팅방 입장 처리 중 오류: {}", e.getMessage(), e);
//         }
//     }
    
//     /**
//      * 채팅 메시지 전송
//      */
//     public void sendMessage(MessageRequestDTO message) {
//         try {
//             // 메시지 ID 설정
//             if (message.getMessage_index() == null) {
//                 message.setMessage_index(UUID.randomUUID().toString());
//             }
            
//             // 전송 시간 설정
//             if (message.getSent_at() == null) {
//                 message.setSent_at(LocalDateTime.now().toString());
//             }
            
//             // 활성 상태 설정
//             if (message.getActive() == null) {
//                 message.setActive("Y");
//             }
            
//             // 메시지 히스토리에 추가
//             messageHistory.computeIfAbsent(message.getRoom_index(), k -> new ArrayList<>()).add(message);
            
//             // 채팅방에 메시지 전송
//             messagingTemplate.convertAndSend("/topic/room/" + message.getRoom_index(), message);
            
//             log.info("채팅 메시지 전송: roomId={}, userId={}, message={}", 
//                     message.getRoom_index(), message.getUser_id(), message.getMessage());
            
//         } catch (Exception e) {
//             log.error("채팅 메시지 전송 중 오류: {}", e.getMessage(), e);
//         }
//     }
    
//     /**
//      * 기존 메시지 조회
//      */
//     public List<MessageRequestDTO> getMessages(String roomId) {
//         return messageHistory.getOrDefault(roomId, new ArrayList<>());
//     }
    
//     /**
//      * 타이핑 상태 전송
//      */
//     public void sendTypingStatus(ChatTypingDto typingStatus) {
//         try {
//             messagingTemplate.convertAndSend("/topic/room/" + typingStatus.getRoomId() + "/typing", typingStatus);
            
//             log.debug("타이핑 상태 전송: roomId={}, userId={}, isTyping={}", 
//                     typingStatus.getRoomId(), typingStatus.getUserId(), typingStatus.isTyping());
            
//         } catch (Exception e) {
//             log.error("타이핑 상태 전송 중 오류: {}", e.getMessage(), e);
//         }
//     }
    
//     /**
//      * 온라인 사용자 목록 업데이트
//      */
//     private void updateOnlineUsersList() {
//         try {
//             List<ChatUserDto> onlineUserList = new ArrayList<>();
            
//             for (String userId : onlineUsers) {
//                 UserEntity userEntity = userRepository.findById(userId).orElse(null);
//                 if (userEntity != null) {
//                     onlineUserList.add(ChatUserDto.builder()
//                         .id(userId)
//                         .name(userEntity.getName())
//                         .email(userEntity.getEmail())
//                         .role(userEntity.getRole() != null ? userEntity.getRole().toString() : "")
//                         .build());
//                 }
//             }
            
//             messagingTemplate.convertAndSend("/topic/users", onlineUserList);
            
//         } catch (Exception e) {
//             log.error("온라인 사용자 목록 업데이트 중 오류: {}", e.getMessage(), e);
//         }
//     }
    
//     /**
//      * 채팅방 참여자 목록 업데이트
//      */
//     private void updateRoomParticipants(String roomId) {
//         try {
//             Set<String> participants = roomParticipants.get(roomId);
//             if (participants != null) {
//                 List<ChatUserDto> participantList = new ArrayList<>();
                
//                 for (String userId : participants) {
//                     UserEntity userEntity = userRepository.findById(userId).orElse(null);
//                     if (userEntity != null) {
//                         participantList.add(ChatUserDto.builder()
//                             .id(userId)
//                             .name(userEntity.getName())
//                             .email(userEntity.getEmail())
//                             .role(userEntity.getRole() != null ? userEntity.getRole().toString() : "")
//                             .build());
//                     }
//                 }
                
//                 messagingTemplate.convertAndSend("/topic/room/" + roomId + "/participants", participantList);
//             }
            
//         } catch (Exception e) {
//             log.error("채팅방 참여자 목록 업데이트 중 오류: {}", e.getMessage(), e);
//         }
//     }
=======
import org.springframework.stereotype.Service;
import com.jakdang.labs.api.chat.repository.AjgChatServiceRepository;
import com.jakdang.labs.api.common.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
>>>>>>> jihun

//     // =============== 관리자 리스트 조회 기능 ===============

<<<<<<< HEAD
//     /**
//      * 채팅용 관리자 리스트 조회
//      * AdminListResponseDto 형식으로 반환
//      */
//     public List<AdminListResponseDto> getChatAllAdminList() {
//         log.info("=== 채팅용 관리자 리스트 조회 서비스 시작 ===");
        
//         try {
//             // UserTesseris 엔티티에서 관리자 역할 사용자들 조회
//             List<UserTesseris> userTesserisAdmins = userTesserisRepository.findAll();
//             List<AdminListResponseDto> adminList = new ArrayList<>();
            
//             for (UserTesseris userTesseris : userTesserisAdmins) {
//                 // UserTesseris에서 연결된 UserEntity 가져오기
//                 UserEntity userEntity = userTesseris.getUsersId();
                
//                 if (userEntity != null) {
//                     // AdminListResponseDto 생성
//                     AdminListResponseDto adminDto = AdminListResponseDto.builder()
//                         .adminUserEmail(userEntity.getEmail())
//                         .adminUserName(userEntity.getName())
//                         .adminUserPhone(userEntity.getPhone())
//                         .adminTypeName("관리자") // 기본값, 추후 실제 admin_type 테이블과 연동
//                         .adminRankName(userEntity.getRole() != null ? userEntity.getRole().toString() : "USER")
//                         .adminRegistrationDate(userEntity.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime())
//                         .build();
                        
//                     adminList.add(adminDto);
                    
//                     log.debug("관리자 정보 추가: email={}, name={}", 
//                              userEntity.getEmail(), userEntity.getName());
//                 }
//             }
            
//             log.info("✅ 채팅용 관리자 리스트 조회 완료 - 총 {}명", adminList.size());
//             return adminList;
            
//         } catch (Exception e) {
//             log.error("❌ 채팅용 관리자 리스트 조회 중 오류 발생");
//             log.error("❌ 오류 타입: {}", e.getClass().getSimpleName());
//             log.error("❌ 오류 메시지: {}", e.getMessage());
//             log.error("❌ 오류 상세: ", e);
//             return new ArrayList<>();
//         }
//     }

//     /**
//      * 간단한 사용자 리스트 조회 (UserListDTO 형식)
//      * 기존 Adminlist() 메서드 구현
//      */
//     public List<UserListDTO> Adminlist() {
//         log.info("=== 간단한 관리자 리스트 조회 시작 ===");
        
//         try {
//             List<UserTesseris> userTesserisAdmins = userTesserisRepository.findAll();
//             List<UserListDTO> userList = new ArrayList<>();
            
//             for (UserTesseris userTesseris : userTesserisAdmins) {
//                 UserEntity userEntity = userTesseris.getUsersId();
                
//                 if (userEntity != null) {
//                     // UserListDTO의 실제 필드 구조에 맞게 생성
//                     UserListDTO userDto = new UserListDTO(
//                         userTesseris.getUserIndex().toString(),  // user_index
//                         userEntity.getId(),                      // users_id
//                         userTesseris.getUserRoleIndex().toString(), // user_role_index
//                         userEntity.getName()                     // name
//                     );
                    
//                     userList.add(userDto);
                    
//                     log.debug("사용자 정보 추가: user_index={}, users_id={}, name={}", 
//                              userTesseris.getUserIndex(), userEntity.getId(), userEntity.getName());
//                 }
//             }
            
//             log.info("✅ 간단한 관리자 리스트 조회 완료 - 총 {}명", userList.size());
//             return userList;
            
//         } catch (Exception e) {
//             log.error("❌ 간단한 관리자 리스트 조회 중 오류 발생: {}", e.getMessage(), e);
//             return new ArrayList<>();
//         }
//     }
// }
=======
    public ResponseDTO<?> Adminlist() {
        return ResponseDTO.createSuccessResponse("조회 성공", ajgChatServiceRepository.findAdminList());
    }
}
>>>>>>> jihun
