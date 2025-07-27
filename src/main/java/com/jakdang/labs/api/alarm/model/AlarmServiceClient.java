package com.jakdang.labs.api.alarm.model;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.jakdang.labs.api.alarm.dto.AlarmTypesDTO;
import com.jakdang.labs.api.alarm.dto.UserAlarmsDTO;
import com.jakdang.labs.api.alarm.dto.AlarmHistoryRequest;
import com.jakdang.labs.config.FeignConfig;

@FeignClient(name = "alarm-service", url = "${alarm-service.url}", configuration = FeignConfig.FeignErrorDecoder.class)
public interface AlarmServiceClient {
    
    // 테스트
    @GetMapping("/HelloAlarm")
    String helloAlarm();

    // 알림 구독은 프론트에서 직접 alarm-service에 EventSource로 연결해야함. FeignClient로는 스트리밍 유지가 안됨.
    // SSE는 클라이언트가 직접 구독해야함. 

    @GetMapping("/getUserAlarms")
    List<UserAlarmsDTO> getUserAlarms();
    
    /**
     * 특정 사용자의 알림 설정 조회
     */
    @GetMapping("/getUserAlarms/{userIndex}")
    List<UserAlarmsDTO> getUserAlarmsByUserIndex(@PathVariable("userIndex") Integer userIndex);
    
    /**
     * 활성화된 알림 설정만 조회
     */
    @GetMapping("/getActiveAlarms")
    List<UserAlarmsDTO> getActiveAlarms();
    
    /**
     * 알림 내역 저장
     */
    @PostMapping("/saveAlarmHistory")
    String saveAlarmHistory(@RequestBody AlarmHistoryRequest request);
    
    /**
     * 특정 알림 타입 조회
     */
    @GetMapping("/getAlarmType/{alarmTypeId}")
    AlarmTypesDTO getAlarmType(@PathVariable("alarmTypeId") Integer alarmTypeId);
}
