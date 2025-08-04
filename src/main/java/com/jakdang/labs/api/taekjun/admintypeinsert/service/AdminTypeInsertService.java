package com.jakdang.labs.api.taekjun.admintypeinsert.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jakdang.labs.api.taekjun.admintypeinsert.repository.AdminTypeInsertRepository;
import com.jakdang.labs.api.taekjun.admintypeinsert.dto.AdminTypeInsertDTO;
import com.jakdang.labs.api.taekjun.admintypeinsert.dto.AdminTypeUpdateDTO;
import com.jakdang.labs.entity.adminType;
import com.jakdang.labs.api.alarm.service.AlarmSvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminTypeInsertService {

    private final AdminTypeInsertRepository adminTypeInsertRepository;
    private final AlarmSvc alarmSvc;

    public List<adminType> getAllAdminTypesOrderByOrder() {
        return adminTypeInsertRepository.findAllByOrderByAdminTypeOrderAsc();
    }

    @Transactional
    public boolean insertAdminType(AdminTypeInsertDTO insertDTO, Integer userIndex) {
        try {
            if (insertDTO.getAdminTypeName() == null || insertDTO.getAdminTypeName().trim().isEmpty()) {
                log.error("adminTypeName이 비어있습니다.");
                return false;
            }

            if (insertDTO.getInsertPosition() == null || insertDTO.getInsertPosition() < 1) {
                log.error("insertPosition이 올바르지 않습니다: {}", insertDTO.getInsertPosition());
                return false;
            }

            Integer maxIndex = adminTypeInsertRepository.findMaxAdminTypeIndex().orElse(0);
            Integer newAdminTypeIndex = maxIndex + 1;

            adminTypeInsertRepository.incrementAdminTypeOrderFromPosition(insertDTO.getInsertPosition());

            adminType newAdminType = new adminType();
            newAdminType.setAdminTypeIndex(newAdminTypeIndex);
            newAdminType.setAdminTypeName(insertDTO.getAdminTypeName());
            newAdminType.setAdminTypeOrder(insertDTO.getInsertPosition());

            adminTypeInsertRepository.save(newAdminType);

            try {
                alarmSvc.sendAuthorityChangedAlarm(userIndex, insertDTO.getAdminTypeName(), "관리자 타입", "추가");
                log.info("관리자 타입 추가 알림 전송 완료: {}", insertDTO.getAdminTypeName());
            } catch (Exception e) {
                log.error("관리자 타입 추가 알림 전송 실패: {}", e.getMessage());
            }

            log.info("AdminType 삽입 완료 - 이름: {}, 위치: {}", insertDTO.getAdminTypeName(), insertDTO.getInsertPosition());
            return true;

        } catch (Exception e) {
            log.error("AdminType 삽입 중 오류 발생: ", e);
            return false;
        }
    }

    @Transactional
    public boolean updateAdminType(AdminTypeUpdateDTO updateDTO, Integer userIndex) {
        try {
            Optional<adminType> optionalAdminType = adminTypeInsertRepository.findById(updateDTO.getAdminTypeIndex());
            if (!optionalAdminType.isPresent()) {
                log.error("해당 관리자 타입을 찾을 수 없습니다: {}", updateDTO.getAdminTypeIndex());
                return false;
            }

            adminType adminType = optionalAdminType.get();
            String oldName = adminType.getAdminTypeName();
            Integer oldOrder = adminType.getAdminTypeOrder();

            // 이름 변경
            if (updateDTO.getAdminTypeName() != null && !updateDTO.getAdminTypeName().trim().isEmpty()) {
                adminType.setAdminTypeName(updateDTO.getAdminTypeName().trim());
            }

            // 순서 변경
            if (updateDTO.getNewOrder() != null && !updateDTO.getNewOrder().equals(oldOrder)) {
                // 최대 순서 값 확인
                Integer maxOrder = adminTypeInsertRepository.findMaxAdminTypeOrder().orElse(0);
                if (updateDTO.getNewOrder() < 1 || updateDTO.getNewOrder() > maxOrder) {
                    log.error("잘못된 순서 값입니다: {}", updateDTO.getNewOrder());
                    return false;
                }

                // 현재 위치에서 새 위치로 이동
                if (updateDTO.getNewOrder() > oldOrder) {
                    // 위치를 뒤로 이동: oldOrder+1 부터 newOrder까지 -1
                    adminTypeInsertRepository.decrementAdminTypeOrderBetween(oldOrder + 1, updateDTO.getNewOrder());
                } else {
                    // 위치를 앞으로 이동: newOrder부터 oldOrder-1까지 +1
                    adminTypeInsertRepository.incrementAdminTypeOrderBetween(updateDTO.getNewOrder(), oldOrder - 1);
                }
                
                // 현재 항목의 순서 변경
                adminType.setAdminTypeOrder(updateDTO.getNewOrder());
            }

            // 변경사항 저장
            adminTypeInsertRepository.save(adminType);

            try {
                alarmSvc.sendAuthorityChangedAlarm(userIndex, adminType.getAdminTypeName(), "관리자 타입", "수정");
                log.info("관리자 타입 수정 알림 전송 완료: {}", adminType.getAdminTypeName());
            } catch (Exception e) {
                log.error("관리자 타입 수정 알림 전송 실패: {}", e.getMessage());
            }

            return true;

        } catch (Exception e) {
            log.error("AdminType 수정 중 오류 발생: ", e);
            e.printStackTrace(); // 상세 에러 로그 추가
            return false;
        }
    }

    @Transactional
    public boolean deleteAdminType(Integer adminTypeIndex, Integer userIndex) {
        try {
            Optional<adminType> optionalAdminType = adminTypeInsertRepository.findById(adminTypeIndex);
            if (!optionalAdminType.isPresent()) {
                log.error("해당 관리자 타입을 찾을 수 없습니다: {}", adminTypeIndex);
                return false;
            }

            adminType adminType = optionalAdminType.get();
            Integer orderToDelete = adminType.getAdminTypeOrder();

            // 관련된 모든 권한 기능 삭제
            adminTypeInsertRepository.deleteAllAuthoritiesByAdminTypeIndex(adminTypeIndex);

            // 관리자 타입 삭제
            adminTypeInsertRepository.deleteById(adminTypeIndex);

            // 순서 재조정
            adminTypeInsertRepository.decrementAdminTypeOrderFromPosition(orderToDelete + 1);

            try {
                alarmSvc.sendAuthorityChangedAlarm(userIndex, adminType.getAdminTypeName(), "관리자 타입", "삭제");
                log.info("관리자 타입 삭제 알림 전송 완료: {}", adminType.getAdminTypeName());
            } catch (Exception e) {
                log.error("관리자 타입 삭제 알림 전송 실패: {}", e.getMessage());
            }

            return true;

        } catch (Exception e) {
            log.error("AdminType 삭제 중 오류 발생: ", e);
            return false;
        }
    }
} 