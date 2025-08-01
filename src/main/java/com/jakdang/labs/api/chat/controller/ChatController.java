// package com.jakdang.labs.api.chat.controller;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.stream.Collectors;

// import org.springframework.context.event.EventListener;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.messaging.handler.annotation.MessageMapping;
// import org.springframework.messaging.handler.annotation.Payload;
// import org.springframework.messaging.simp.SimpMessagingTemplate;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RequestPart;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.multipart.MultipartFile;

<<<<<<< HEAD
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.jakdang.labs.api.chat.dto.ChatWebSocketMessageDto;
// import com.jakdang.labs.api.chat.dto.MessageRequestDTO;
// import com.jakdang.labs.api.chat.dto.RoomRequestDTO;
// import com.jakdang.labs.api.chat.dto.SearchResponseDTO;
// import com.jakdang.labs.api.chat.dto.UserListDTO;
// import com.jakdang.labs.api.deokkyu.admin.dto.AdminListResponseDto;
// import com.jakdang.labs.api.chat.model.ChatServiceClient;
// import com.jakdang.labs.api.chat.service.ChatService;
// import com.jakdang.labs.api.common.ResponseDTO;
=======
import com.amazonaws.services.s3.internal.eventstreaming.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakdang.labs.api.chat.dto.AlarmCheckRequestDTO;
import com.jakdang.labs.api.chat.dto.InvitationRequestDTO;
import com.jakdang.labs.api.chat.dto.MessageRequestDTO;
import com.jakdang.labs.api.chat.dto.RoomRequestDTO;
import com.jakdang.labs.api.chat.dto.UserListDTO;
import com.jakdang.labs.api.chat.model.ChatServiceClient;
import com.jakdang.labs.api.chat.service.ChatService;
import com.jakdang.labs.api.common.ResponseDTO;
>>>>>>> jihun

// import feign.FeignException;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @RestController
// @RequestMapping("/api/adminchat")
// @RequiredArgsConstructor
// @Slf4j
// @Tag(name = "채팅 API", description = "채팅 관련 API")
// public class ChatController {
//     // 메인 db
//     private final ChatService chatService;
//     // 채팅 db
//     private final ChatServiceClient chatServiceClient;
//     // WebSocket 메시지 전송용
//     private final SimpMessagingTemplate messagingTemplate;

<<<<<<< HEAD
//     @GetMapping("/adminlist")
//     public ResponseEntity<List<UserListDTO>> Adminlist() {
//         log.info("=== 간단한 관리자 리스트 조회 API 호출 ===");
        
