package com.jakdang.labs.api.jiyun.updateLog.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.jakdang.labs.api.jiyun.updateLog.dto.UpdateUserLogResponseDTO;

public interface UpdateUserLogRepositoryCustom {
    List<UpdateUserLogResponseDTO> searchLogs(
        String updateUserId,
        String inflictUserId,
        String updateDataValue,
        LocalDateTime updateUserLogUpdateTimeStart,
        LocalDateTime updateUserLogUpdateTimeEnd
    );
} 