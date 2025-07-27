package com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.jakdang.labs.api.auth.dto.CustomUserDetails;
import com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.service.FrontMyPageStoreInfoService;
import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.dto.StoreUpdateRequest;
import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.service.StoreInfoService;
import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.entity.StoreCategory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/store/basic-info")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class StoreInfoController {
    
    private final StoreInfoService storeInfoService;
    
    /**
     * JWT 방식의 가맹점 정보 조회
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getStoreInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String userId = userDetails.getUserId();
        Map<String, Object> result = storeInfoService.getStoreInfoByUserId(userId);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * JWT 방식의 가맹점 정보 수정
     */
    @PutMapping("/info")
    public ResponseEntity<Map<String, Object>> updateStoreInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody StoreUpdateRequest request) {
        String userId = userDetails.getUserId();
        Map<String, Object> result = storeInfoService.updateStoreInfoByUserId(userId, request);
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 매장 카테고리 목록 조회 (데이터베이스에서 조회)
     */
    @GetMapping("/categories")
    public ResponseDTO<List<StoreCategory>> getStoreCategories() {
        List<StoreCategory> categories = storeInfoService.getAllStoreCategories();
        return ResponseDTO.createSuccessResponse("카테고리 목록 조회 성공", categories);
    }
} 