package com.jakdang.labs.api.chat.model;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
     * new create room
     */
    @PostMapping("/roomcreate")
    public String RoomCreate(@RequestBody RoomRequestDTO roomRequestDTO);
    /**
     * search room
     */
    @GetMapping("/{userid}")
    public ResponseDTO<List<RoomRequestDTO>> SearchRoom(@PathVariable("userid") String userid);
}
