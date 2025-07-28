package com.jakdang.labs.api.alarm.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAlarmsDTO {
    private Integer userAlarmsId;
    private Integer userIndex;
    private Integer alarmTypesId;
    private Integer isActive;
    private LocalDateTime updatedAt;
    
    // 알림 타입 정보 (선택적)
    private String alarmTypesCode;
    private String alarmTypesLabel;
    private String alarmTypesDescription;
}
