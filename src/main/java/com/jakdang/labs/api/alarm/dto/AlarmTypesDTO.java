package com.jakdang.labs.api.alarm.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmTypesDTO {
    private Integer alarmTypesId;
    private String alarmTypesCode;
    private String alarmTypesLabel;
    private String alarmTypesDescription;
    private LocalDateTime alarmTypesCreatedAt;
} 