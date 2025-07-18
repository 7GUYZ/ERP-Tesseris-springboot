package com.jakdang.labs.api.jiyun.updateLog.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserLogSearchRequest {
    private String updateUserId;
    private String inflictUserId;
    private String updateDataValue;
    private LocalDate updateUserLogUpdateTimeStart;
    private LocalDate updateUserLogUpdateTimeEnd;
} 