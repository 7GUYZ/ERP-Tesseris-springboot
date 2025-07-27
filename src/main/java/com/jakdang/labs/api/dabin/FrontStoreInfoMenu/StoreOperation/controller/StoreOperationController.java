package com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreOperation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreOperation.dto.StoreOperationUpdateRequest;
import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreOperation.service.StoreOperationService;
import com.jakdang.labs.api.auth.dto.CustomUserDetails;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RestController
@RequestMapping("/api/store/operation")
@RequiredArgsConstructor
public class StoreOperationController {
    
    private final StoreOperationService storeOperationService;
    
    // JWT 기반 가맹점 운영정보 조회 (더 구체적인 패턴을 먼저 배치)
    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyStoreOperationInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String userId = userDetails.getUserId();
        System.out.println("🔍 [Controller] JWT 요청 받음 - userId: " + userId);
        System.out.println("🔍 [Controller] 요청 URL: /api/store/operation/my");
        
        Map<String, Object> result = storeOperationService.getStoreOperationInfoByUserId(userId);
        
        System.out.println("✅ [Controller] JWT 응답 결과: " + result);
        return ResponseEntity.ok(result);
    }
    
    // JWT 기반 가맹점 운영정보 수정 (더 구체적인 패턴을 먼저 배치)
    @PutMapping("/my")
    public ResponseEntity<Map<String, Object>> updateMyStoreOperationInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody StoreOperationUpdateRequest request) {
        String userId = userDetails.getUserId();
        System.out.println("🔍 [Controller] JWT 수정 요청 받음 - userId: " + userId);
        
        Map<String, Object> result = storeOperationService.updateStoreOperationInfoByUserId(userId, request);
        return ResponseEntity.ok(result);
    }
    
    // 기존 엔드포인트 (하위 호환성) - 더 일반적인 패턴을 나중에 배치
    @GetMapping("/{userIndex}")
    public ResponseEntity<Map<String, Object>> getStoreOperationInfo(@PathVariable("userIndex") Integer userIndex) {
        System.out.println("🔍 [Controller] 요청 받음 - userIndex: " + userIndex);
        System.out.println("🔍 [Controller] 요청 URL: /api/store/operation/" + userIndex);
        
        Map<String, Object> result = storeOperationService.getStoreOperationInfo(userIndex);
        
        System.out.println("✅ [Controller] 응답 결과: " + result);
        return ResponseEntity.ok(result);
    }
    
    // 기존 엔드포인트 (하위 호환성) - 더 일반적인 패턴을 나중에 배치
    @PutMapping("/{userIndex}")
    public ResponseEntity<Map<String, Object>> updateStoreOperationInfo(
            @PathVariable("userIndex") Integer userIndex,
            @RequestBody StoreOperationUpdateRequest request) {
        Map<String, Object> result = storeOperationService.updateStoreOperationInfo(userIndex, request);
        return ResponseEntity.ok(result);
    }
} 