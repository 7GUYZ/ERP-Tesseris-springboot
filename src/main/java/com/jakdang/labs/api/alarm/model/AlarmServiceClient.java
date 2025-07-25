package com.jakdang.labs.api.alarm.model;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.jakdang.labs.config.FeignConfig;

@FeignClient(name = "alarm-service", url = "${alarm-service.url}", configuration = FeignConfig.FeignErrorDecoder.class)
public interface AlarmServiceClient {
    
    // 테스트
    @GetMapping("/HelloAlarm")
    String helloAlarm();
}
