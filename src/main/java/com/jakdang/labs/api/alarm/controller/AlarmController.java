package com.jakdang.labs.api.alarm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jakdang.labs.api.alarm.AlarmUtil;
import com.jakdang.labs.api.alarm.model.AlarmServiceClient;
import com.jakdang.labs.api.auth.service.AuthService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/alarms")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "알림 API", description = "알림 관련 API")
public class AlarmController {
    private final AlarmServiceClient alarmServiceClient;
    private final AlarmUtil alarmUtil;
    private final AuthService authService;

    @GetMapping("/HelloAlarm")
    public String helloAlarm(){
        log.info("🔍 HelloAlarm 호출 시작");
        log.info("🔍 alarmServiceClient: {}", alarmServiceClient);
        
        try {
            String result = alarmServiceClient.helloAlarm();
            log.info("✅ HelloAlarm 결과: {}", result);
            return result;
        } catch (Exception e) {
            log.error("❌ HelloAlarm 오류: {}", e.getMessage(), e);
            return "ERROR: " + e.getMessage();
        }
    }

}
