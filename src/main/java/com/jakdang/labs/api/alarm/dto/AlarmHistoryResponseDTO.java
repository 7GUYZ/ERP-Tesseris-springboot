package com.jakdang.labs.api.alarm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmHistoryResponseDTO {
    private Integer alarmId;
    private String title;
    private String message;
    private LocalDateTime createdAt;
    private String alarmTypeLabel;
    private Integer isRead;
    private String alarmTypeCode;
} 