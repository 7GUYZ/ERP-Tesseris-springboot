package com.jakdang.labs.api.dabin.FrontCommissionManage.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.jakdang.labs.api.auth.dto.CustomUserDetails;

import com.jakdang.labs.api.dabin.FrontCommissionManage.dto.UserCommissionHistoryResponse;
import com.jakdang.labs.api.dabin.FrontCommissionManage.service.UserCommissionHistoryService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/user/commission-history")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserCommissionHistoryController {

    private final UserCommissionHistoryService userCommissionHistoryService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserCommissionHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "limit", defaultValue = "20") Integer limit) {
        
        // JWT에서 사용자 정보 추출
        String userId = userDetails.getUserId();
        // userIndex는 별도로 조회해야 함 (JWT에는 userIndex가 없을 수 있음)
        Integer userIndex = 119; // 임시로 하드코딩, 나중에 UserTesseris 조회 로직 추가
        try {
            List<UserCommissionHistoryResponse> history = userCommissionHistoryService.getUserCommissionHistory(userIndex, page, limit);
            Long totalCount = userCommissionHistoryService.getTotalCount(userIndex);
            Long totalPages = (long) Math.ceil((double) totalCount / limit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", history);
            response.put("totalCount", totalCount);
            response.put("totalPages", totalPages);
            response.put("currentPage", page);
            response.put("limit", limit);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "수당 내역 조회 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }


} 