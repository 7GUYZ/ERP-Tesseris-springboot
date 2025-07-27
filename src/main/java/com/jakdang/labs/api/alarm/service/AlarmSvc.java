package com.jakdang.labs.api.alarm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jakdang.labs.api.alarm.model.AlarmServiceClient;
import com.jakdang.labs.api.alarm.dto.UserAlarmsDTO;
import com.jakdang.labs.api.alarm.dto.AlarmHistoryRequest;
import com.jakdang.labs.api.alarm.controller.WebSocketController;
import com.jakdang.labs.api.jungeun.repository.UserTesserisLjeRepo;
import com.jakdang.labs.api.jungeun.repository.AuthorityLjeRepo;
import com.jakdang.labs.entity.AuthorityType;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmSvc {
    private final WebSocketController webSocketController;
    private final UserTesserisLjeRepo userRepo;
    private final AuthorityLjeRepo authorityRepo;
    private final AlarmServiceClient alarmServiceClient;


    /**
     * 특정 프로그램 권한을 가진 관리자들의 user_index 목록 조회 (범용 메서드)
     * @param programIndex 권한을 확인할 프로그램 인덱스
     * @return 해당 권한을 가진 관리자들의 user_index 목록
     */
    private List<String> findAdminsWithAuthority(Integer programIndex) {
        try {
            // 해당 프로그램에 권한이 있는 관리자 타입들 조회
            List<AuthorityType> authorities = authorityRepo.findByProgramIndex(programIndex);
            
            // 각 관리자 타입별로 해당하는 관리자들의 user_index 조회
            List<String> adminUserIndexes = new ArrayList<>();
            for (AuthorityType authority : authorities) {
                Integer adminTypeIndex = authority.getAdminTypeIndex().getAdminTypeIndex();
                List<String> userIndexes = userRepo.findAdminIndexesByType(adminTypeIndex);
                adminUserIndexes.addAll(userIndexes);
            }
            
            log.info("프로그램 {} 권한을 가진 관리자 조회 완료: {}명", programIndex, adminUserIndexes.size());
            return adminUserIndexes;
            
        } catch (Exception e) {
            log.error("프로그램 {} 권한 조회 중 오류 발생: {}", programIndex, e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 알림 설정이 활성화된 사용자만 필터링
     */
    private List<String> filterActiveAlarmUsers(List<String> userIndexes) {
        List<String> activeUsers = new ArrayList<>();
        
        for (String userIndexStr : userIndexes) {
            try {
                Integer userIndex = Integer.valueOf(userIndexStr);
                
                // alarm-service에서 해당 사용자의 알림 설정 조회
                List<UserAlarmsDTO> userAlarms = alarmServiceClient.getUserAlarmsByUserIndex(userIndex);
                
                // 월 CM 한도 변경 알림 타입 ID (실제 DB의 alarmTypes 테이블에서 확인 필요)
                Integer cmLimitAlarmTypeId = 3; // TODO: 실제 알림 타입 ID로 변경
                
                // 해당 알림 타입이 활성화되어 있는지 확인
                boolean isActive = userAlarms.stream()
                        .anyMatch(alarm -> cmLimitAlarmTypeId.equals(alarm.getAlarmTypesId()) && 
                                         alarm.getIsActive() == 1);
                
                // 알림 설정이 없거나 활성화된 경우
                if (userAlarms.isEmpty() || isActive) {
                    activeUsers.add(userIndexStr);
                    log.debug("알림 활성화된 사용자: {}", userIndex);
                } else {
                    log.debug("알림 비활성화된 사용자: {}", userIndex);
                }
                
            } catch (Exception e) {
                log.warn("사용자 {} 알림 설정 조회 중 오류: {}", userIndexStr, e.getMessage());
                // 오류 발생 시 기본적으로 알림 전송 (안전장치)
                activeUsers.add(userIndexStr);
            }
        }
        
        return activeUsers;
    }

    /**
     * 알림 내역을 alarm-service에 저장
     */
    private void saveAlarmHistory(List<String> userIndexes, List<String> adminIndexes, String message) {
        try {
            // 모든 수신자 목록 생성
            List<String> allReceivers = new ArrayList<>();
            allReceivers.addAll(userIndexes);
            allReceivers.addAll(adminIndexes);
            
            // 수신자 인덱스를 Integer로 변환
            List<Integer> receiverIndexes = allReceivers.stream()
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
            
            // 알림 내역 저장 요청 생성
            AlarmHistoryRequest request = AlarmHistoryRequest.builder()
                    .alarmTypesId(3) // 월 CM 한도 변경 알림 타입 ID
                    .alarmMessage(message)
                    .senderIndex(0) // 시스템 발신
                    .receiverIndexes(receiverIndexes)
                    .alarmType("MONTHLY_CM_LIMIT_UPDATED")
                    .title("월 CM 한도 변경 알림")
                    .build();
            
            // alarm-service에 알림 내역 저장 요청
            String result = alarmServiceClient.saveAlarmHistory(request);
            log.info("알림 내역 저장 완료: {}", result);
            
        } catch (Exception e) {
            log.error("알림 내역 저장 중 오류 발생: {}", e.getMessage());
        }
    }

    /**
     * 월 CM 한도 변경 알림 (알림 설정 필터링 + 내역 저장)
     */
    public void sendMonthlyCmLimitChangedAlarm(Integer cmLimit) {
        String alarmMessage = "월 CM 한도가 " + cmLimit + "CM으로 변경되었습니다.";
        
        // 1. 모든 사용자 목록 조회
        List<String> allUserIndexes = new ArrayList<>();
        allUserIndexes.addAll(userRepo.findUserIndexesByRole(1)); // 일반(정회원)
        allUserIndexes.addAll(userRepo.findUserIndexesByRole(2)); // 사업자
        allUserIndexes.addAll(userRepo.findUserIndexesByRole(3)); // 가맹점

        // 2. CM 한도 관리 권한을 가진 관리자 목록 조회
        List<String> adminUserIndexes = findAdminsWithAuthority(8); // CM 한도 관리 프로그램

        // 3. 알림 설정이 활성화된 사용자만 필터링
        List<String> activeUserIndexes = filterActiveAlarmUsers(allUserIndexes);
        List<String> activeAdminIndexes = filterActiveAlarmUsers(adminUserIndexes);

        // 4. WebSocket으로 알림 전송
        Map<String, Object> notification = Map.of(
                "type", "MONTHLY_CM_LIMIT_UPDATED",
                "title", "월 CM 한도 변경 알림",
                "message", alarmMessage,
                "timestamp", System.currentTimeMillis(),
                "action", "MONTHLY_CM_LIMIT");

        webSocketController.sendToManyUsers(activeUserIndexes, notification);
        webSocketController.sendToManyUsers(activeAdminIndexes, notification);

        // 5. 알림 내역을 alarm-service에 저장
        saveAlarmHistory(activeUserIndexes, activeAdminIndexes, alarmMessage);

        log.info("월 CM 한도 변경 알림 전송 완료 - 활성 사용자: {}명, 활성 관리자: {}명",
                activeUserIndexes.size(), activeAdminIndexes.size());
    }

} 