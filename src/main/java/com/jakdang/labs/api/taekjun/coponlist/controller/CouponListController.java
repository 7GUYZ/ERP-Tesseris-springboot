package com.jakdang.labs.api.taekjun.coponlist.controller;

import com.jakdang.labs.api.taekjun.coponlist.service.CouponListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/coponlist")
@RequiredArgsConstructor
@Slf4j
public class CouponListController {
    
    private final CouponListService couponListService;
    
    /**
     * 내가 받은 쿠폰 리스트 조회
     */
    @GetMapping("/my-coupons")
    public ResponseEntity<Map<String, Object>> getMyCoupons(@RequestParam String userIndex) {
        log.info("내 쿠폰 리스트 조회 요청 - userIndex: {}", userIndex);
        
        try {
            List<Map<String, Object>> coupons = couponListService.getMyCoupons(userIndex);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("coupons", coupons);
            response.put("message", "내 쿠폰 리스트를 성공적으로 조회했습니다.");
            
            log.info("내 쿠폰 리스트 조회 완료 - 조회된 쿠폰 수: {}", coupons.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("내 쿠폰 리스트 조회 중 오류 발생", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "내 쿠폰 리스트 조회 중 오류가 발생했습니다.");
            
            return ResponseEntity.badRequest().body(response);
        }
    }
} 