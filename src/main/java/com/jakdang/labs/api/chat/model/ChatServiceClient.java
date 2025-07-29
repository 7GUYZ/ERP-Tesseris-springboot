package com.jakdang.labs.api.chat.model;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.jakdang.labs.api.chat.dto.InvitationRequestDTO;
import com.jakdang.labs.api.chat.dto.MessageRequestDTO;
import com.jakdang.labs.api.chat.dto.RoomRequestDTO;
import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.config.FeignConfig;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@FeignClient(name = "chat-service", url = "${chat-service.url}", configuration = FeignConfig.FeignErrorDecoder.class)
@Tag(name = "채팅 FeignClient", description = "채팅 관련 API")
public interface ChatServiceClient {
    @GetMapping("/hello")
    public String hello();
    /**
     * search room
     */
    @GetMapping("/{userid}")
    public ResponseDTO<List<RoomRequestDTO>> SearchRoom(@PathVariable("userid") String userid);
    /**
     * send message
     */
    @PostMapping(value = "/sendmessage",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String SendMessage(@RequestPart("message") String messageRequestDTO, @RequestPart(value = "files", required = false) List<MultipartFile> files);
    /**
     * user invitation
     */
    @PostMapping("/{room}/invitation")
    public String Invitation(@PathVariable("room") String room, @RequestBody InvitationRequestDTO invitationRequestDTO);
}
