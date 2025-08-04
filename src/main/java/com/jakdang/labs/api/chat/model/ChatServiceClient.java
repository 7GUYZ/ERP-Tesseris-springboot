package com.jakdang.labs.api.chat.model;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import com.jakdang.labs.api.chat.dto.AlarmCheckRequestDTO;
import com.jakdang.labs.api.chat.dto.InvitationRequestDTO;
import com.jakdang.labs.api.chat.dto.MessageRequestDTO;
import com.jakdang.labs.api.chat.dto.RoomRequestDTO;
import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.config.FeignConfig;
import io.swagger.v3.oas.annotations.tags.Tag;

@FeignClient(name = "chat-service", url = "${chat-service.url}", configuration = FeignConfig.FeignErrorDecoder.class)
@Tag(name = "채팅 FeignClient", description = "채팅 관련 API")
public interface ChatServiceClient {
    /**
     * search room
     */
    @GetMapping("/{userid}")
    public ResponseDTO<?> SearchRoom(@PathVariable("userid") String userid);

    /**
     * send message
     */
    @PostMapping(value = "/sendmessage")
    public ResponseDTO<?> SendMessage(@RequestBody MessageRequestDTO messageRequestDTO);

    /**
     * check room - 사용자 조합으로 기존 방 확인
     */
    @PostMapping(value = "/checkroom")
    public ResponseDTO<?> CheckRoom(@RequestBody MessageRequestDTO messageRequestDTO);

    /**
     * user invitation
     */
    @PostMapping("/{room}/invitation")
    public String Invitation(@PathVariable("room") String room, @RequestBody InvitationRequestDTO invitationRequestDTO);

    /**
     * check alarm
     */
    @PutMapping("/alarm")
    public String CheckAlram(@RequestBody AlarmCheckRequestDTO alarmCheck);

    /**
     * 채팅방 채팅 내용 조회
     * 채팅방 입장
     */
    @GetMapping("/{room}/chatlist/{userid}")
    public ResponseDTO<?> ChatList(@PathVariable("room") String room,
            @PathVariable("userid") String userid,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "25") int size);

    /**
     * 읽음 처리
     */
    @PostMapping("/{room}/read/{messageid}/{userid}")   
    public String MessageRead(@PathVariable("room") String room, @PathVariable("messageid") String messageid,
            @PathVariable("userid") String userid);

    /**
     * 방 퇴장시 나간 사람 읽음처리 구분을 위한 나간 시간체크
     */
    @PutMapping("/{room}/leave/{userid}")
    public ResponseDTO<?> Leave(@PathVariable("room") String room, @PathVariable("userid") String userid);

    /**
     * 메세지 삭제
     */
    @DeleteMapping("/{room_index}/{message_index}")
    public ResponseDTO<?> DeleteMessage(@PathVariable("room_index") String room_index, @PathVariable("message_index") String message_index);
}
