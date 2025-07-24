package com.jakdang.labs.api.dabin.FrontEventCouponList.controller;

import com.jakdang.labs.api.dabin.FrontEventCouponList.service.EventListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dabin/event-list")
@RequiredArgsConstructor
public class EventListController {

    private final EventListService eventListService;

    /**
     * 활성 이벤트 목록 조회 (프랜차이즈 사용자용)
     */
    @GetMapping("/active")
    public ResponseEntity<?> getActiveEvents(@RequestParam Map<String, Object> params) {
        try {
            var result = eventListService.getActiveEvents();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "이벤트 목록 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 종료된 이벤트 목록 조회 (프랜차이즈 사용자용)
     */
    @GetMapping("/ended")
    public ResponseEntity<?> getEndedEvents(@RequestParam Map<String, Object> params) {
        try {
            var result = eventListService.getEndedEvents();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "종료된 이벤트 목록 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 이벤트 상세 정보 조회 (프랜차이즈 사용자용)
     */
    @GetMapping("/detail/{eventMasterIndex}")
    public ResponseEntity<?> getEventDetail(@PathVariable("eventMasterIndex") Integer eventMasterIndex, 
                                          @RequestParam Map<String, Object> params) {
        try {
            var result = eventListService.getEventDetail(eventMasterIndex);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "이벤트 상세 정보 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 쿠폰 다운로드 (사용자용)
     */
    @PostMapping("/coupon/download")
    public ResponseEntity<?> downloadCoupon(@RequestBody Map<String, Object> request) {
        try {
            Integer eventMasterIndex = (Integer) request.get("eventMasterIndex");
            Integer couponIndex = (Integer) request.get("couponIndex");
            
            var result = eventListService.downloadCoupon(eventMasterIndex, couponIndex);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "쿠폰 다운로드 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
} 