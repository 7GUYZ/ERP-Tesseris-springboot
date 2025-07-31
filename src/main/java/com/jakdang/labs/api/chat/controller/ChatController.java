package com.jakdang.labs.api.chat.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
<<<<<<< HEAD
import com.jakdang.labs.api.chat.dto.ChatWebSocketMessageDto;
=======
import com.jakdang.labs.api.chat.dto.AlarmCheckRequestDTO;
import com.jakdang.labs.api.chat.dto.InvitationRequestDTO;
>>>>>>> dev
import com.jakdang.labs.api.chat.dto.MessageRequestDTO;
import com.jakdang.labs.api.chat.dto.RoomRequestDTO;
import com.jakdang.labs.api.chat.dto.SearchResponseDTO;
import com.jakdang.labs.api.chat.dto.UserListDTO;
import com.jakdang.labs.api.deokkyu.admin.dto.AdminListResponseDto;
import com.jakdang.labs.api.chat.model.ChatServiceClient;
import com.jakdang.labs.api.chat.service.ChatService;
import com.jakdang.labs.api.common.ResponseDTO;

import feign.FeignException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/adminchat")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "채팅 API", description = "채팅 관련 API")
public class ChatController {
    // 메인 db
    private final ChatService chatService;
    // 채팅 db
    private final ChatServiceClient chatServiceClient;
<<<<<<< HEAD
    // WebSocket 메시지 전송용
    private final SimpMessagingTemplate messagingTemplate;
=======
>>>>>>> dev

