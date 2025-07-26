package com.jakdang.labs.api.jungeun.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.jakdang.labs.api.jungeun.controller.WebSocketController;
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
     * 월 CM 한도 변경 알림 (모든 사용자 + 권한 있는 관리자)
     */
    @Transactional // 알림 내역 DB 저장 필요
    public void sendMonthlyCmLimitChangedAlarm(Integer cmLimit) {
        Map<String, Object> notification = Map.of(
                "type", "MONTHLY_CM_LIMIT_UPDATED",
                "title", "월 CM 한도 변경 알림",
                "message", "월 CM 한도가 " + cmLimit + "CM으로 변경되었습니다.",
                "timestamp", System.currentTimeMillis(),
                "action", "MONTHLY_CM_LIMIT");

        // 1. 모든 사용자에게 알림
        List<String> allUserIndexes = new ArrayList<>();
        allUserIndexes.addAll(userRepo.findUserIndexesByRole(1)); // 일반(정회원)
        allUserIndexes.addAll(userRepo.findUserIndexesByRole(2)); // 사업자
        allUserIndexes.addAll(userRepo.findUserIndexesByRole(3)); // 가맹점
        webSocketController.sendToManyUsers(allUserIndexes, notification);

        // 2. CM 한도 관리 권한을 가진 관리자들에게 알림
        List<String> adminUserIndexes = findAdminsWithAuthority(8); // CM 한도 관리 프로그램
        webSocketController.sendToManyUsers(adminUserIndexes, notification);

        log.info("월 CM 한도 변경 알림 전송 완료 - 사용자: {}명, 관리자: {}명",
                allUserIndexes.size(), adminUserIndexes.size());
    }

}
