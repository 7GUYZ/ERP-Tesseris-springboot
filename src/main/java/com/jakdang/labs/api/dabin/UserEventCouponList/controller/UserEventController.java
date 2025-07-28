package com.jakdang.labs.api.dabin.UserEventCouponList.controller;

import com.jakdang.labs.api.dabin.UserEventCouponList.service.UserEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user/event-list")
@RequiredArgsConstructor
public class UserEventController {

    private final UserEventService userEventService;

    /**
     * 활성 이벤트 목록 조회 (사용자용)
     */
    @GetMapping("/active")
    public ResponseEntity<?> getActiveEvents(@RequestParam Map<String, Object> params,
                                           @RequestHeader("Authorization") String authHeader) {
        try {
            var result = userEventService.getActiveEvents(authHeader);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "이벤트 목록 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 종료된 이벤트 목록 조회 (사용자용)
     */
    @GetMapping("/ended")
    public ResponseEntity<?> getEndedEvents(@RequestParam Map<String, Object> params,
                                          @RequestHeader("Authorization") String authHeader) {
        try {
            var result = userEventService.getEndedEvents(authHeader);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "종료된 이벤트 목록 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 이벤트 상세 정보 조회 (사용자용)
     */
    @GetMapping("/detail/{eventMasterIndex}")
    public ResponseEntity<?> getEventDetail(@PathVariable("eventMasterIndex") Integer eventMasterIndex, 
                                          @RequestParam Map<String, Object> params) {
        try {
            var result = userEventService.getEventDetail(eventMasterIndex);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "이벤트 상세 정보 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 쿠폰 다운로드 (사용자용)
     */
    @PostMapping("/coupon/download")
    public ResponseEntity<?> downloadCoupon(@RequestBody Map<String, Object> request,
                                          @RequestHeader("Authorization") String authHeader) {
        try {
            Integer eventMasterIndex = (Integer) request.get("eventMasterIndex");
            Integer couponIndex = (Integer) request.get("couponIndex");
            
            var result = userEventService.downloadCoupon(eventMasterIndex, couponIndex, authHeader);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "쿠폰 다운로드 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
} 