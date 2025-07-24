package com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreOperation.controller;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreOperation.dto.StoreOperationUpdateRequest;
import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreOperation.service.StoreOperationService;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RestController
@RequestMapping("/api/store/operation")
@RequiredArgsConstructor
public class StoreOperationController {
    
    private final StoreOperationService storeOperationService;
    
    @GetMapping("/{userIndex}")
    public ResponseEntity<Map<String, Object>> getStoreOperationInfo(@PathVariable("userIndex") Integer userIndex) {
        System.out.println("🔍 [Controller] 요청 받음 - userIndex: " + userIndex);
        System.out.println("🔍 [Controller] 요청 URL: /api/store/operation/" + userIndex);
        
        Map<String, Object> result = storeOperationService.getStoreOperationInfo(userIndex);
        
        System.out.println("✅ [Controller] 응답 결과: " + result);
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/{userIndex}")
    public ResponseEntity<Map<String, Object>> updateStoreOperationInfo(
            @PathVariable("userIndex") Integer userIndex,
            @RequestBody StoreOperationUpdateRequest request) {
        Map<String, Object> result = storeOperationService.updateStoreOperationInfo(userIndex, request);
        return ResponseEntity.ok(result);
    }
} 