package com.jakdang.labs.api.dabin.FrontEventCouponList.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import com.jakdang.labs.api.dabin.FrontEventCouponList.service.StoreContactInfoService;

@RestController
@RequestMapping("/api/dabin/store-contact")
@RequiredArgsConstructor
public class StoreContactInfoController {
    private final StoreContactInfoService storeContactInfoService;

    /**
     * eventMasterIndex로 가맹점 전화, 주소, CM 정보 조회
     */
    @GetMapping("/info/{eventMasterIndex}")
    public ResponseEntity<?> getStoreContactInfo(@PathVariable("eventMasterIndex") Integer eventMasterIndex) {
        try {
            Map<String, Object> result = storeContactInfoService.getStoreContactInfo(eventMasterIndex);
            if (result == null) {
                return ResponseEntity.status(404).body(Map.of("error", "가맹점 정보를 찾을 수 없습니다."));
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "가맹점 정보 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
} 