package com.jakdang.labs.api.dabin.FrontEventCouponList.controller;

import com.jakdang.labs.api.dabin.FrontEventCouponList.service.CouponService;
import com.jakdang.labs.api.dabin.FrontEventCouponList.dto.CouponDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/coupon-detail")
@RequiredArgsConstructor
@Slf4j
public class CouponController {

    private final CouponService couponService;

    @GetMapping("/{couponIssuanceIndex}")
    public ResponseEntity<Map<String, Object>> getCouponDetail(
            @PathVariable Long couponIssuanceIndex,
            @RequestHeader("Authorization") String authHeader) {
        log.info("🔍 쿠폰 상세보기 요청: couponIssuanceIndex={}, authHeader={}", couponIssuanceIndex, authHeader != null ? "존재함" : "없음");
        try {
            // 실제 서비스 호출
            CouponDetailResponse couponDetail = couponService.getCouponDetail(couponIssuanceIndex, authHeader);
            
            if (couponDetail != null) {
                log.info("✅ 쿠폰 상세보기 성공: {}", couponDetail);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "coupon", couponDetail
                ));
            } else {
                log.warn("⚠️ 쿠폰 상세보기 실패: 보유중인 쿠폰이 아님");
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "보유중인 쿠폰이 아닙니다."
                ));
            }
        } catch (Exception e) {
            log.error("❌ 쿠폰 상세보기 오류: ", e);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "쿠폰 정보를 불러오는데 실패했습니다: " + e.getMessage()
            ));
        }
    }
} 