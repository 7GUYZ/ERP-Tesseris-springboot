package com.jakdang.labs.api.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakdang.labs.api.chat.dto.AdminListDTO;
import com.jakdang.labs.api.chat.dto.AlarmCheckRequestDTO;
import com.jakdang.labs.api.chat.dto.InvitationRequestDTO;
import com.jakdang.labs.api.chat.dto.MessageRequestDTO;
import com.jakdang.labs.api.chat.dto.RoomRequestDTO;
import com.jakdang.labs.api.chat.model.ChatServiceClient;
import com.jakdang.labs.api.chat.service.ChatService;
import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.deokkyu.storeRegister.service.S3FileUploadService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import java.util.stream.Collectors;

@Component
@RestController
@RequestMapping("/api/adminchat")
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatService chatService;
    private final ChatServiceClient chatServiceClient;
    private final ObjectMapper objectMapper;
    private final S3FileUploadService s3FileUploadService;
    private final SimpMessagingTemplate messagingTemplate;

    // ==================== WebSocket STOMP 메시지 처리 ====================

    /**
     * 채팅 메시지 전송 처리 (단체 채팅방)
     * 클라이언트에서 /app/adminchat.sendMessage/{roomId}로 전송
     * 새 방 생성 시: 방 생성 + 구독 설정 + 메시지 브로드캐스트
     * 기존 방: 메시지 브로드캐스트
     */
    @MessageMapping("/adminchat.sendMessage/{roomId}")
    public void sendMessage(@PathVariable String roomId, @Payload Map<String, Object> messageData) {
        log.info("=== 채팅 메시지 수신 시작 ===");
        log.info("채팅 메시지 수신: {}", messageData);
        log.info("원본 roomId: {}", roomId);

        try {
            // 1. 프론트에서 전송한 데이터 추출
            String userId = (String) messageData.get("user_id");
            String message = (String) messageData.get("message");
            
            // 2. room_index 추출 및 새 방 생성 여부 판단
            Object roomIndexObj = messageData.get("room_index");
            String roomIndex = null;
            boolean isNewRoomCreation = false;
            
            if (roomIndexObj == null || "null".equals(String.valueOf(roomIndexObj))) {
                // room_index가 null이면 새 방 생성
                isNewRoomCreation = true;
                log.info("새 방 생성 감지: room_index가 null");
            } else {
                // room_index가 있으면 기존 방 사용
                roomIndex = String.valueOf(roomIndexObj);
                log.info("기존 방 사용: room_index={}", roomIndex);
            }

            // 파일 업로드는 HTTP 방식으로 처리하므로 WebSocket에서는 파일 정보만 전달받음
            Object filesObj = messageData.get("files");
            List<String> uploadFiles = new ArrayList<>();

            if (filesObj != null && filesObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> filesList = (List<Object>) filesObj;

                for (Object fileObj : filesList) {
                    if (fileObj instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> fileData = (Map<String, Object>) fileObj;

                        // HTTP 업로드에서 이미 S3 URL이 전달됨
                        String fileUrl = (String) fileData.get("url");
                        if (fileUrl != null) {
                            uploadFiles.add(fileUrl);
                        }
                    }
                }
            }

            // 3. MessageRequestDTO 생성 및 설정
            MessageRequestDTO messageRequestDTO = new MessageRequestDTO();
            messageRequestDTO.setUser_id(userId);
            messageRequestDTO.setMessage(message);
            messageRequestDTO.setRoom_index(roomIndex); // null이면 새 방 생성, 있으면 기존 방 사용
            messageRequestDTO.setRoom_name((String) messageData.get("room_name"));
            messageRequestDTO.setParticipants(new ArrayList<>());
            messageRequestDTO.setTimestamp(null);
            messageRequestDTO.setUploadFiles(new ArrayList<>());

            String roomName = (String) messageData.get("room_name");
            Object participantsObj = messageData.get("participants");
            log.info("participants 원본 데이터: {}", participantsObj);
            log.info("participants 타입: {}", participantsObj != null ? participantsObj.getClass().getName() : "null");

            List<String> participants;
            if (participantsObj instanceof List) {
                participants = (List<String>) participantsObj;
            } else if (participantsObj instanceof String) {
                // JSON 문자열인 경우 파싱
                try {
                    participants = objectMapper.readValue((String) participantsObj, List.class);
                } catch (Exception e) {
                    log.error("participants JSON 파싱 실패: {}", e.getMessage());
                    participants = new ArrayList<>();
                }
            } else {
                participants = new ArrayList<>();
            }

            log.info("파싱된 participants: {}", participants);

            messageRequestDTO.setUser_id(userId);
            messageRequestDTO.setMessage(message);
            
            // 새 방 생성인 경우 room_index를 null로 설정하여 서비스에서 자동 생성하도록 함
            if (isNewRoomCreation) {
                messageRequestDTO.setRoom_index(null);
                log.info("새 방 생성으로 room_index를 null로 설정");
            } else {
                messageRequestDTO.setRoom_index(roomIndex);
            }
            
            messageRequestDTO.setRoom_name(roomName);
            messageRequestDTO.setParticipants(participants != null ? participants : new ArrayList<>());
            messageRequestDTO.setTimestamp(null);
            messageRequestDTO.setUploadFiles(uploadFiles);

            // 3. 채팅 서비스를 통해 메시지 저장 및 방 관리
            log.info("채팅 서비스 호출 시작: {}", messageRequestDTO);
            ResponseDTO<?> response = chatServiceClient.SendMessage(messageRequestDTO);
            log.info("채팅 서비스 응답: {}", response);

            // 4. 응답에서 room_index와 messageindex 추출하여 메시지 데이터에 추가
            if (response != null && response.getData() != null) {
                log.info("응답 데이터 존재: {}", response.getData());
                if (response.getData() instanceof Map) {
                    Map<String, Object> responseData = (Map<String, Object>) response.getData();
                    messageData.put("room_index", responseData.get("room_index"));
                    messageData.put("messageindex", responseData.get("messageindex"));

                    log.info("Map 응답에서 추출: room_index={}, messageindex={}",
                            responseData.get("room_index"), responseData.get("messageindex"));

                    // 임시 messageindex가 있으면 응답에 포함
                    String tempMessageIndex = (String) messageData.get("tempMessageIndex");
                    if (tempMessageIndex != null) {
                        messageData.put("tempMessageIndex", tempMessageIndex);
                        log.info("임시 messageindex 포함: {}", tempMessageIndex);
                    } else {
                        log.warn("임시 messageindex가 없음");
                    }

                    log.info("메시지 저장 응답: room_index={}, messageindex={}, tempMessageIndex={}",
                            responseData.get("room_index"), responseData.get("messageindex"), tempMessageIndex);
                } else {
                    // 기존 호환성을 위해 단순 값도 처리
                    messageData.put("room_index", response.getData());
                    log.info("단순 값 응답에서 추출: room_index={}", response.getData());

                    // 임시 messageindex가 있으면 응답에 포함
                    String tempMessageIndex = (String) messageData.get("tempMessageIndex");
                    if (tempMessageIndex != null) {
                        messageData.put("tempMessageIndex", tempMessageIndex);
                        log.info("임시 messageindex 포함: {}", tempMessageIndex);
                    } else {
                        log.warn("임시 messageindex가 없음");
                    }
                }

                                 // 4-1. 새 채팅방 생성 시 해당 room_index로 직접 응답
                 if (isNewRoomCreation) {
                     log.info("📡 새 방 생성 완료 - room_index: {}로 브로드캐스트", messageData.get("room_index"));
                 }
            }

            // 5. 메시지에 타임스탬프 추가
            messageData.put("timestamp", System.currentTimeMillis());

            // 6. 발신자 정보 추가 (프론트에서 전송한 정보 사용)
            messageData.put("sender", userId);
            messageData.put("user_id", userId);

            // 7. 채팅방 목록 업데이트를 위한 정보 추가
            messageData.put("message_type", "chat");
            
            // 새 방 생성 시에는 응답에서 받은 room_index 사용, 기존 방은 원래 roomIndex 사용
            if (isNewRoomCreation && response != null && response.getData() != null) {
                String responseRoomIndex;
                if (response.getData() instanceof Map) {
                    Map<String, Object> responseData = (Map<String, Object>) response.getData();
                    responseRoomIndex = String.valueOf(responseData.get("room_index"));
                } else {
                    responseRoomIndex = String.valueOf(response.getData());
                }
                messageData.put("room_id", responseRoomIndex);
                messageData.put("room_index", responseRoomIndex);
                log.info("새 방 생성으로 응답에서 받은 room_index 설정: {}", responseRoomIndex);
            } else {
                messageData.put("room_id", roomIndex);
                messageData.put("room_index", roomIndex);
            }

                         // 8. 파일 정보 추가 (DB에서 조회한 파일 정보)
             if (!uploadFiles.isEmpty()) {
                 try {
                     // 채팅 서비스에서 파일 정보 조회
                     List<Map<String, Object>> fileInfoList = new ArrayList<>();
                     for (String fileUrl : uploadFiles) {
                         Map<String, Object> fileInfo = new HashMap<>();
                         fileInfo.put("url", fileUrl);
                         fileInfo.put("name", extractFileNameFromUrl(fileUrl));
                         fileInfo.put("type", extractFileTypeFromUrl(fileUrl));
                         fileInfo.put("size", 0); // S3에서 직접 조회하지 않으므로 0으로 설정
                         fileInfoList.add(fileInfo);
                     }
                     messageData.put("files", fileInfoList);
                     log.info("📁 파일 정보 추가: {}", fileInfoList);
                 } catch (Exception e) {
                     log.error("파일 정보 추가 실패: {}", e.getMessage());
                 }
             } else {
                 // 파일이 없는 경우 빈 배열로 설정
                 messageData.put("files", new ArrayList<>());
                 log.info("📁 파일 없음 - 빈 배열 설정");
             }

             // 9. 발신자 이름 정보 추가 (관리자 목록에서 조회)
             try {
                 log.info("발신자 이름 조회 시작: userId={}", userId);
                 ResponseDTO<?> adminListResponse = chatService.Adminlist();
                 if (adminListResponse != null && adminListResponse.getData() != null) {
                     @SuppressWarnings("unchecked")
                     List<AdminListDTO> adminList = (List<AdminListDTO>) adminListResponse.getData();
                     log.info("관리자 목록 조회 성공: {}명", adminList.size());

                     boolean found = false;
                     for (AdminListDTO admin : adminList) {
                         String adminUserId = admin.getUserId();
                         String adminName = admin.getName();
                         log.debug("관리자 정보: userId={}, name={}", adminUserId, adminName);

                         if (userId.equals(adminUserId)) {
                             messageData.put("sender_name", adminName);
                             log.info("발신자 이름 찾음: {} -> {}", userId, adminName);
                             found = true;
                             break;
                         }
                     }

                     if (!found) {
                         log.warn("발신자 이름을 찾을 수 없음: userId={}", userId);
                         messageData.put("sender_name", "Unknown");
                     }
                 } else {
                     log.warn("관리자 목록 응답이 null이거나 데이터가 없음");
                     messageData.put("sender_name", "Unknown");
                 }
             } catch (Exception e) {
                 log.error("발신자 이름 조회 실패: {}", e.getMessage(), e);
                 messageData.put("sender_name", "Unknown");
             }

             log.info("채팅 메시지 브로드캐스트: {}", messageData);

                         // 8. 브로드캐스트 전송
             if (!isNewRoomCreation) {
                 // 기존 방: 해당 방으로 브로드캐스트
                 messagingTemplate.convertAndSend("/queue/" + roomIndex, messageData);
                 log.info("📡 기존 방 메시지 브로드캐스트 완료: /queue/{}", roomIndex);
             } else {
                 // 새 방 생성: admin 방으로 브로드캐스트 (연속성 유지)
                 messagingTemplate.convertAndSend("/queue/admin", messageData);
                 log.info("📡 새 방 생성 메시지 브로드캐스트 완료: /queue/admin (연속성 유지)");
             }

        } catch (Exception e) {
            log.error("메시지 처리 중 오류 발생: {}", e.getMessage());
            messageData.put("error", "메시지 전송 실패");
            messageData.put("timestamp", System.currentTimeMillis());
            // 오류 메시지도 브로드캐스트 (기본값 "1" 사용)
            messagingTemplate.convertAndSend("/queue/1", messageData);
        }
    }

    /**
     * 채팅방 입장 처리 (기존 방)
     * 클라이언트에서 /app/adminchat.joinRoom/{roomId}로 전송
     * 방 존재 확인 + 구독 설정 + 기존 메시지 전송
     */
    @MessageMapping("/adminchat.joinRoom/{roomId}")
    @SendTo("/queue/{roomId}")
    public Map<String, Object> joinRoom(@PathVariable String roomId, @Payload Map<String, Object> joinMessage) {

        String userId = (String) joinMessage.get("user_id");
        log.info("채팅방 입장 요청: roomId={}, user={}", roomId, userId);

        try {
            // 1. roomId가 JSON 객체인 경우 처리 (프론트엔드에서 잘못 전달된 경우)
            String cleanRoomId = roomId;
            if (roomId.startsWith("{") && roomId.contains("user_id")) {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> roomIdObj = objectMapper.readValue(roomId, Map.class);
                    // JSON 객체에서 room_index 추출 시도 (우선순위: room_index > id > roomId > roomid)
                    Object roomIndexObj = roomIdObj.get("room_index");
                    if (roomIndexObj == null) {
                        roomIndexObj = roomIdObj.get("id");
                    }
                    if (roomIndexObj == null) {
                        roomIndexObj = roomIdObj.get("roomId");
                    }
                    if (roomIndexObj == null) {
                        roomIndexObj = roomIdObj.get("roomid");
                    }
                    
                    if (roomIndexObj != null) {
                        cleanRoomId = String.valueOf(roomIndexObj);
                        log.info("JSON roomId에서 room_index 추출: {} -> {}", roomId, cleanRoomId);
                                         } else {
                         log.warn("JSON roomId에서 room_index를 찾을 수 없음: {}", roomId);
                         // 오류 메시지 대신 기본값 사용
                         cleanRoomId = "1";
                         log.info("기본값으로 roomId 설정: {}", cleanRoomId);
                     }
                                 } catch (Exception e) {
                     log.error("JSON roomId 파싱 실패: {}", e.getMessage());
                     // 오류 메시지 대신 기본값 사용
                     cleanRoomId = "1";
                     log.info("파싱 실패로 기본값 설정: {}", cleanRoomId);
                 }
            }

            // 2. 방 존재 여부 확인 (정리된 roomId 사용)
            ResponseDTO<?> roomCheckResponse = chatServiceClient.SearchRoom(userId);
            boolean roomExists = false;
            String actualRoomIndex = cleanRoomId;

            if (roomCheckResponse != null && roomCheckResponse.getData() != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> roomList = (List<Map<String, Object>>) roomCheckResponse.getData();

                for (Map<String, Object> room : roomList) {
                    String roomIndex = String.valueOf(room.get("room_index"));
                    if (roomIndex.equals(cleanRoomId)) {
                        roomExists = true;
                        actualRoomIndex = roomIndex;
                        break;
                    }
                }
            }

                         if (!roomExists) {
                 log.warn("방이 존재하지 않음: roomId={}, cleanRoomId={}", roomId, cleanRoomId);
                 // 오류 메시지 대신 기본 방으로 처리
                 actualRoomIndex = "1";
                 log.info("존재하지 않는 방을 기본값으로 설정: {}", actualRoomIndex);
             }

            // 2. 기존 메시지 조회 (최근 25개)
            ResponseDTO<?> chatResponse = chatServiceClient.ChatList(actualRoomIndex, userId, 0, 25);
            List<Map<String, Object>> existingMessages = new ArrayList<>();

            if (chatResponse != null && chatResponse.getData() != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> messages = (List<Map<String, Object>>) chatResponse.getData();
                existingMessages = messages;
            }

                         // 3. 입장 성공 응답 (기존 메시지 포함, 입장 알림 없음)
             Map<String, Object> joinResponse = new HashMap<>();
             joinResponse.put("type", "join_success");
             joinResponse.put("roomId", actualRoomIndex);
             joinResponse.put("userId", userId);
             joinResponse.put("existingMessages", existingMessages);
             joinResponse.put("timestamp", System.currentTimeMillis());
             // 입장 알림은 명시적인 입장 요청 시에만 발생하도록 제거

            log.info("채팅방 입장 성공: roomId={}, user={}, 기존 메시지 수={}", actualRoomIndex, userId, existingMessages.size());
            return joinResponse;

                 } catch (Exception e) {
             log.error("채팅방 입장 처리 중 오류: {}", e.getMessage());
                           // 오류 발생 시에도 기본 응답 반환
              Map<String, Object> joinResponse = new HashMap<>();
              joinResponse.put("type", "join_success");
              joinResponse.put("roomId", "1");
              joinResponse.put("userId", userId);
              joinResponse.put("existingMessages", new ArrayList<>());
              joinResponse.put("timestamp", System.currentTimeMillis());
              // 입장 알림은 명시적인 입장 요청 시에만 발생하도록 제거
             
             log.info("오류 발생으로 기본 응답 반환: roomId=1");
             return joinResponse;
         }
    }

         /**
      * 채팅방 명시적 입장 알림 (초대된 사용자가 방에 입장할 때)
      */
     @MessageMapping("/adminchat.enterRoom/{roomId}")
     @SendTo("/queue/{roomId}")
     public Map<String, Object> enterRoom(@PathVariable String roomId, @Payload Map<String, Object> enterMessage) {
         
         String userId = (String) enterMessage.get("user_id");
         log.info("채팅방 명시적 입장: roomId={}, user={}", roomId, userId);
         
         // 사용자 이름 조회
         String userName = "알 수 없음";
         try {
             ResponseDTO<?> adminResponse = chatService.Adminlist();
             if (adminResponse.getData() != null) {
                 @SuppressWarnings("unchecked")
                 List<AdminListDTO> adminList = (List<AdminListDTO>) adminResponse.getData();
                 log.info("관리자 목록 조회: {}명", adminList.size());
                 
                 for (AdminListDTO admin : adminList) {
                     log.debug("관리자 정보: userId={}, name={}", admin.getUserId(), admin.getName());
                     if (userId.equals(admin.getUserId())) {
                         userName = admin.getName();
                         log.info("사용자 이름 찾음: {} -> {}", userId, userName);
                         break;
                     }
                 }
                 
                 if ("알 수 없음".equals(userName)) {
                     log.warn("사용자 이름을 찾을 수 없음: userId={}", userId);
                 }
             }
         } catch (Exception e) {
             log.error("사용자 이름 조회 실패: {}", e.getMessage());
         }
         
         // 입장 알림 메시지 브로드캐스트 (사용자 이름 사용)
         Map<String, Object> systemMessage = Map.of(
                 "type", "system",
                 "message", userName + "님이 입장했습니다.",
                 "timestamp", System.currentTimeMillis(),
                 "roomId", roomId,
                 "userId", userId,
                 "action", "enter");
         
         return systemMessage;
     }
     
     /**
      * 채팅방 퇴장 처리 (단체 채팅방) - 명시적 퇴장 요청 시에만
      */
     @MessageMapping("/adminchat.leaveRoom/{roomId}")
     @SendTo("/queue/{roomId}")
     public Map<String, Object> leaveRoom(@PathVariable String roomId, @Payload Map<String, Object> leaveMessage) {

        String userId = (String) leaveMessage.get("user_id");
        String messageType = (String) leaveMessage.get("type");

        log.info("채팅방 퇴장: roomId={}, user={}, type={}", roomId, userId, messageType);

        // 사용자 이름 조회
        String userName = "알 수 없음";
        try {
            ResponseDTO<?> adminResponse = chatService.Adminlist();
            if (adminResponse.getData() != null) {
                @SuppressWarnings("unchecked")
                List<AdminListDTO> adminList = (List<AdminListDTO>) adminResponse.getData();
                log.info("관리자 목록 조회: {}명", adminList.size());
                
                for (AdminListDTO admin : adminList) {
                    log.debug("관리자 정보: userId={}, name={}", admin.getUserId(), admin.getName());
                    if (userId.equals(admin.getUserId())) {
                        userName = admin.getName();
                        log.info("사용자 이름 찾음: {} -> {}", userId, userName);
                        break;
                    }
                }
                
                if ("알 수 없음".equals(userName)) {
                    log.warn("사용자 이름을 찾을 수 없음: userId={}", userId);
                }
            }
        } catch (Exception e) {
            log.error("사용자 이름 조회 실패: {}", e.getMessage());
        }

        // 퇴장 메시지 브로드캐스트 (사용자 이름 사용)
        Map<String, Object> systemMessage = Map.of(
                "type", "system",
                "message", userName + "님이 퇴장했습니다.",
                "timestamp", System.currentTimeMillis(),
                "roomId", roomId,
                "userId", userId,
                "action", "leave");

        // 서버 측에서 사용자 상태 업데이트 (필요시)
        try {
            // 사용자가 방에서 나갔음을 서버에 기록
            log.info("사용자 {}가 방 {}에서 퇴장했습니다.", userId, roomId);
        } catch (Exception e) {
            log.error("사용자 퇴장 상태 업데이트 실패: {}", e.getMessage());
        }

        return systemMessage;
    }

    // ==================== HTTP REST API (필수 기능만 유지) ====================

    /**
     * 관리자 목록 조회 (채팅방 생성 시 필요)
     */
    @GetMapping("/adminlist")
    public ResponseEntity<ResponseDTO<?>> Adminlist() {
        try {
            ResponseDTO<?> result = chatService.Adminlist();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error fetching admin list: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 채팅방 목록 조회 (단체 채팅방)
     */
    @GetMapping("/{userid}")
    public ResponseEntity<ResponseDTO<?>> SearchRoom(@PathVariable("userid") String userid) {
        try {
            ResponseDTO<?> roomList = chatServiceClient.SearchRoom(userid);
            log.info("SearchRoom: {}", roomList);
            return ResponseEntity.ok(roomList);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.ok(new ResponseDTO<>(404, e.getMessage(), null));
        }
    }

    /**
     * 채팅방 채팅 내용 조회 (단체 채팅방)
     * 입장한사람 읽음처리
     */
    @GetMapping("/{room}/chatlist/{userid}")
    public ResponseEntity<?> ChatList(@PathVariable("room") String room,
            @PathVariable("userid") String userid,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "25") int size) {
        try {
            log.info("ChatList: room={}, userid={}, page={}, size={}", room, userid, page, size);

            // 채팅 내용 조회
            ResponseDTO<?> chatResponse = chatServiceClient.ChatList(room, userid, page, size);

            // 관리자 정보 조회
            ResponseDTO<?> adminResponse = chatService.Adminlist();

            // 응답 데이터에 관리자 정보 추가
            if (chatResponse != null && chatResponse.getData() != null) {
                // 기존 채팅 데이터를 Map으로 변환
                Map<String, Object> chatDataMap = new HashMap<>();

                // 채팅 데이터가 List인 경우 그대로 유지
                if (chatResponse.getData() instanceof List) {
                    chatDataMap.put("messages", chatResponse.getData());
                } else {
                    chatDataMap.put("messages", chatResponse.getData());
                }

                // 관리자 정보 추가
                chatDataMap.put("adminList", adminResponse != null ? adminResponse.getData() : null);

                ResponseDTO<?> combinedResponse = ResponseDTO.createSuccessResponse(
                        chatResponse.getResultMessage(),
                        chatDataMap);
                return ResponseEntity.ok(combinedResponse);
            }

            return ResponseEntity.ok(chatResponse);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 읽음 처리 (단체 채팅방)
     */
    @PostMapping("/{room}/read/{messageid}/{userid}")
    public ResponseEntity<String> MessageRead(@PathVariable("room") String room,
            @PathVariable("messageid") String messageid,
            @PathVariable("userid") String userid) {
        try {
            log.info("MessageRead: room={}, messageid={}, userid={}", room, messageid, userid);
            String result = chatServiceClient.MessageRead(room, messageid, userid);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR: " + e.getMessage());
        }
    }

    /**
     * 방 퇴장시 나간 사람 읽음처리 구분을 위한 나간 시간체크 (단체 채팅방)
     */
    @PutMapping("/{room}/leave/{userid}")
    public ResponseEntity<?> Leave(@PathVariable("room") String room, @PathVariable("userid") String userid) {
        try {
            log.info("Leave: room={}, userid={}", room, userid);
            return ResponseEntity.ok(chatServiceClient.Leave(room, userid));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 특정 채팅방에 특정 사용자 초대하기 (단체 채팅방)
     */
    @PostMapping("/{room}/invitation")
    public ResponseEntity<?> Invitation(@PathVariable("room") String room,
            @RequestBody InvitationRequestDTO invitationRequestDTO) {
        try {
            log.info("Invitation: room={}, data={}", room, invitationRequestDTO);
            return ResponseEntity.ok(chatServiceClient.Invitation(room, invitationRequestDTO));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 알림 설정 변경 (단체 채팅방)
     */
    @PutMapping("/alarm")
    public ResponseEntity<?> CheckAlram(@RequestBody AlarmCheckRequestDTO alarmCheck) {
        try {
            log.info("CheckAlram: {}", alarmCheck);
            return ResponseEntity.ok(chatServiceClient.CheckAlram(alarmCheck));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 1:1 채팅방 존재 여부 확인
     */
    @PostMapping("/checkroom")
    public ResponseEntity<?> CheckRoom(@RequestBody MessageRequestDTO messageRequestDTO) {
        try {
            log.info("CheckRoom: {}", messageRequestDTO);
            ResponseDTO<?> result = chatServiceClient.CheckRoom(messageRequestDTO);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("CheckRoom Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 메세지 삭제
     */
    @DeleteMapping("/{room_index}/{message_index}")
    public ResponseEntity<?> DeleteMessage(@PathVariable("room_index") String room_index,
            @PathVariable("message_index") String message_index) {
        try {
            log.info("DeleteMessage: room_index={}, message_index={}", room_index, message_index);
            return ResponseEntity.ok(chatServiceClient.DeleteMessage(room_index, message_index));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 메시지 삭제 처리 (WebSocket)
     * 클라이언트에서 /app/adminchat.deleteMessage/{roomId}로 전송
     * 해당 방의 모든 구독자에게 삭제 이벤트 브로드캐스트
     */
    @MessageMapping("/adminchat.deleteMessage/{roomId}")
    @SendTo("/queue/{roomId}")
    public Map<String, Object> deleteMessage(@PathVariable String roomId, @Payload Map<String, Object> deleteData) {

        log.info("메시지 삭제 요청 수신: {}", deleteData);

        try {
            // 1. 프론트에서 전송한 데이터 추출
            Object messageIndexObj = deleteData.get("messageIndex");
            String messageIndex;
            if (messageIndexObj == null) {
                log.error("messageIndex가 null입니다.");
                throw new IllegalArgumentException("messageIndex가 null입니다.");
            } else if (messageIndexObj instanceof Integer) {
                messageIndex = String.valueOf(messageIndexObj);
                log.info("messageIndex를 Integer에서 String으로 변환: {} -> {}", messageIndexObj, messageIndex);
            } else {
                messageIndex = (String) messageIndexObj;
                log.info("messageIndex String 타입: {}", messageIndex);
            }

            Object roomIdObj = deleteData.get("roomId");
            String roomIdFromData;
            if (roomIdObj == null) {
                log.error("roomId가 null입니다.");
                throw new IllegalArgumentException("roomId가 null입니다.");
            } else if (roomIdObj instanceof Integer) {
                roomIdFromData = String.valueOf(roomIdObj);
                log.info("roomId를 Integer에서 String으로 변환: {} -> {}", roomIdObj, roomIdFromData);
            } else {
                roomIdFromData = (String) roomIdObj;
                log.info("roomId String 타입: {}", roomIdFromData);
            }

            log.info("메시지 삭제 처리: roomId={}, messageIndex={}", roomIdFromData, messageIndex);

            // 2. 백엔드에서 메시지 삭제 처리
            ResponseDTO<?> deleteResult = chatServiceClient.DeleteMessage(roomIdFromData, messageIndex);

            if (deleteResult.getResultCode() == 200) {
                // 3. 삭제 성공 시 모든 구독자에게 삭제 이벤트 브로드캐스트
                Map<String, Object> deleteEvent = Map.of(
                        "type", "DELETE_MESSAGE",
                        "messageIndex", messageIndex,
                        "roomId", roomIdFromData,
                        "timestamp", System.currentTimeMillis(),
                        "success", true);

                log.info("메시지 삭제 성공 및 브로드캐스트: messageIndex={}", messageIndex);
                return deleteEvent;
            } else {
                // 4. 삭제 실패 시 에러 응답
                Map<String, Object> errorEvent = Map.of(
                        "type", "DELETE_MESSAGE_ERROR",
                        "messageIndex", messageIndex,
                        "error", deleteResult.getResultMessage(),
                        "timestamp", System.currentTimeMillis(),
                        "success", false);

                log.error("메시지 삭제 실패: messageIndex={}, error={}", messageIndex, deleteResult.getResultMessage());
                return errorEvent;
            }

        } catch (Exception e) {
            log.error("메시지 삭제 처리 중 오류 발생: {}", e.getMessage());
            Map<String, Object> errorEvent = Map.of(
                    "type", "DELETE_MESSAGE_ERROR",
                    "error", "메시지 삭제 처리 중 오류가 발생했습니다.",
                    "timestamp", System.currentTimeMillis(),
                    "success", false);
            return errorEvent;
        }
    }

    // HTTP 파일 업로드 엔드포인트
    @PostMapping("/upload-files")
    public ResponseEntity<?> uploadFiles(
            @RequestParam("room_index") String roomIndex,
            @RequestParam("user_id") String userId,
            @RequestParam("message") String message,
            @RequestParam("files") MultipartFile[] files) {

        try {
            List<Map<String, Object>> uploadedFiles = new ArrayList<>();

            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    // S3 업로드
                    String folder = "chat-files/" + roomIndex;
                    String fileKey = s3FileUploadService.uploadFile(file, folder);

                    // 업로드된 파일 정보 생성
                    Map<String, Object> uploadedFile = new HashMap<>();
                    uploadedFile.put("name", file.getOriginalFilename());
                    uploadedFile.put("type", file.getContentType());
                    uploadedFile.put("size", file.getSize());
                    uploadedFile.put("url", fileKey);
                    uploadedFiles.add(uploadedFile);

                    log.info("파일 업로드 성공: {} -> {}", file.getOriginalFilename(), fileKey);
                }
            }

            // 메시지 저장 및 WebSocket 브로드캐스트
            MessageRequestDTO messageRequest = new MessageRequestDTO();
            messageRequest.setRoom_index(roomIndex);
            messageRequest.setUser_id(userId);
            messageRequest.setMessage(message);
            // 파일 정보를 엔티티 구조에 맞게 전송
            List<String> fileUrls = uploadedFiles.stream()
                    .map(file -> (String) file.get("url"))
                    .collect(Collectors.toList());
            messageRequest.setUploadFiles(fileUrls);

            // 채팅 서비스로 메시지 저장 및 브로드캐스트
            ResponseDTO<?> response = chatServiceClient.SendMessage(messageRequest);

            // WebSocket으로 메시지 브로드캐스트
            if (response != null && response.getData() != null) {
                Map<String, Object> messageData = new HashMap<>();
                messageData.put("type", "chat");
                messageData.put("message", message);
                messageData.put("user_id", userId);
                messageData.put("sender", userId);
                messageData.put("room_index", roomIndex);
                messageData.put("timestamp", System.currentTimeMillis());
                messageData.put("files", uploadedFiles);

                // 발신자 이름 정보 추가
                try {
                    ResponseDTO<?> adminListResponse = chatService.Adminlist();
                    if (adminListResponse != null && adminListResponse.getData() != null) {
                        @SuppressWarnings("unchecked")
                        List<AdminListDTO> adminList = (List<AdminListDTO>) adminListResponse.getData();

                        boolean found = false;
                        for (AdminListDTO admin : adminList) {
                            String adminUserId = admin.getUserId();
                            String adminName = admin.getName();

                            if (userId.equals(adminUserId)) {
                                messageData.put("sender_name", adminName);
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            messageData.put("sender_name", "Unknown");
                        }
                    } else {
                        messageData.put("sender_name", "Unknown");
                    }
                } catch (Exception e) {
                    log.error("발신자 이름 조회 실패: {}", e.getMessage());
                    messageData.put("sender_name", "Unknown");
                }

                // 해당 방의 모든 구독자에게 브로드캐스트
                log.info("📡 WebSocket 브로드캐스트: /queue/{} -> {}", roomIndex, messageData);
                log.info("📁 파일 정보 확인: files={}", messageData.get("files"));
                messagingTemplate.convertAndSend("/queue/" + roomIndex, messageData);
            }

            // 응답 데이터
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("messageIndex", System.currentTimeMillis());
            responseData.put("files", uploadedFiles);

            return ResponseEntity.ok(responseData);

        } catch (Exception e) {
            log.error("파일 업로드 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "파일 업로드 중 오류가 발생했습니다."));
        }
    }

    /**
     * S3 URL에서 파일명 추출
     */
    private String extractFileNameFromUrl(String fileUrl) {
        try {
            String[] parts = fileUrl.split("/");
            String fileNameWithExtension = parts[parts.length - 1];
            // UUID와 타임스탬프 제거하고 원본 파일명만 추출
            if (fileNameWithExtension.contains("_")) {
                String[] nameParts = fileNameWithExtension.split("_");
                if (nameParts.length >= 3) {
                    // 원본 파일명이 있는 경우 (예: chat-files/1/20241201_123456_uuid.jpg)
                    return nameParts[nameParts.length - 1];
                }
            }
            return fileNameWithExtension;
        } catch (Exception e) {
            log.warn("파일명 추출 실패: {}", fileUrl);
            return "unknown_file";
        }
    }

    /**
     * S3 URL에서 파일 타입 추출
     */
    private String extractFileTypeFromUrl(String fileUrl) {
        try {
            if (fileUrl.contains(".")) {
                return fileUrl.substring(fileUrl.lastIndexOf(".") + 1).toLowerCase();
            }
            return "unknown";
        } catch (Exception e) {
            log.warn("파일 타입 추출 실패: {}", fileUrl);
            return "unknown";
        }
    }

}