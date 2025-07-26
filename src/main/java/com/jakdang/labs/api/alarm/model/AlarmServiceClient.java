package com.jakdang.labs.api.alarm.model;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.jakdang.labs.config.FeignConfig;

@FeignClient(name = "alarm-service", url = "${alarm-service.url}", configuration = FeignConfig.FeignErrorDecoder.class)
public interface AlarmServiceClient {
    
    // 테스트
    @GetMapping("/HelloAlarm")
    String helloAlarm();

    // 알림 구독은 프론트에서 직접 alarm-service에 EventSource로 연결해야함. FeignClient로는 스트리밍 유지가 안됨.
    // SSE는 클라이언트가 직접 구독해야함. 
}