//         try {
//             List<UserListDTO> userList = chatService.Adminlist();
//             log.info("✅ 관리자 리스트 조회 완료 - 결과 개수: {}", userList.size());
//             return ResponseEntity.ok(userList);
//         } catch (Exception e) {
//             log.error("❌ 관리자 리스트 조회 API 오류");
//             log.error("❌ 오류 타입: {}", e.getClass().getSimpleName());
//             log.error("❌ 오류 메시지: {}", e.getMessage());
//             log.error("❌ 오류 상세: ", e);
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//         }
//     }
=======
    @GetMapping("/adminlist")
    public ResponseEntity<ResponseDTO<?>> Adminlist() {
        try {
            return ResponseEntity.ok(ResponseDTO.createSuccessResponse("조회 성공", chatService.Adminlist()));
        } catch (Exception e) {
            log.error("Error fetching user list: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
>>>>>>> jihun

//     /**
//      * 테스트용 간단한 엔드포인트
//      */
//     @GetMapping("/test")
//     public ResponseEntity<String> test() {
//         log.info("Chat API 테스트 호출됨");
//         return ResponseEntity.ok("Chat API 정상 동작");
//     }


<<<<<<< HEAD
=======
    /**
     * send message
     */
    @PostMapping("/sendmessage")
    public ResponseEntity<ResponseDTO<?>> SendMessage(@RequestBody MessageRequestDTO messageRequestDTO) {
        try {
            log.info("SendMessage: {}", messageRequestDTO);
            return ResponseEntity.ok(chatServiceClient.SendMessage(messageRequestDTO));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
>>>>>>> jihun



//     /**
//      * 채팅용 관리자 리스트 조회 API
//      * GET /api/adminchat/list
//      */
//     @GetMapping("/list")
//     public ResponseEntity<List<AdminListResponseDto>> getChatAllAdminList() {
//         log.info("=== 채팅용 관리자 리스트 조회 API 호출 ===");
        
//         try {
//             log.info("🔍 ChatService.getChatAllAdminList() 호출 시작");
            
//             // ChatService를 통해 채팅용 관리자 리스트 조회
//             List<AdminListResponseDto> adminList = chatService.getChatAllAdminList();
            
//             log.info("✅ 채팅용 관리자 리스트 조회 완료 - 결과 개수: {}", adminList.size());
            
//             return ResponseEntity.ok(adminList);
            
//         } catch (Exception e) {
//             log.error("❌ 채팅용 관리자 리스트 조회 API 오류");
//             log.error("❌ 오류 타입: {}", e.getClass().getSimpleName());
//             log.error("❌ 오류 메시지: {}", e.getMessage());
//             log.error("❌ 오류 상세: ", e);
//             return ResponseEntity.internalServerError().build();
//         }
//     }

//     /**
//      * search room
//      * GET /api/chat/{userid}
//      */
//     @GetMapping("/{userid}")
//     public ResponseEntity<ResponseDTO<List<RoomRequestDTO>>> SearchRoom(@PathVariable("userid") String userid) {
//         log.info("=== 📞 SearchRoom API 호출 ===");
//         log.info("🔍 요청 userid: {}", userid);
        
//         try {
//             log.info("🌐 외부 채팅 서비스 호출 시작 - ChatServiceClient.SearchRoom()");
            
//             // 외부 서비스에서 SearchResponseDTO 리스트 받기
//             ResponseDTO<List<SearchResponseDTO>> externalResponse = chatServiceClient.SearchRoom(userid);
//             log.info("✅ 외부 채팅 서비스 응답 성공: {}", externalResponse);
            
//             // SearchResponseDTO → RoomRequestDTO 변환
//             List<RoomRequestDTO> roomList = convertToRoomRequestDTO(externalResponse.getData());
            
//             // 내부 ResponseDTO로 래핑
//             ResponseDTO<List<RoomRequestDTO>> response = new ResponseDTO<>(
//                 externalResponse.getResultCode(), 
//                 externalResponse.getResultMessage(), 
//                 roomList
//             );
            
//             log.info("✅ DTO 변환 완료 - 결과 개수: {}", roomList.size());
            
//             return ResponseEntity.ok(response);
            
//         } catch (FeignException e) {
//             log.error("❌ Feign 통신 오류");
//             log.error("❌ HTTP 상태: {}", e.status());
//             log.error("❌ 오류 메시지: {}", e.getMessage());
//             log.error("❌ 오류 상세: ", e);
//             return ResponseEntity.ok(new ResponseDTO<>(e.status(), e.getMessage(), null));
//         } catch (Exception e) {
//             log.error("❌ SearchRoom 처리 중 오류 발생");
//             log.error("❌ 오류 타입: {}", e.getClass().getSimpleName());
//             log.error("❌ 오류 메시지: {}", e.getMessage());
//             log.error("❌ 오류 상세: ", e);
//             return ResponseEntity.ok(new ResponseDTO<>(500, e.getMessage(), null));
//         }
//     }

//     /**
//      * send message (JSON) - 프론트엔드에서 JSON으로 전송되는 메시지 처리
//      */
//     @PostMapping("/sendmessage")
//     public ResponseEntity<String> SendMessage(@RequestPart("message") String messageRequestDTO,
//             @RequestPart(value = "files", required = false) List<MultipartFile> files) {
//         try {
//             // 프론트에서 받지 않는 필드들을 적절히 처리
//             MessageRequestDTO processedRequest = processMessageRequestDTO(messageRequestDTO);
//             log.info("✅ MessageRequestDTO 처리 완료: {}", processedRequest);
            
//             // 처리된 DTO를 JSON 문자열로 변환
//             ObjectMapper objectMapper = new ObjectMapper();
//             String processedMessageRequestJson = objectMapper.writeValueAsString(processedRequest);
//             log.info("🔄 최종 JSON 변환 완료: {}", processedMessageRequestJson);
            
//             // 외부 서비스 호출 (파일 없이 전송)
//             log.info("🌐 외부 채팅 서비스 호출 시작");
//             String response = chatServiceClient.SendMessage(processedMessageRequestJson, null);
//             log.info("✅ 외부 채팅 서비스 응답: {}", response);
            
//             return ResponseEntity.ok(response);
            
//         } catch (Exception e) {
//             log.error("❌ JSON 메시지 전송 처리 중 오류 발생");
//             log.error("❌ 오류 타입: {}", e.getClass().getSimpleName());
//             log.error("❌ 오류 메시지: {}", e.getMessage());
//             log.error("❌ 오류 상세: ", e);
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                     .body("메시지 전송에 실패했습니다: " + e.getMessage());
//         }
//     }

//     /**
//      * 특정 채팅방에 특정 사용자 초대하기
//      * 
//      * @param entity
//      * @return
//      */
//     @PostMapping("/{room}/invitation")
//     public ResponseEntity<?> Invitation(@PathVariable("room") String room,
//             @RequestBody InvitationRequestDTO invitationRequestDTO) {
//         try {
//             log.info("Invitation: {}", room);
//             log.info("Invitation: {}", invitationRequestDTO.getUserid());
//             log.info("Invitation: {}", invitationRequestDTO.getInviter());
//             return ResponseEntity.ok(chatServiceClient.Invitation(room, invitationRequestDTO));
//         } catch (FeignException e) {
//             log.error("Feign Error: {}", e.getMessage());
//             return ResponseEntity.ok(new ResponseDTO<List<RoomRequestDTO>>(e.status(), e.getMessage(), null));
//         } catch (Exception e) {
//             log.error("Error: {}", e.getMessage());
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//         }
//     }

//     @PutMapping("/alarm")
//     public ResponseEntity<?> CheckAlram(@RequestBody AlarmCheckRequestDTO alarmCheck) {
//         try {
//             log.info("CheckAlram: {}", alarmCheck.getRoom_index());
//             log.info("CheckAlram: {}", alarmCheck.getUser_id());
//             log.info("CheckAlram: {}", alarmCheck.getAlarm_index());
//             return ResponseEntity.ok(chatServiceClient.CheckAlram(alarmCheck));
//         } catch (Exception e) {
//             log.error("Error: {}", e.getMessage());
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//         }
//     }

//     /**
//      * 채팅방 채팅 내용 조회
//      * 입장한사람 읽음처리
//      * 
//      * @param room
//      * @return
//      */
//     @GetMapping("/{room}/chatlist/{userid}")
//     public ResponseEntity<?> ChatList(@PathVariable("room") String room,
//             @PathVariable("userid") String userid,
//             @RequestParam(name = "page", defaultValue = "0") int page,
//             @RequestParam(name = "size", defaultValue = "25") int size) {
//         try {
//             log.info("ChatList: {}", room);
//             log.info("ChatList: {}", userid);
//             log.info("ChatList: {}", page);
//             log.info("ChatList: {}", size);
//             return ResponseEntity.ok(chatServiceClient.ChatList(room, userid, page, size));
//         } catch (Exception e) {
//             log.error("Error: {}", e.getMessage());
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//         }
//     }

//     // 읽음 처리
//     @PostMapping("/{room}/read/{messageid}/{userid}")
//     public String MessageRead(@PathVariable("room") String room, @PathVariable("messageid") String messageid,
//             @PathVariable("userid") String userid) {
//         try {
//             log.info("MessageRead: {}", room);
//             log.info("MessageRead: {}", messageid);
//             log.info("MessageRead: {}", userid);
//             return chatServiceClient.MessageRead(room, messageid, userid);
//         } catch (Exception e) {
//             log.error("Error: {}", e.getMessage());
//             return "ERROR: " + e.getMessage();
//         }
//     }

//     /**
//      * 방 퇴장시 나간 사람 읽음처리 구분을 위한 나간 시간체크
//      * 
//      */
//     @PutMapping("/{room}/leave/{userid}")
//     public ResponseEntity<?> Leave(@PathVariable("room") String room, @PathVariable("userid") String userid) {
//         try {
//             log.info("Leave: {}", room);
//             log.info("Leave: {}", userid);
//             log.info("Leave: {}", chatServiceClient.Leave(room, userid));
//             return ResponseEntity.ok(chatServiceClient.Leave(room, userid));
//         } catch (Exception e) {
//             log.error("Error: {}", e.getMessage());
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//         }
//     }
// }