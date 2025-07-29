package com.jakdang.labs.api.chat.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jakdang.labs.api.chat.dto.RoomRequestDTO;
import com.jakdang.labs.api.chat.dto.UserListDTO;
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

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

    @GetMapping("/hello")
    public String hello() {
        try {
            String result = chatServiceClient.hello();
            log.info("Hello: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return "ERROR: " + e.getMessage();
        }
    }

    /**
     * new create room
     */
    @PostMapping("/roomcreate")
    public ResponseEntity<String> RoomCreate(@RequestBody RoomRequestDTO roomRequestDTO) {
        try {
            log.info("들어온 RoomCreate: {}", roomRequestDTO);
            roomRequestDTO.setCreated_at(LocalDateTime.parse(roomRequestDTO.getCreated_at(), formatter).format(formatter).replace(" ", " "));
            String result = chatServiceClient.RoomCreate(roomRequestDTO);
            log.info("New room: {}", result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    /**
     * search room
     */
    @GetMapping("/{userid}")
    public ResponseEntity<ResponseDTO<List<RoomRequestDTO>>> SearchRoom(@PathVariable("userid") String userid) {
        try {
            ResponseDTO<List<RoomRequestDTO>> roomList = chatServiceClient.SearchRoom(userid);
            log.info("SearchRoom: {}", roomList);
            return ResponseEntity.ok(roomList);
        } catch (FeignException e) {
            log.error("Feign Error: {}", e.getMessage());
            return ResponseEntity.ok(new ResponseDTO<List<RoomRequestDTO>>(e.status(), e.getMessage(), null));
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.ok(new ResponseDTO<List<RoomRequestDTO>>(404, e.getMessage(), null));
        }
    }
}