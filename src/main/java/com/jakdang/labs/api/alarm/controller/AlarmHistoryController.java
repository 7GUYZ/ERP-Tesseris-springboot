package com.jakdang.labs.api.alarm.controller;

import com.jakdang.labs.api.alarm.dto.AlarmHistoryResponseDTO;
import com.jakdang.labs.api.alarm.model.AlarmHistoryServiceClient;
import com.jakdang.labs.api.common.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alarm-history")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "알림 내역 API", description = "알림 내역 조회 관련 API")
public class AlarmHistoryController {
    
    private final AlarmHistoryServiceClient alarmHistoryServiceClient;

    @GetMapping("/user/{userIndex}")
    @Operation(summary = "사용자별 알림 내역 조회", description = "특정 사용자의 알림 내역을 조회합니다.")
    public ResponseEntity<ResponseDTO<?>> getUserAlarmHistory(
            @Parameter(description = "사용자 인덱스", example = "1") 
            @PathVariable("userIndex") Integer userIndex) {
        
        log.info("🔍 사용자 알림 내역 조회 시작 - userIndex: {}", userIndex);
        
        try {
            List<AlarmHistoryResponseDTO> alarms = alarmHistoryServiceClient.getUserAlarmHistory(userIndex);
            log.info("✅ 알림 내역 조회 성공 - 개수: {}", alarms.size());
            return ResponseEntity.ok().body(ResponseDTO.createSuccessResponse("알림 내역 조회 성공", alarms));
            
        } catch (Exception e) {
            log.error("❌ 알림 내역 조회 실패 - userIndex: {}, error: {}", userIndex, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDTO.createErrorResponse(500, "알림 내역 조회 실패"));
        }
    }
} 