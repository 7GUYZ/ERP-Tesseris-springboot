package com.jakdang.labs.api.dabin.FrontEventCouponRegistration.controller;

import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.dabin.FrontEventCouponRegistration.dto.CouponForEventResponse;
import com.jakdang.labs.api.dabin.FrontEventCouponRegistration.dto.EventRegistrationRequest;
import com.jakdang.labs.api.dabin.FrontEventCouponRegistration.service.EventRegistrationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dabin/event-registration")
@RequiredArgsConstructor
@Slf4j
public class EventRegistrationController {
    
    private final EventRegistrationService eventRegistrationService;
    
    /**
     * 사용 가능한 쿠폰 목록 조회
     */
    @GetMapping("/coupons")
    public ResponseDTO<List<CouponForEventResponse>> getAvailableCoupons(
            @RequestParam(name = "userIndex") Long userIndex,
            @RequestParam(name = "minPrice", required = false) Integer minPrice) {
        log.info("사용 가능한 쿠폰 목록 조회 요청: userIndex={}, minPrice={}", userIndex, minPrice);
        return eventRegistrationService.getAvailableCoupons(userIndex, minPrice);
    }
    
    /**
     * 이벤트 등록
     */
    @PostMapping("/register")
    public ResponseDTO<String> registerEvent(
            @RequestBody EventRegistrationRequest request,
            @RequestParam(name = "userIndex") Long userIndex) {
        log.info("이벤트 등록 요청: userIndex={}, eventName={}", userIndex, request.getEventName());
        return eventRegistrationService.registerEvent(request, userIndex);
    }
} 