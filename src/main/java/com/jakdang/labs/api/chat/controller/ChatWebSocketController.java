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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RestController
@RequestMapping("/api/adminchat")
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatService chatService;
    private final ChatServiceClient chatServiceClient;
    private final ObjectMapper objectMapper;

    // ==================== WebSocket STOMP 메시지 처리 ====================

    /**
     * 채팅 메시지 전송 처리 (단체 채팅방)
     * 클라이언트에서 /app/adminchat.sendMessage/{roomId}로 전송
     * 해당 방의 모든 구독자에게 /queue/{roomId}로 브로드캐스트
     */
    @MessageMapping("/adminchat.sendMessage/{roomId}")
    @SendTo("/queue/{roomId}")
    public Map<String, Object> sendMessage(@PathVariable String roomId, @Payload Map<String, Object> messageData) {

        log.info("채팅 메시지 수신: {}", messageData);

        try {
            // 1. 프론트에서 전송한 데이터 추출
            String userId = (String) messageData.get("user_id");
            String message = (String) messageData.get("message");
            
            // room_index는 Integer일 수 있으므로 안전하게 처리
            Object roomIndexObj = messageData.get("room_index");
            String roomIndex;
            if (roomIndexObj instanceof Integer) {
                roomIndex = String.valueOf(roomIndexObj);
            } else {
                roomIndex = (String) roomIndexObj;
            }
            
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
            
            // 2. MessageRequestDTO 생성
            MessageRequestDTO messageRequestDTO = new MessageRequestDTO();
            messageRequestDTO.setUser_id(userId);
            messageRequestDTO.setMessage(message);
            messageRequestDTO.setRoom_index(roomIndex);
            messageRequestDTO.setRoom_name(roomName);
            messageRequestDTO.setParticipants(participants != null ? participants : new ArrayList<>());
            messageRequestDTO.setTimestamp(null);
            
            // 3. 채팅 서비스를 통해 메시지 저장 및 방 관리
            ResponseDTO<?> response = chatServiceClient.SendMessage(messageRequestDTO);
            
            // 4. 응답에서 room_index와 messageindex 추출하여 메시지 데이터에 추가
            if (response != null && response.getData() != null) {
                if (response.getData() instanceof Map) {
                    Map<String, Object> responseData = (Map<String, Object>) response.getData();
                    messageData.put("room_index", responseData.get("room_index"));
                    messageData.put("messageindex", responseData.get("messageindex"));
                    log.info("메시지 저장 응답: room_index={}, messageindex={}", 
                            responseData.get("room_index"), responseData.get("messageindex"));
                } else {
                    // 기존 호환성을 위해 단순 값도 처리
                    messageData.put("room_index", response.getData());
                }
            }
            
            // 5. 메시지에 타임스탬프 추가
            messageData.put("timestamp", System.currentTimeMillis());

            // 6. 발신자 정보 추가 (프론트에서 전송한 정보 사용)
            messageData.put("sender", userId);
            messageData.put("user_id", userId);
            
            // 7. 발신자 이름 정보 추가 (관리자 목록에서 조회)
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
            return messageData;

        } catch (Exception e) {
            log.error("메시지 처리 중 오류 발생: {}", e.getMessage());
            messageData.put("error", "메시지 전송 실패");
            messageData.put("timestamp", System.currentTimeMillis());
            return messageData;
        }
    }

    /**
     * 채팅방 구독 처리 (단체 채팅방)
     * 클라이언트가 /queue/{roomId}를 구독할 때 호출
     */
    @MessageMapping("/adminchat.joinRoom/{roomId}")
    @SendTo("/queue/{roomId}")
    public Map<String, Object> joinRoom(@Payload Map<String, Object> joinMessage) {

        String roomId = (String) joinMessage.get("roomId");
        String userId = (String) joinMessage.get("user_id");

        log.info("채팅방 입장: roomId={}, user={}", roomId, userId);

        // 입장 메시지 브로드캐스트
        Map<String, Object> systemMessage = Map.of(
                "type", "system",
                "message", userId + "님이 입장했습니다.",
                "timestamp", System.currentTimeMillis(),
                "roomId", roomId,
                "userId", userId);

        return systemMessage;
    }

    /**
     * 채팅방 퇴장 처리 (단체 채팅방)
     */
    @MessageMapping("/adminchat.leaveRoom/{roomId}")
    @SendTo("/queue/{roomId}")
    public Map<String, Object> leaveRoom(@Payload Map<String, Object> leaveMessage) {

        String roomId = (String) leaveMessage.get("roomId");
        String userId = (String) leaveMessage.get("user_id");

        log.info("채팅방 퇴장: roomId={}, user={}", roomId, userId);

        // 퇴장 메시지 브로드캐스트
        Map<String, Object> systemMessage = Map.of(
                "type", "system",
                "message", userId + "님이 퇴장했습니다.",
                "timestamp", System.currentTimeMillis(),
                "roomId", roomId,
                "userId", userId);

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
                    chatDataMap
                );
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
}