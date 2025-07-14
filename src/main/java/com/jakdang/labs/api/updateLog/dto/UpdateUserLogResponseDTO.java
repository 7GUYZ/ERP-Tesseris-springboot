package com.jakdang.labs.api.updateLog.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserLogResponseDTO {
    private String updateUserId;
    private String updateUserRoleNm1;
    private String inflictUserId;
    private String updateUserRoleNm2;
    private String updateDataValue;
    private String updateBeforeData;
    private String updateAfterData;
    private LocalDateTime updateUserLogUpdateTime;

    public UpdateUserLogResponseDTO(
        String updateUserId,
        String updateUserRoleNm1,
        String inflictUserId,
        String updateUserRoleNm2,
        String updateDataValue,
        String updateBeforeData,
        String updateAfterData,
        LocalDateTime updateUserLogUpdateTime
    ) {
        this.updateUserId = updateUserId;
        this.updateUserRoleNm1 = updateUserRoleNm1;
        this.inflictUserId = inflictUserId;
        this.updateUserRoleNm2 = updateUserRoleNm2;
        this.updateDataValue = updateDataValue;
        this.updateBeforeData = updateBeforeData;
        this.updateAfterData = updateAfterData;
        this.updateUserLogUpdateTime = updateUserLogUpdateTime;
    }
} 