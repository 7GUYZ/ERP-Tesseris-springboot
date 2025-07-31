package com.jakdang.labs.api.dabin.FrontEventCouponRegistration.controller;

import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.dabin.FrontEventCouponRegistration.dto.CouponForEventResponse;
import com.jakdang.labs.api.dabin.FrontEventCouponRegistration.dto.EventRegistrationRequest;
import com.jakdang.labs.api.dabin.FrontEventCouponRegistration.service.EventRegistrationService;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.repository.UserTesserisJdbRepo;
import com.jakdang.labs.api.auth.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dabin/event-registration")
@RequiredArgsConstructor
@Slf4j
public class EventRegistrationController {
    
    private final EventRegistrationService eventRegistrationService;
    private final UserTesserisJdbRepo userTesserisRepository;
    
    /**
     * JWT 기반 사용 가능한 쿠폰 목록 조회
     */
    @GetMapping("/coupons")
    public ResponseDTO<List<CouponForEventResponse>> getAvailableCouponsJwt(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(name = "minPrice", required = false) Integer minPrice) {
        String userId = userDetails.getUserId();
        UserTesseris userTesseris = userTesserisRepository.findByUsersId_Id(userId)
            .orElseThrow(() -> new RuntimeException("UserTesseris not found for userId: " + userId));
        Long userIndex = userTesseris.getUserIndex().longValue();
        log.info("JWT 쿠폰 목록 조회: userId={}, userIndex={}, minPrice={}", userId, userIndex, minPrice);
        return eventRegistrationService.getAvailableCoupons(userIndex, minPrice);
    }
    
    /**
     * JWT 기반 이벤트 등록
     */
    @PostMapping("/register")
    public ResponseDTO<String> registerEventJwt(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody EventRegistrationRequest request) {
        String userId = userDetails.getUserId();
        UserTesseris userTesseris = userTesserisRepository.findByUsersId_Id(userId)
            .orElseThrow(() -> new RuntimeException("UserTesseris not found for userId: " + userId));
        Long userIndex = userTesseris.getUserIndex().longValue();
        log.info("JWT 이벤트 등록 요청: userId={}, userIndex={}, eventName={}", userId, userIndex, request.getEventName());
        return eventRegistrationService.registerEvent(request, userIndex);
    }
} 