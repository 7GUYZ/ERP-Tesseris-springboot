package com.jakdang.labs.api.jungeun.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
         setting.setSettingIndex(Integer.valueOf(2));
         setting.setSettingName("cm_use_limit");
         setting.setSettingValue(updateDTO.getSettingValue());
         settingRepo.save(setting);
         
         // CM 한도 변경 알림 전송
         try {
             alarmSvc.sendMonthlyCmLimitChangedAlarm(Integer.valueOf(updateDTO.getSettingValue()));
             log.info("월 CM 한도 변경 알림 전송 완료: {}", updateDTO.getSettingValue());
         } catch (Exception e) {
             log.error("월 CM 한도 변경 알림 전송 실패: {}", e.getMessage());
             // 알림 전송 실패해도 저장은 성공으로 처리
         }
         
         return ResponseDTO.createSuccessResponse("월CM한도 업데이트 성공", 
         SettingDTO.builder()
            .settingName(setting.getSettingName())
            .settingValue(setting.getSettingValue())
            .build());
        }else{
            throw new CustomException("월CM한도 설정값을 불러올 수 없습니다.", -200);
        }
    }
}
