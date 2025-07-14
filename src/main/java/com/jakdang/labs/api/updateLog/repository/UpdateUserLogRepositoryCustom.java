package com.jakdang.labs.api.updateLog.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.jakdang.labs.api.updateLog.dto.UpdateUserLogResponseDTO;

public interface UpdateUserLogRepositoryCustom {
    List<UpdateUserLogResponseDTO> searchLogs(
        String updateUserId,
        String inflictUserId,
        String updateDataValue,
        LocalDateTime updateUserLogUpdateTimeStart,
        LocalDateTime updateUserLogUpdateTimeEnd
    );
} 