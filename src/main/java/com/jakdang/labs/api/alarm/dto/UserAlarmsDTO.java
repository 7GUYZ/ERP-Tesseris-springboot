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
    private Integer userIndex;
    private Integer alarmTypesId;
    private Integer isActive;
    private LocalDateTime updatedAt;
}
