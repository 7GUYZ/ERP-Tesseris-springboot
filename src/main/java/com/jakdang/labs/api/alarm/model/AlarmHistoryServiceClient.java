package com.jakdang.labs.api.alarm.model;

import com.jakdang.labs.api.alarm.dto.AlarmHistoryResponseDTO;
import com.jakdang.labs.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "alarm-history-service", url = "${alarm-service.url}", configuration = FeignConfig.FeignErrorDecoder.class)
public interface AlarmHistoryServiceClient {
    
    /**
     * 사용자별 알림 내역 조회
     */
    @GetMapping("/alarm-history/user/{userIndex}")
    List<AlarmHistoryResponseDTO> getUserAlarmHistory(@PathVariable("userIndex") Integer userIndex);
} 