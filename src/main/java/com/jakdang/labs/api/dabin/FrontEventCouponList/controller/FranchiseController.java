package com.jakdang.labs.api.dabin.FrontEventCouponList.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import com.jakdang.labs.api.dabin.FrontEventCouponList.service.FranchiseService;
import com.jakdang.labs.api.dabin.FrontEventCouponList.dto.NearbyFranchiseRequest;

@RestController
@RequestMapping("/api/franchise")
@RequiredArgsConstructor
public class FranchiseController {
    
    private final FranchiseService franchiseService;
    
    /**
     * 특정 가맹점 정보 조회
     */
    @GetMapping("/{storeIndex}")
    public ResponseEntity<?> getFranchiseInfo(@PathVariable("storeIndex") Integer storeIndex) {
        try {
            Map<String, Object> result = franchiseService.getFranchiseInfo(storeIndex);
            
            if (result == null) {
                return ResponseEntity.status(404).body(Map.of("error", "가맹점 정보를 찾을 수 없습니다."));
            }
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(500).body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "가맹점 정보 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    /**
     * 모든 가맹점 카테고리 조회
     */
    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories() {
        try {
            Map<String, Object> result = franchiseService.getAllCategories();
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(500).body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "카테고리 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    /**
     * 주변 가맹점 검색
     */
    @PostMapping("/nearby")
    public ResponseEntity<?> getNearbyFranchises(@RequestBody NearbyFranchiseRequest request) {
        try {
            // 필수 파라미터 검증
            if (request.getLatitude() == null || request.getLongitude() == null || request.getRadius() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "위도, 경도, 반경은 필수입니다."));
            }
            
            Map<String, Object> result = franchiseService.getNearbyFranchises(request);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(500).body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "주변 가맹점 검색 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
} 