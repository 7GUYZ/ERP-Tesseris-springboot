package com.jakdang.labs.api.jiyun.cmsAccessLog.controller;

import com.jakdang.labs.api.jiyun.cmsAccessLog.service.CmsAccessLogService;
import com.jakdang.labs.api.common.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cms-access-log")
@RequiredArgsConstructor
public class CmsAccessLogController {
    
    private final CmsAccessLogService cmsAccessLogService;
    
    @GetMapping("/search")
    public ResponseDTO<?> searchCmsAccessLogs(
        @RequestParam(name = "userEmail", required = false, defaultValue = "") String userEmail,
        @RequestParam(name = "userName", required = false, defaultValue = "") String userName,
        @RequestParam(name = "cmsAccessUserIp", required = false, defaultValue = "") String cmsAccessUserIp,
        @RequestParam(name = "adminTypeIndex", required = false, defaultValue = "0") String adminTypeIndex,
        @RequestParam(name = "cmsAccessUserTimeStart", required = false) String cmsAccessUserTimeStart,
        @RequestParam(name = "cmsAccessUserTimeEnd", required = false) String cmsAccessUserTimeEnd
    ) {
        try {
            List<Object[]> logs = cmsAccessLogService.searchCmsAccessLogs(
                userEmail, userName, cmsAccessUserIp, adminTypeIndex, cmsAccessUserTimeStart, cmsAccessUserTimeEnd
            );
            return ResponseDTO.<List<Object[]>>createSuccessResponse("조회 성공", logs);
        } catch (Exception e) {
            return ResponseDTO.createErrorResponse(500, "조회 실패: " + e.getMessage());
        }
    }
    
    @GetMapping("/admin-types")
    public ResponseDTO<?> getAllAdminTypes() {
        try {
            List<com.jakdang.labs.api.jiyun.cmsAccessLog.dto.AdminTypeDTO> adminTypes = cmsAccessLogService.getAllAdminTypes();
            return ResponseDTO.<List<com.jakdang.labs.api.jiyun.cmsAccessLog.dto.AdminTypeDTO>>createSuccessResponse("관리자 타입 조회 성공", adminTypes);
        } catch (Exception e) {
            return ResponseDTO.createErrorResponse(500, "관리자 타입 조회 실패: " + e.getMessage());
        }
    }
} 