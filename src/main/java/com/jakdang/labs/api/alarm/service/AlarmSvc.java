package com.jakdang.labs.api.alarm.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    
    // 알림 타입 상수 정의
    private static final class AlarmTypeIds {
        public static final Integer AUTHORITY_CHANGED = 1;
        public static final Integer ADMIN_REGISTER = 2;
        public static final Integer MONTHLY_TS_LIMIT = 3;
        public static final Integer COMMISSION_CHANGED = 4;
        public static final Integer NOTICE = 5;
        public static final Integer QNA_REGISTER = 6;
        public static final Integer QNA_ANSWER = 7;
        public static final Integer STORE_REGISTER = 9;
        public static final Integer GIFT = 10;
        public static final Integer COUPON = 11;
    }
    
    // 프로그램 인덱스 상수 정의
    private static final class ProgramIndexes {
        public static final Integer AUTHORITY_MANAGEMENT = 8;
        public static final Integer COMMISSION_MANAGEMENT = 9;
        public static final Integer ADMIN_MANAGEMENT = 10;
        public static final Integer TS_LIMIT_MANAGEMENT = 8;
        public static final Integer NOTICE_MANAGEMENT = 25;
        public static final Integer QNA_MANAGEMENT = 26;
        public static final Integer STORE_REGISTER_MANAGEMENT = 33;
    }
    
    // 사용자 역할 상수 정의
    private static final class UserRoles {
        public static final Integer REGULAR_MEMBER = 1;
        public static final Integer BUSINESSMAN = 2;
        public static final Integer FRANCHISE = 3;
    }
    
    private final WebSocketController webSocketController;
    private final UserTesserisLjeRepo userRepo;
    private final AuthorityLjeRepo authorityRepo;
    private final AlarmServiceClient alarmServiceClient;

    /**
     * 사용자의 특정 알림 타입 설정 조회
     */
    public Map<String, Object> getUserAlarmSetting(Integer userIndex, Integer alarmTypesId) {
        log.info("🔍 사용자 알림 설정 조회 - userIndex: {}, alarmTypesId: {}", userIndex, alarmTypesId);
        
        try {
            log.info("🔍 alarmServiceClient 호출 시작");
            
            // alarm-service에서 사용자의 알림 설정 조회
            List<UserAlarmsDTO> userAlarms = alarmServiceClient.getUserAlarmsByUserIndex(userIndex);
            
            log.info("✅ alarm-service 응답 받음 - 조회된 알림 설정 개수: {}", userAlarms != null ? userAlarms.size() : "null");
            
            if (userAlarms == null) {
                log.warn("⚠️ alarm-service에서 null 응답 받음");
                userAlarms = new ArrayList<>();
            }
            
            // 특정 알림 타입의 설정 찾기
            Optional<UserAlarmsDTO> targetAlarm = userAlarms.stream()
                    .filter(alarm -> alarm != null && alarmTypesId.equals(alarm.getAlarmTypesId()))
                    .findFirst();
            
            Map<String, Object> response = new HashMap<>();
            
            if (targetAlarm.isPresent()) {
                // 설정이 있는 경우: isActive 값 반환
                UserAlarmsDTO alarm = targetAlarm.get();
                Integer isActive = alarm.getIsActive();
                response.put("hasSetting", true);
                response.put("isActive", isActive);
                response.put("message", isActive == 1 ? "알림 활성화" : "알림 비활성화");
                
                log.info("✅ 알림 설정 조회 완료 - userIndex: {}, alarmTypesId: {}, isActive: {}", 
                    userIndex, alarmTypesId, isActive);
            } else {
                // 설정이 없는 경우: 기본값 반환
                response.put("hasSetting", false);
                response.put("isActive", null);
                response.put("message", "알림 설정 없음 (기본값: 활성화)");
                
                log.info("✅ 알림 설정 없음 - userIndex: {}, alarmTypesId: {}", userIndex, alarmTypesId);
            }
            
            return response;
            
        } catch (Exception e) {
            log.error("❌ 사용자 알림 설정 조회 실패 - userIndex: {}, alarmTypesId: {}", userIndex, alarmTypesId, e);
            log.error("❌ 예외 상세 정보:", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "알림 설정 조회에 실패했습니다.");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("details", e.toString());
            
            return errorResponse;
        }
    }

    /**
     * 특정 프로그램 권한을 가진 관리자들의 user_index 목록 조회 (범용 메서드)
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
                    .senderIndex(finalSenderIndex)
                    .receiverIndexes(receiverIndexes)
                    .alarmType("MONTHLY_TS_LIMIT_UPDATED")
                    .title("월 TS 한도 변경 알림")
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

        // 4. WebSocket으로 알림 전송 (비동기)
        Map<String, Object> notification = createNotification(alarmTypeId, alarmMessage);

        CompletableFuture.runAsync(() -> {
            try {
                webSocketController.sendToManyUsers(activeUserIndexes, notification);
                webSocketController.sendToManyUsers(activeAdminIndexes, notification);
                log.info("WebSocket 알림 전송 완료 (비동기) - 활성 사용자: {}명, 활성 관리자: {}명",
                        activeUserIndexes.size(), activeAdminIndexes.size());
            } catch (Exception e) {
                log.error("WebSocket 알림 전송 실패 (비동기): {}", e.getMessage());
            }
        });

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

        log.info("알림 전송 요청 완료 - 활성 사용자: {}명, 활성 관리자: {}명, 발신자: {} (백그라운드에서 처리됨)",
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
                    "action", alarmTypeCode,
                    "alarmTypeId", alarmTypeId);

        } catch (Exception e) {
            log.warn("알림 객체 생성 실패, 기본값 사용: {}", e.getMessage());
            return Map.of(
                    "type", "ERROR_NOTIFICATION",
                    "title", "알림 오류",
                    "message", message,
                    "timestamp", System.currentTimeMillis(),
                    "action", "ERROR_NOTIFICATION"
            );
        }
    }

    /**
     * 모든 사용자 목록 조회 (공통 메서드)
     */
    private List<String> getAllUserIndexes() {
        List<String> allUserIndexes = new ArrayList<>();
        allUserIndexes.addAll(userRepo.findUserIndexesByRole(UserRoles.REGULAR_MEMBER));
        allUserIndexes.addAll(userRepo.findUserIndexesByRole(UserRoles.BUSINESSMAN));
        allUserIndexes.addAll(userRepo.findUserIndexesByRole(UserRoles.FRANCHISE));
        return allUserIndexes;
    }

    /**
     * 단일 사용자 목록 생성 (공통 메서드)
     */
    private List<String> createSingleUserList(Integer userIndex) {
        List<String> userIndexes = new ArrayList<>();
        userIndexes.add(String.valueOf(userIndex));
        return userIndexes;
    }

    /**
     * 비동기 알림 전송 (공통 메서드)
     */
    private void sendAsyncAlarm(Runnable alarmTask, String alarmName) {
        CompletableFuture.runAsync(() -> {
            try {
                alarmTask.run();
                log.info("{} 알림 전송 완료", alarmName);
            } catch (Exception e) {
                log.error("{} 알림 전송 중 오류: {}", alarmName, e.getMessage());
            }
        });
    }

    // ==========================================
    // 기능별 알림 전송 메서드들
    // ==========================================

    /**
     * 1. 월 TS 한도 변경 알림
     */
    public void sendMonthlyCmLimitChangedAlarm(Integer userIndex, Integer cmLimit) {
        List<String> allUserIndexes = getAllUserIndexes();
        List<String> adminUserIndexes = findAdminsWithAuthority(ProgramIndexes.TS_LIMIT_MANAGEMENT);
        String value = String.valueOf(cmLimit) + "TS";
        sendAlarmWithValue(AlarmTypeIds.MONTHLY_TS_LIMIT, allUserIndexes, adminUserIndexes, value, userIndex);
    }

    /**
     * 2. 공지사항 알림 전송
     */
    public void sendNoticeAlarm(Integer userIndex, String noticeTitle) {
        List<String> allUserIndexes = getAllUserIndexes();
        List<String> adminUserIndexes = findAdminsWithAuthority(ProgramIndexes.NOTICE_MANAGEMENT);
        sendAlarmWithValue(AlarmTypeIds.NOTICE, allUserIndexes, adminUserIndexes, noticeTitle, userIndex);
    }

    /**
     * 3. 선물 보내기 알림 전송
     */
    public void sendGiftAlarm(Integer receiveUserIndex, Integer giftAmount, Integer sendUserIndex) {
        try {
            List<String> receiveUserIndexes = createSingleUserList(receiveUserIndex);
            String value = String.valueOf(giftAmount) + "TS";
            sendAlarmWithValue(AlarmTypeIds.GIFT, receiveUserIndexes, new ArrayList<>(), value, sendUserIndex);
            log.info("선물 알림 전송 완료: 받는 사람={}, 금액={}TS", receiveUserIndex, giftAmount);
        } catch (Exception e) {
            log.error("선물 알림 전송 중 오류: {}", e.getMessage());
        }
    }

    /**
     * 4. 쿠폰 선물 알림 전송
     */
    public void sendCouponAlarm(List<String> userIndexes, String storeUserIndex, String couponName) {
        try {
            Integer senderIndex = Integer.valueOf(storeUserIndex);
            sendAlarmWithValue(AlarmTypeIds.COUPON, userIndexes, new ArrayList<>(), couponName, senderIndex);
            log.info("쿠폰 선물 알림 전송 완료 - 쿠폰명: {}, 발신자: {}", couponName, storeUserIndex);
        } catch (Exception e) {
            log.error("쿠폰 선물 알림 전송 중 오류: {}", e.getMessage());
        }
    }

    /**
     * 5. 중계수수료 변경 알림 전송 (비동기 처리)
     */
    public void sendCommissionChangedAlarm(Integer userIndex) {
        sendAsyncAlarm(() -> {
            List<String> adminUserIndexes = findAdminsWithAuthority(ProgramIndexes.COMMISSION_MANAGEMENT);
            sendAlarmWithValue(AlarmTypeIds.COMMISSION_CHANGED, new ArrayList<>(), adminUserIndexes, "", userIndex);
            log.info("중계수수료 변경 알림 전송 완료 - 대상 관리자: {}명", adminUserIndexes.size());
        }, "중계수수료 변경");
    }

    /**
     * 6. 권한 변경 알림 전송 (비동기 처리)
     */
    public void sendAuthorityChangedAlarm(Integer userIndex, String adminTypeName, String programName, String changeType) {
        sendAsyncAlarm(() -> {
            List<String> adminUserIndexes = findAdminsWithAuthority(ProgramIndexes.AUTHORITY_MANAGEMENT);
            String alarmMessage = createAuthorityChangeMessage(adminTypeName, programName, changeType);
            sendAlarmWithValue(AlarmTypeIds.AUTHORITY_CHANGED, new ArrayList<>(), adminUserIndexes, alarmMessage, userIndex);
            log.info("권한 {} 알림 전송 완료 - 등급: {}, 프로그램: {}, 대상 관리자: {}명", 
                changeType, adminTypeName, programName, adminUserIndexes.size());
        }, "권한 변경");
    }

    /**
     * 권한 변경 메시지 생성 (공통 메서드)
     */
    private String createAuthorityChangeMessage(String adminTypeName, String programName, String changeType) {
        switch (changeType) {
            case "수정":
                return String.format("%s 등급의 %s 권한이 수정되었습니다.", adminTypeName, programName);
            case "추가":
                return String.format("%s 등급의 %s 권한이 추가되었습니다.", adminTypeName, programName);
            case "삭제":
                return String.format("%s 등급의 %s 권한이 삭제되었습니다.", adminTypeName, programName);
            default:
                return String.format("%s 등급의 %s 권한이 변경되었습니다.", adminTypeName, programName);
        }
    }

    /**
     * 7. 가맹점 신청 처리(승인/반려)
     */
    public void sendStoreRegisterAlarm(Integer userIndex, Integer storeRequestStatusIndex) {
        sendAsyncAlarm(() -> {
            String alarmMessage = createStoreRegisterMessage(storeRequestStatusIndex);
            List<String> userIndexes = createSingleUserList(userIndex);
            sendAlarmWithValue(AlarmTypeIds.STORE_REGISTER, userIndexes, new ArrayList<>(), alarmMessage, null);
            log.info("가맹점 신청 처리 알림 전송 완료 - 가맹점 신청 user_index: {}", userIndex);
        }, "가맹점 신청 처리");
    }

    /**
     * 가맹점 신청 메시지 생성 (공통 메서드)
     */
    private String createStoreRegisterMessage(Integer storeRequestStatusIndex) {
        switch (storeRequestStatusIndex) {
            case 2:
            case 3:
                return "가맹점 신청이 승인되었습니다.";
            default:
                return "가맹점 신청이 처리되었습니다.";
        }
    }

    /**
     * 8. 신규 관리자 등록 알림 전송
     */
    public void sendAdminRegisterAlarm(Integer userIndex) {
        sendAsyncAlarm(() -> {
            List<String> adminUserIndexes = findAdminsWithAuthority(ProgramIndexes.ADMIN_MANAGEMENT);
            sendAlarmWithValue(AlarmTypeIds.ADMIN_REGISTER, new ArrayList<>(), adminUserIndexes, "", userIndex);
            log.info("신규 관리자 등록 알림 전송 완료 - 동작 관리자: {}, 대상 관리자: {}명", userIndex, adminUserIndexes.size());
        }, "신규 관리자 등록");
    }

    /**
     * 9. 신규 Q&A 등록 알림 전송
     */
    public void sendQnaRegisterAlarm(Integer userIndex) {
        sendAsyncAlarm(() -> {
            List<String> adminUserIndexes = findAdminsWithAuthority(ProgramIndexes.QNA_MANAGEMENT);
            sendAlarmWithValue(AlarmTypeIds.QNA_REGISTER, new ArrayList<>(), adminUserIndexes, null, userIndex);
        }, "신규 Q&A 등록");
    }

    /**
     * 10. Q&A 답변 알림 전송
     */
    public void sendQnaAnswerAlarm(Integer userIndex, Integer adminIndex) {
        sendAsyncAlarm(() -> {
            List<String> userIndexes = createSingleUserList(userIndex);
            sendAlarmWithValue(AlarmTypeIds.QNA_ANSWER, userIndexes, new ArrayList<>(), null, adminIndex);
        }, "Q&A 답변 완료");
    }

    /**
     * 11. 가맹점 신청 등록 알림 전송 (user->admin)
     */
    public void sendNewStoreRegisterAlarm(Integer userIndex) {
        sendAsyncAlarm(() -> {
            List<String> adminUserIndexes = findAdminsWithAuthority(ProgramIndexes.STORE_REGISTER_MANAGEMENT);
            sendAlarmWithValue(AlarmTypeIds.STORE_REGISTER, new ArrayList<>(), adminUserIndexes, null, userIndex);
        }, "신규 가맹점 신청 접수");
    }
}