    @GetMapping("/adminlist")
    public ResponseEntity<List<UserListDTO>> Adminlist() {
        log.info("=== 간단한 관리자 리스트 조회 API 호출 ===");
        
        try {
            List<UserListDTO> userList = chatService.Adminlist();
            log.info("✅ 관리자 리스트 조회 완료 - 결과 개수: {}", userList.size());
            return ResponseEntity.ok(userList);
        } catch (Exception e) {
            log.error("❌ 관리자 리스트 조회 API 오류");
            log.error("❌ 오류 타입: {}", e.getClass().getSimpleName());
            log.error("❌ 오류 메시지: {}", e.getMessage());
            log.error("❌ 오류 상세: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 테스트용 간단한 엔드포인트
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        log.info("Chat API 테스트 호출됨");
        return ResponseEntity.ok("Chat API 정상 동작");
    }





    /**
     * 채팅용 관리자 리스트 조회 API
     * GET /api/adminchat/list
     */
    @GetMapping("/list")
    public ResponseEntity<List<AdminListResponseDto>> getChatAllAdminList() {
        log.info("=== 채팅용 관리자 리스트 조회 API 호출 ===");
        
        try {
            log.info("🔍 ChatService.getChatAllAdminList() 호출 시작");
            
            // ChatService를 통해 채팅용 관리자 리스트 조회
            List<AdminListResponseDto> adminList = chatService.getChatAllAdminList();
            
            log.info("✅ 채팅용 관리자 리스트 조회 완료 - 결과 개수: {}", adminList.size());
            
            return ResponseEntity.ok(adminList);
            
        } catch (Exception e) {
            log.error("❌ 채팅용 관리자 리스트 조회 API 오류");
            log.error("❌ 오류 타입: {}", e.getClass().getSimpleName());
            log.error("❌ 오류 메시지: {}", e.getMessage());
            log.error("❌ 오류 상세: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * search room
     * GET /api/chat/{userid}
     */
    @GetMapping("/{userid}")
    public ResponseEntity<ResponseDTO<List<RoomRequestDTO>>> SearchRoom(@PathVariable("userid") String userid) {
        log.info("=== 📞 SearchRoom API 호출 ===");
        log.info("🔍 요청 userid: {}", userid);
        
        try {
            log.info("🌐 외부 채팅 서비스 호출 시작 - ChatServiceClient.SearchRoom()");
            
            // 외부 서비스에서 SearchResponseDTO 리스트 받기
            ResponseDTO<List<SearchResponseDTO>> externalResponse = chatServiceClient.SearchRoom(userid);
            log.info("✅ 외부 채팅 서비스 응답 성공: {}", externalResponse);
            
            // SearchResponseDTO → RoomRequestDTO 변환
            List<RoomRequestDTO> roomList = convertToRoomRequestDTO(externalResponse.getData());
            
            // 내부 ResponseDTO로 래핑
            ResponseDTO<List<RoomRequestDTO>> response = new ResponseDTO<>(
                externalResponse.getResultCode(), 
                externalResponse.getResultMessage(), 
                roomList
            );
            
            log.info("✅ DTO 변환 완료 - 결과 개수: {}", roomList.size());
            
            return ResponseEntity.ok(response);
            
        } catch (FeignException e) {
            log.error("❌ Feign 통신 오류");
            log.error("❌ HTTP 상태: {}", e.status());
            log.error("❌ 오류 메시지: {}", e.getMessage());
            log.error("❌ 오류 상세: ", e);
            return ResponseEntity.ok(new ResponseDTO<>(e.status(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("❌ SearchRoom 처리 중 오류 발생");
            log.error("❌ 오류 타입: {}", e.getClass().getSimpleName());
            log.error("❌ 오류 메시지: {}", e.getMessage());
            log.error("❌ 오류 상세: ", e);
            return ResponseEntity.ok(new ResponseDTO<>(500, e.getMessage(), null));
        }
    }

    /**
     * send message (JSON) - 프론트엔드에서 JSON으로 전송되는 메시지 처리
     */
<<<<<<< HEAD
    @PostMapping("/message")
    public ResponseEntity<String> SendMessageJson(@RequestBody MessageRequestDTO messageRequestDTO) {
        log.info("=== 📤 JSON 메시지 전송 API 호출 ===");
        log.info("🔍 수신된 MessageRequestDTO: {}", messageRequestDTO);
        
=======
    @PostMapping("/sendmessage")
    public ResponseEntity<String> SendMessage(@RequestPart("message") String messageRequestDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
>>>>>>> dev
        try {
            // 프론트에서 받지 않는 필드들을 적절히 처리
            MessageRequestDTO processedRequest = processMessageRequestDTO(messageRequestDTO);
            log.info("✅ MessageRequestDTO 처리 완료: {}", processedRequest);
            
            // 처리된 DTO를 JSON 문자열로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            String processedMessageRequestJson = objectMapper.writeValueAsString(processedRequest);
            log.info("🔄 최종 JSON 변환 완료: {}", processedMessageRequestJson);
            
            // 외부 서비스 호출 (파일 없이 전송)
            log.info("🌐 외부 채팅 서비스 호출 시작");
            String response = chatServiceClient.SendMessage(processedMessageRequestJson, null);
            log.info("✅ 외부 채팅 서비스 응답: {}", response);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ JSON 메시지 전송 처리 중 오류 발생");
            log.error("❌ 오류 타입: {}", e.getClass().getSimpleName());
            log.error("❌ 오류 메시지: {}", e.getMessage());
            log.error("❌ 오류 상세: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("메시지 전송에 실패했습니다: " + e.getMessage());
        }
    }

    /**
<<<<<<< HEAD
     * send message (Multipart) - 파일과 함께 전송되는 메시지 처리
     */
    @PostMapping("/message/multipart")
    public ResponseEntity<String> SendMessageMultipart(@RequestPart("message") String messageRequestDTO, @RequestPart(value = "files", required = false) MultipartFile[] files) {
        log.info("=== 📤 메시지 전송 API 호출 ===");
        log.info("🔍 수신된 MessageRequestDTO JSON: {}", messageRequestDTO);
        log.info("📁 수신된 파일 개수: {}", files != null ? files.length : 0);
        
        try {
            // JSON 문자열을 MessageRequestDTO 객체로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            MessageRequestDTO messageRequest = objectMapper.readValue(messageRequestDTO, MessageRequestDTO.class);
            log.info("✅ JSON → DTO 변환 완료: {}", messageRequest);
            
            // 파일 정보 로깅
            if (files != null && files.length > 0) {
                log.info("📁 파일 상세 정보:");
                for (int i = 0; i < files.length; i++) {
                    MultipartFile file = files[i];
                    log.info("   [{}] 파일명: {}, 크기: {} bytes, Content-Type: {}", 
                            i, file.getOriginalFilename(), file.getSize(), file.getContentType());
                }
            } else {
                log.info("📁 첨부 파일 없음");
            }
            
            // 프론트에서 받지 않는 필드들을 적절히 처리
            MessageRequestDTO processedRequest = processMessageRequestDTO(messageRequest);
            log.info("✅ MessageRequestDTO 처리 완료: {}", processedRequest);
            
            // 처리된 DTO를 다시 JSON 문자열로 변환
            String processedMessageRequestJson = objectMapper.writeValueAsString(processedRequest);
            log.info("🔄 최종 JSON 변환 완료: {}", processedMessageRequestJson);
            
            // 외부 서비스 호출 (파일과 함께 전송)
            log.info("🌐 외부 채팅 서비스 호출 시작");
            String response = chatServiceClient.SendMessage(processedMessageRequestJson, files);
            log.info("✅ 외부 채팅 서비스 응답: {}", response);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ 메시지 전송 처리 중 오류 발생");
            log.error("❌ 오류 타입: {}", e.getClass().getSimpleName());
            log.error("❌ 오류 메시지: {}", e.getMessage());
            log.error("❌ 오류 상세: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("메시지 전송에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 프론트엔드에서 받은 MessageRequestDTO 처리 메서드
     * 프론트에서 보내지 않는 필드들을 null로 설정하고 필요한 기본값 처리
     */
    private MessageRequestDTO processMessageRequestDTO(MessageRequestDTO frontendRequest) {
        log.info("🔄 MessageRequestDTO 처리 시작");
        log.info("   - 수신된 데이터: user_id={}, room_index={}, room_name={}, message={}", 
                frontendRequest.getUser_id(), 
                frontendRequest.getRoom_index(), 
                frontendRequest.getRoom_name(),
                frontendRequest.getMessage());
        
        // 새로운 DTO 생성 (기존 데이터 복사 + 추가 처리)
        MessageRequestDTO processedRequest = new MessageRequestDTO();
        
        // === 프론트엔드에서 받는 필드들 ===
        processedRequest.setUser_id(frontendRequest.getUser_id());
        processedRequest.setSent_at(frontendRequest.getSent_at());
        processedRequest.setMessage(frontendRequest.getMessage());
        processedRequest.setRoom_index(frontendRequest.getRoom_index());
        processedRequest.setRoom_name(frontendRequest.getRoom_name());
        processedRequest.setParticipants(frontendRequest.getParticipants());
        
        // === 프론트엔드에서 받지 않는 필드들 (null 또는 기본값 설정) ===
        processedRequest.setMessage_index(null);              // 채팅 서버에서 생성
        processedRequest.setActive("Y");                       // 기본값: 활성 상태

        
        log.info("✅ MessageRequestDTO 처리 완료");
        log.info("   - message_index: {} (null로 설정)", processedRequest.getMessage_index());
        log.info("   - active: {} (기본값)", processedRequest.getActive());
        log.info("   - participants: {}", processedRequest.getParticipants());
        
        return processedRequest;
    }

    // DTO 변환 관련 메서드들

    /**
     * SearchResponseDTO → RoomRequestDTO 변환 메서드
     */
    private List<RoomRequestDTO> convertToRoomRequestDTO(List<SearchResponseDTO> searchResponseList) {
        if (searchResponseList == null) {
            log.warn("⚠️ SearchResponseDTO 리스트가 null입니다. 빈 리스트 반환");
            return new ArrayList<>();
        }
        
        log.info("🔄 DTO 변환 시작 - SearchResponseDTO 개수: {}", searchResponseList.size());
        
        List<RoomRequestDTO> roomRequestList = searchResponseList.stream()
            .map(this::mapToRoomRequestDTO)
            .collect(Collectors.toList());
            
        log.info("🔄 DTO 변환 완료 - RoomRequestDTO 개수: {}", roomRequestList.size());
        
        return roomRequestList;
    }

    /**
     * 개별 DTO 변환 로직
     */
    private RoomRequestDTO mapToRoomRequestDTO(SearchResponseDTO searchResponse) {
        RoomRequestDTO roomRequest = new RoomRequestDTO();
        
        // 직접 매핑 가능한 필드들
        roomRequest.setRoom_index(searchResponse.getRoom_index());
        roomRequest.setRoom_name(searchResponse.getRoom_name());
        
        // joined_at → created_at 매핑
        roomRequest.setCreated_at(searchResponse.getJoined_at());
        
        // created_by는 기본값 설정 (외부 데이터에 없음)
        roomRequest.setCreated_by("system");
        
        log.debug("🔄 개별 DTO 변환: room_index={}, room_name={}, joined_at={} → created_at={}", 
                searchResponse.getRoom_index(), 
                searchResponse.getRoom_name(),
                searchResponse.getJoined_at(),
                roomRequest.getCreated_at());
        
        return roomRequest;
    }

    // ============= STOMP WebSocket 메시지 핸들러 =============

    /**
     * STOMP WebSocket을 통한 채팅 메시지 처리
     * 프론트엔드 ChatWebSocketContext → 메인 백엔드 → 채팅백엔드 → 브로드캐스트
     */
    @MessageMapping("/chat/room")
    public void handleChatMessage(@Payload ChatWebSocketMessageDto webSocketMessage) {
        log.info("=== 📨 STOMP 채팅 메시지 수신 ===");
        log.info("🔍 수신된 WebSocket 메시지: {}", webSocketMessage);
        
        try {
            // 1. WebSocket 메시지를 채팅백엔드용 DTO로 변환
            MessageRequestDTO chatBackendRequest = convertToMessageRequestDTO(webSocketMessage);
            log.info("✅ DTO 변환 완료: {}", chatBackendRequest);
            
            // 2. 채팅백엔드로 HTTP 요청 전송 (메시지 저장)
            log.info("🌐 채팅백엔드로 메시지 저장 요청 시작");
            ObjectMapper objectMapper = new ObjectMapper();
            String chatBackendRequestJson = objectMapper.writeValueAsString(chatBackendRequest);
            
            String saveResponse = chatServiceClient.SendMessage(chatBackendRequestJson, null);
            log.info("✅ 채팅백엔드 저장 응답: {}", saveResponse);
            
            // 3. 저장 성공 후 구독자들에게 실시간 브로드캐스트
            String topicDestination = "/topic/room/" + webSocketMessage.getRoomId();
            messagingTemplate.convertAndSend(topicDestination, webSocketMessage);
            log.info("📡 브로드캐스트 완료 - 토픽: {}, 메시지: {}", topicDestination, webSocketMessage.getText());
            
        } catch (Exception e) {
            log.error("❌ STOMP 채팅 메시지 처리 중 오류 발생");
            log.error("❌ 오류 타입: {}", e.getClass().getSimpleName());
            log.error("❌ 오류 메시지: {}", e.getMessage());
            log.error("❌ 오류 상세: ", e);
            
            // 에러 발생 시에도 WebSocket으로 에러 메시지 전송 (선택사항)
            // String errorTopic = "/topic/room/" + webSocketMessage.getRoomId() + "/error";
            // messagingTemplate.convertAndSend(errorTopic, "메시지 전송에 실패했습니다: " + e.getMessage());
=======
     * 특정 채팅방에 특정 사용자 초대하기
     * 
     * @param entity
     * @return
     */
    @PostMapping("/{room}/invitation")
    public ResponseEntity<?> Invitation(@PathVariable("room") String room,
            @RequestBody InvitationRequestDTO invitationRequestDTO) {
        try {
            log.info("Invitation: {}", room);
            log.info("Invitation: {}", invitationRequestDTO.getUserid());
            log.info("Invitation: {}", invitationRequestDTO.getInviter());
            return ResponseEntity.ok(chatServiceClient.Invitation(room, invitationRequestDTO));
        } catch (FeignException e) {
            log.error("Feign Error: {}", e.getMessage());
            return ResponseEntity.ok(new ResponseDTO<List<RoomRequestDTO>>(e.status(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/alarm")
    public ResponseEntity<?> CheckAlram(@RequestBody AlarmCheckRequestDTO alarmCheck) {
        try {
            log.info("CheckAlram: {}", alarmCheck.getRoom_index());
            log.info("CheckAlram: {}", alarmCheck.getUser_id());
            log.info("CheckAlram: {}", alarmCheck.getAlarm_index());
            return ResponseEntity.ok(chatServiceClient.CheckAlram(alarmCheck));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
>>>>>>> dev
        }
    }

    /**
<<<<<<< HEAD
     * WebSocket 메시지를 채팅백엔드용 MessageRequestDTO로 변환
     */
    private MessageRequestDTO convertToMessageRequestDTO(ChatWebSocketMessageDto webSocketMessage) {
        log.info("🔄 WebSocket → ChatBackend DTO 변환 시작");
        
        MessageRequestDTO request = new MessageRequestDTO();
        
        // WebSocket 메시지에서 추출 가능한 필드들
        request.setUser_id(webSocketMessage.getSender().getId());
        request.setMessage(webSocketMessage.getText());
        request.setRoom_index(webSocketMessage.getRoomId());
        request.setSent_at(webSocketMessage.getTimestamp());
        
        // 기본값 설정
        request.setMessage_index(null);  // 채팅백엔드에서 생성
        request.setActive("Y");         // 기본값: 활성 상태
        request.setRoom_name(null);     // 기존 방이므로 불필요
        request.setParticipants(null);  // 기존 방이므로 불필요
        
        log.info("✅ DTO 변환 완료 - user_id: {}, room_index: {}, message: {}", 
                request.getUser_id(), request.getRoom_index(), request.getMessage());
        
        return request;
=======
     * 채팅방 채팅 내용 조회
     * 입장한사람 읽음처리
     * 
     * @param room
     * @return
     */
    @GetMapping("/{room}/chatlist/{userid}")
    public ResponseEntity<?> ChatList(@PathVariable("room") String room,
            @PathVariable("userid") String userid,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "25") int size) {
        try {
            log.info("ChatList: {}", room);
            log.info("ChatList: {}", userid);
            log.info("ChatList: {}", page);
            log.info("ChatList: {}", size);
            return ResponseEntity.ok(chatServiceClient.ChatList(room, userid, page, size));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 읽음 처리
    @PostMapping("/{room}/read/{messageid}/{userid}")
    public String MessageRead(@PathVariable("room") String room, @PathVariable("messageid") String messageid,
            @PathVariable("userid") String userid) {
        try {
            log.info("MessageRead: {}", room);
            log.info("MessageRead: {}", messageid);
            log.info("MessageRead: {}", userid);
            return chatServiceClient.MessageRead(room, messageid, userid);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return "ERROR: " + e.getMessage();
        }
    }

    /**
     * 방 퇴장시 나간 사람 읽음처리 구분을 위한 나간 시간체크
     * 
     */
    @PutMapping("/{room}/leave/{userid}")
    public ResponseEntity<?> Leave(@PathVariable("room") String room, @PathVariable("userid") String userid) {
        try {
            log.info("Leave: {}", room);
            log.info("Leave: {}", userid);
            log.info("Leave: {}", chatServiceClient.Leave(room, userid));
            return ResponseEntity.ok(chatServiceClient.Leave(room, userid));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
>>>>>>> dev
    }
}