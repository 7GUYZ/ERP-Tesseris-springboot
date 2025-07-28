package com.jakdang.labs.api.alarm.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.jakdang.labs.api.alarm.model.AlarmServiceClient;
import com.jakdang.labs.api.alarm.service.AlarmSvc;
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
    private final AlarmSvc alarmService;

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

    /**
     * 사용자의 특정 알림 타입 설정 조회
     * @param userIndex 사용자 인덱스
     * @param alarmTypesId 알림 타입 ID
     * @return isActive 상태 (1: 활성화, 0: 비활성화, null: 설정 없음)
     */
    @GetMapping("/user-alarm-setting")
    public ResponseEntity<Map<String, Object>> getUserAlarmSetting(
            @RequestParam(name = "userIndex") Integer userIndex,
            @RequestParam(name = "alarmTypesId") Integer alarmTypesId) {
        
        Map<String, Object> result = alarmService.getUserAlarmSetting(userIndex, alarmTypesId);
        
        // 에러가 있는 경우 500 에러 반환
        if (result.containsKey("error")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
        
        return ResponseEntity.ok(result);
    }

    /**
     * 사용자의 알림 설정 업데이트
     */
    @PostMapping("/update-user-alarm-setting")
    public ResponseEntity<Map<String, Object>> updateUserAlarmSetting(
            @RequestParam(name = "userIndex") Integer userIndex,
            @RequestParam(name = "alarmTypesId") Integer alarmTypesId,
            @RequestParam(name = "isActive") Integer isActive) {
        
        log.info("🔧 사용자 알림 설정 업데이트 요청 - userIndex: {}, alarmTypesId: {}, isActive: {}", 
            userIndex, alarmTypesId, isActive);
        
        try {
            Map<String, Object> result = alarmServiceClient.updateUserAlarmSetting(userIndex, alarmTypesId, isActive);
            
            if (result.containsKey("success") && (Boolean) result.get("success")) {
                log.info("✅ 알림 설정 업데이트 성공 - userIndex: {}, alarmTypesId: {}, isActive: {}", 
                    userIndex, alarmTypesId, isActive);
                return ResponseEntity.ok(result);
            } else {
                log.error("❌ 알림 설정 업데이트 실패 - userIndex: {}, alarmTypesId: {}, isActive: {}", 
                    userIndex, alarmTypesId, isActive);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
            }
            
        } catch (Exception e) {
            log.error("❌ 알림 설정 업데이트 중 예외 발생 - userIndex: {}, alarmTypesId: {}, isActive: {}", 
                userIndex, alarmTypesId, isActive, e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "알림 설정 업데이트에 실패했습니다.");
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
