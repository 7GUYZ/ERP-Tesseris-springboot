package com.jakdang.labs.api.updateLog.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jakdang.labs.api.updateLog.dto.UpdateUserLogResponseDTO;
import com.jakdang.labs.api.updateLog.dto.UpdateUserLogSearchRequest;
import com.jakdang.labs.api.updateLog.repository.UpdateUserLogRepositoryCustom;

@Service
public class UpdateUserLogService {
    
    @Autowired
    private UpdateUserLogRepositoryCustom updateUserLogRepository;

    public List<UpdateUserLogResponseDTO> searchLogs(UpdateUserLogSearchRequest request) {
        // LocalDate를 LocalDateTime으로 변환
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        
        if (request.getUpdateUserLogUpdateTimeStart() != null) {
            startDateTime = request.getUpdateUserLogUpdateTimeStart().atStartOfDay();
        }
        
        if (request.getUpdateUserLogUpdateTimeEnd() != null) {
            endDateTime = request.getUpdateUserLogUpdateTimeEnd().atTime(LocalTime.MAX);
        }

        return updateUserLogRepository.searchLogs(
            request.getUpdateUserId(),
            request.getInflictUserId(),
            request.getUpdateDataValue(),
            startDateTime,
            endDateTime
        );
    }
} 