package com.jakdang.labs.api.chat.model;

import org.springframework.cloud.openfeign.FeignClient;

import com.jakdang.labs.config.FeignConfig;

@FeignClient(name = "chat-service", url = "${chat-service.url}", configuration = FeignConfig.FeignErrorDecoder.class)
public interface ChatServiceClient {
    
}
