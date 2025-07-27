package com.jakdang.labs.api.alarm.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmHistoryRequest {
    private Integer alarmTypesId;
    private String alarmMessage;
    private Integer senderIndex;
    private List<Integer> receiverIndexes;
    private String alarmType;
    private String title;
} 