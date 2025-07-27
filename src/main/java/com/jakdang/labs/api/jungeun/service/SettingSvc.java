package com.jakdang.labs.api.jungeun.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jakdang.labs.api.alarm.service.AlarmSvc;
import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.jungeun.dto.SettingDTO;
import com.jakdang.labs.api.jungeun.repository.SettingLjeRepo;
import com.jakdang.labs.entity.Setting;
import com.jakdang.labs.exceptions.handler.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettingSvc {
    private final SettingLjeRepo settingRepo;
    private final AlarmSvc alarmSvc;

    public ResponseDTO<SettingDTO> getCmLimit(){
        Setting setting = settingRepo.findBySettingIndex(Integer.valueOf(2));
        if (setting == null) {
            throw new CustomException("월CM한도 설정값을 불러올 수 없습니다.", -200);
        }

        return ResponseDTO.createSuccessResponse("월CM한도 설정값 불러오기 성공", SettingDTO.builder()
            .settingName(setting.getSettingName())
            .settingValue(setting.getSettingValue())
            .build());
    }

    // 월 CM 한도 수정
    @Transactional
    public ResponseDTO<SettingDTO> saveCmLimit(SettingDTO updateDTO){
        Setting setting = settingRepo.findBySettingIndex(Integer.valueOf(2));
        if(setting != null){
            try {
                // 1. DB 저장 (트랜잭션 내에서)
                setting.setSettingIndex(Integer.valueOf(2));
                setting.setSettingName("cm_use_limit");
                setting.setSettingValue(updateDTO.getSettingValue());
                settingRepo.save(setting);
                
                // 2. DB 저장 성공 후 알림 전송 (트랜잭션 커밋 후)
                Integer cmLimit = Integer.valueOf(updateDTO.getSettingValue());
                
                return ResponseDTO.createSuccessResponse("월CM한도 업데이트 성공", 
                    SettingDTO.builder()
                        .settingName(setting.getSettingName())
                        .settingValue(setting.getSettingValue())
                        .build());
                        
            } catch (Exception e) {
                log.error("월 CM 한도 저장 실패: {}", e.getMessage());
                throw new CustomException("월CM한도 저장에 실패했습니다.", -200);
            }
        } else {
            throw new CustomException("월CM한도 설정값을 불러올 수 없습니다.", -200);
        }
    }
    
    /**
     * 월 CM 한도 변경 알림 전송 (트랜잭션 외부에서 호출)
     */
    public void sendCmLimitChangedAlarm(Integer cmLimit) {
        try {
            alarmSvc.sendMonthlyCmLimitChangedAlarm(cmLimit);
            log.info("월 CM 한도 변경 알림 전송 완료: {}", cmLimit);
        } catch (Exception e) {
            log.error("월 CM 한도 변경 알림 전송 실패: {}", e.getMessage());
            // 알림 전송 실패해도 DB 저장은 성공으로 처리
        }
    }
}
