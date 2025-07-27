package com.jakdang.labs.api.alarm.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jakdang.labs.api.alarm.model.AlarmServiceClient;
import com.jakdang.labs.api.alarm.dto.AlarmTypesDTO;
import com.jakdang.labs.api.alarm.dto.UserAlarmsDTO;
import com.jakdang.labs.api.alarm.dto.AlarmHistoryRequest;
import com.jakdang.labs.api.alarm.controller.WebSocketController;
import com.jakdang.labs.api.jungeun.repository.UserTesserisLjeRepo;
import com.jakdang.labs.api.jungeun.repository.AuthorityLjeRepo;
import com.jakdang.labs.entity.AuthorityType;

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
     * 
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
     * 알림 설정에 따른 사용자 필터링
     * - userAlarms에 행이 없는 사용자: 알림 전송
     * - userAlarms에 행이 있는 사용자: is_active=1이면 제외, is_active=0이면 전송
     */
    private List<String> filterActiveAlarmUsers(List<String> userIndexes, Integer alarmTypeId) {
        List<String> activeUsers = new ArrayList<>();

        for (String userIndexStr : userIndexes) {
            try {
                Integer userIndex = Integer.valueOf(userIndexStr);

                // alarm-service에서 해당 사용자의 알림 설정 조회
                List<UserAlarmsDTO> userAlarms = alarmServiceClient.getUserAlarmsByUserIndex(userIndex);

                // 해당 알림 타입의 설정이 있는지 확인
                boolean hasAlarmSetting = userAlarms.stream()
                        .anyMatch(alarm -> alarmTypeId.equals(alarm.getAlarmTypesId()));

                log.info("사용자 {} 알림 타입 {} 설정 조회 결과: {}개 설정, hasAlarmSetting={}",
                        userIndex, alarmTypeId, userAlarms.size(), hasAlarmSetting);

                if (!hasAlarmSetting) {
                    // 설정이 없는 경우: 알림 전송
                    activeUsers.add(userIndexStr);
                    log.info("알림 전송 (설정 없음): {}", userIndex);
                } else {
                    // 설정이 있는 경우: is_active 값 확인
                    boolean isActive = userAlarms.stream()
                            .anyMatch(alarm -> alarmTypeId.equals(alarm.getAlarmTypesId()) &&
                                    alarm.getIsActive() == 1);

                    log.info("사용자 {} 알림 타입 {} is_active 확인: {}", userIndex, alarmTypeId, isActive);

                    if (isActive) {
                        // is_active = 1: 알림 제외
                        log.info("알림 제외 (is_active=1): {}", userIndex);
                    } else {
                        // is_active = 0: 알림 전송
                        activeUsers.add(userIndexStr);
                        log.info("알림 전송 (is_active=0): {}", userIndex);
                    }
                }

            } catch (Exception e) {
                log.warn("사용자 {} 알림 설정 조회 중 오류: {}", userIndexStr, e.getMessage());
                // 오류 발생 시 알림 제외 (보안상 안전)
                log.info("오류로 인해 사용자 {} 알림 제외", userIndexStr);
            }
        }

        return activeUsers;
    }

    /**
     * 알림 내역을 alarm-service에 저장
     */
    private void saveAlarmHistory(List<String> userIndexes, List<String> adminIndexes, String message,
            Integer alarmTypeId, Integer senderIndex) {
        try {
            // 모든 수신자 목록 생성
            List<String> allReceivers = new ArrayList<>();
            allReceivers.addAll(userIndexes);
            allReceivers.addAll(adminIndexes);

            // 수신자 인덱스를 Integer로 변환
            List<Integer> receiverIndexes = allReceivers.stream()
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());

            // senderIndex가 null이면 0으로 설정
            Integer finalSenderIndex = senderIndex != null ? senderIndex : 0;

            // 알림 내역 저장 요청 생성
            AlarmHistoryRequest request = AlarmHistoryRequest.builder()
                    .alarmTypesId(alarmTypeId)
                    .alarmMessage(message)
                    .senderIndex(finalSenderIndex) // senderIndex가 있으면 그 값을, 없으면 0 사용
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
     * 공통 알림 전송 메서드 (수치 포함)
     */
    public void sendAlarmWithValue(Integer alarmTypeId, List<String> userIndexes, List<String> adminIndexes,
            String value, Integer senderIndex) {
        // 1. 알림 메시지 생성
        String alarmMessage = createAlarmMessage(alarmTypeId, value);

        // 2. 발신자를 수신자 목록에서 제외
        List<String> filteredUserIndexes = userIndexes.stream()
                .filter(userIndex -> !userIndex.equals(String.valueOf(senderIndex)))
                .collect(Collectors.toList());
        
        List<String> filteredAdminIndexes = adminIndexes.stream()
                .filter(adminIndex -> !adminIndex.equals(String.valueOf(senderIndex)))
                .collect(Collectors.toList());

        // 3. 알림 설정이 활성화된 사용자만 필터링
        List<String> activeUserIndexes = filterActiveAlarmUsers(filteredUserIndexes, alarmTypeId);
        List<String> activeAdminIndexes = filterActiveAlarmUsers(filteredAdminIndexes, alarmTypeId);

        // 4. WebSocket으로 알림 전송 (즉시)
        Map<String, Object> notification = createNotification(alarmTypeId, alarmMessage);

        webSocketController.sendToManyUsers(activeUserIndexes, notification);
        webSocketController.sendToManyUsers(activeAdminIndexes, notification);

        // 5. 알림 내역을 alarm-service에 비동기로 저장
        CompletableFuture.runAsync(() -> {
            try {
                saveAlarmHistory(activeUserIndexes, activeAdminIndexes, alarmMessage, alarmTypeId, senderIndex);
                log.info("알림 내역 저장 완료 (비동기) - 활성 사용자: {}명, 활성 관리자: {}명, 발신자: {}",
                        activeUserIndexes.size(), activeAdminIndexes.size(), senderIndex);
            } catch (Exception e) {
                log.error("알림 내역 저장 실패 (비동기): {}", e.getMessage());
            }
        });

        log.info("알림 전송 완료 - 활성 사용자: {}명, 활성 관리자: {}명, 발신자: {}",
                activeUserIndexes.size(), activeAdminIndexes.size(), senderIndex);
    }

    /**
     * AlarmTypes 정보를 기반으로 알림 메시지 생성
     */
    private String createAlarmMessage(Integer alarmTypeId, String value) {
        try {
            // alarm-service에서 AlarmTypes 정보 조회
            AlarmTypesDTO alarmType = alarmServiceClient.getAlarmType(alarmTypeId);

            // 동적 메시지 생성
            String description = alarmType.getAlarmTypesDescription();
            if (description == null || description.isEmpty()) {
                description = "신규 알림이 왔습니다.";
            }

            // 수치가 있는 경우 () 안에 추가
            if (value != null && !value.isEmpty()) {
                return String.format("%s (%s)", description, value);
            } else {
                return String.format("%s", description);
            }

        } catch (Exception e) {
            log.warn("AlarmTypes 정보 조회 실패, 기본 메시지 사용: {}", e.getMessage());
            if (value != null && !value.isEmpty()) {
                return String.format("알림이 발생했습니다. (%s)", value);
            } else {
                return "알림이 발생했습니다.";
            }
        }
    }

    /**
     * 알림 타입에 따른 알림 객체 생성
     */
    private Map<String, Object> createNotification(Integer alarmTypeId, String message) {
        try {
            // alarm-service에서 AlarmTypes 정보 조회
            AlarmTypesDTO alarmType = alarmServiceClient.getAlarmType(alarmTypeId);

            String alarmTypeCode = alarmType.getAlarmTypesCode();
            String alarmTypeLabel = alarmType.getAlarmTypesLabel();

            if (alarmTypeCode == null || alarmTypeCode.isEmpty()) {
                alarmTypeCode = "ERROR_NOTIFICATION";
            }
            if (alarmTypeLabel == null || alarmTypeLabel.isEmpty()) {
                alarmTypeLabel = "알림 오류";
            }

            return Map.of(
                    "type", alarmTypeCode,
                    "title", alarmTypeLabel,
                    "message", message,
                    "timestamp", System.currentTimeMillis(),
                    "action", alarmTypeCode, // alarmTypesCode 활용
                    "alarmTypeId", alarmTypeId);

        } catch (Exception e) {
            log.warn("알림 객체 생성 실패, 기본값 사용: {}", e.getMessage());
            return Map.of(
                    "type", "ERROR_NOTIFICATION",
                    "title", "알림 오류",
                    "message", message,
                    "timestamp", System.currentTimeMillis(),
                    "action", "ERROR_NOTIFICATION" // 기본값도 일관성 있게
            );
        }
    }

    // -----------------------------------------------------------기능 알림
    // 서비스-----------------------------------------------------------------------

    /**
     * 1. 월 CM 한도 변경 알림 (기존 메서드 - 호환성 유지)
     */
    public void sendMonthlyCmLimitChangedAlarm(Integer cmLimit) {
        // 1. 모든 사용자 목록 조회
        List<String> allUserIndexes = new ArrayList<>();
        allUserIndexes.addAll(userRepo.findUserIndexesByRole(1)); // 일반(정회원)
        allUserIndexes.addAll(userRepo.findUserIndexesByRole(2)); // 사업자
        allUserIndexes.addAll(userRepo.findUserIndexesByRole(3)); // 가맹점

        // 2. CM 한도 관리 권한을 가진 관리자 목록 조회
        List<String> adminUserIndexes = findAdminsWithAuthority(8); // CM 한도 관리 프로그램

        // 3. 공통 알림 전송 메서드 호출
        sendAlarmWithValue(3, allUserIndexes, adminUserIndexes, String.valueOf(cmLimit) + "CM", null);
    }

    /**
     * 2. 공지사항 알림 전송
     */
    public void sendNoticeAlarm(String noticeTitle) {
        // 1. 모든 사용자 목록 조회
        List<String> allUserIndexes = new ArrayList<>();
        allUserIndexes.addAll(userRepo.findUserIndexesByRole(1)); // 일반(정회원)
        allUserIndexes.addAll(userRepo.findUserIndexesByRole(2)); // 사업자
        allUserIndexes.addAll(userRepo.findUserIndexesByRole(3)); // 가맹점

        // 2. 공지사항 관리 권한을 가진 관리자 목록 조회
        List<String> adminUserIndexes = findAdminsWithAuthority(25); // 공지사항 관리 프로그램

        // 3. 공통 알림 전송 메서드 호출
        sendAlarmWithValue(5, allUserIndexes, adminUserIndexes, noticeTitle, null);
    }

    /**
     * 3. 선물 보내기 알림 전송
     */
    public void sendGiftAlarm(Integer receiveUserIndex, Integer giftAmount, Integer sendUserIndex) {
        try {
            // 1. 받는 사람 1명만 알림 대상으로 설정
            List<String> receiveUserIndexes = new ArrayList<>();
            receiveUserIndexes.add(String.valueOf(receiveUserIndex));

            // 2. 선물 알림 타입 ID (실제 DB의 alarmTypes 테이블에서 확인 필요)
            Integer giftAlarmTypeId = 10; // 실제 알림 타입 ID

            // 3. 기존 메시지 생성 메서드 활용 + "(몇CM)" 추가
            String value = String.valueOf(giftAmount) + "CM";
            sendAlarmWithValue(giftAlarmTypeId, receiveUserIndexes, new ArrayList<>(), value, sendUserIndex);
            
            log.info("선물 알림 전송 완료: 받는 사람={}, 금액={}CM", receiveUserIndex, giftAmount);
                    
        } catch (Exception e) {
            log.error("선물 알림 전송 중 오류: {}", e.getMessage());
        }
    }

    /**
     * 4. 쿠폰 선물 알림 전송
     */
    public void sendCouponAlarm(List<String> userIndexes, String storeUserIndex, String couponName) {
        try {
            // 2. 쿠폰 알림 타입 ID (실제 DB의 alarmTypes 테이블에서 확인 필요)
            Integer couponAlarmTypeId = 11; // 쿠폰 선물 알림 타입 ID

            // 3. storeUserIndex를 senderIndex로 변환
            Integer senderIndex = Integer.valueOf(storeUserIndex);

            // 4. 기존의 sendAlarmWithValue 메서드 사용
            sendAlarmWithValue(couponAlarmTypeId, userIndexes, new ArrayList<>(), couponName, senderIndex);
            
            log.info("쿠폰 선물 알림 전송 완료 - 쿠폰명: {}, 발신자: {}", couponName, storeUserIndex);
                    
        } catch (Exception e) {
            log.error("쿠폰 선물 알림 전송 중 오류: {}", e.getMessage());
        }
    }
}