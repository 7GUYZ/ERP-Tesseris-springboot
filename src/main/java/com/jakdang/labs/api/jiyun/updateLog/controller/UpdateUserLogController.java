package com.jakdang.labs.api.jiyun.updateLog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jakdang.labs.api.jiyun.updateLog.dto.UpdateUserLogResponseDTO;
import com.jakdang.labs.api.jiyun.updateLog.dto.UpdateUserLogSearchRequest;
import com.jakdang.labs.api.jiyun.updateLog.service.UpdateUserLogService;

@RestController
@RequestMapping("/api/update-log")
public class UpdateUserLogController {
    
    @Autowired
    private UpdateUserLogService updateUserLogService;

    @GetMapping("/search")
    public ResponseEntity<List<UpdateUserLogResponseDTO>> searchLogs(
        @RequestParam(name = "updateUserId", required = false, defaultValue = "") String updateUserId,
        @RequestParam(name = "inflictUserId", required = false, defaultValue = "") String inflictUserId,
        @RequestParam(name = "updateDataValue", required = false, defaultValue = "") String updateDataValue,
        @RequestParam(name = "updateUserLogUpdateTimeStart", required = false) String updateUserLogUpdateTimeStart,
        @RequestParam(name = "updateUserLogUpdateTimeEnd", required = false) String updateUserLogUpdateTimeEnd
    ) {
        UpdateUserLogSearchRequest request = new UpdateUserLogSearchRequest();
        request.setUpdateUserId(updateUserId);
        request.setInflictUserId(inflictUserId);
        request.setUpdateDataValue(updateDataValue);
        
        // String을 LocalDate로 변환 (필요시)
        if (updateUserLogUpdateTimeStart != null && !updateUserLogUpdateTimeStart.isEmpty()) {
            request.setUpdateUserLogUpdateTimeStart(java.time.LocalDate.parse(updateUserLogUpdateTimeStart));
        }
        
        if (updateUserLogUpdateTimeEnd != null && !updateUserLogUpdateTimeEnd.isEmpty()) {
            request.setUpdateUserLogUpdateTimeEnd(java.time.LocalDate.parse(updateUserLogUpdateTimeEnd));
        }

        List<UpdateUserLogResponseDTO> results = updateUserLogService.searchLogs(request);
        return ResponseEntity.ok(results);
    }
} 