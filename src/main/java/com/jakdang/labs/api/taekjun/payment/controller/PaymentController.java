package com.jakdang.labs.api.taekjun.payment.controller;

import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.taekjun.payment.dto.*;
import com.jakdang.labs.api.taekjun.payment.service.PaymentInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    
    private final PaymentInfoService paymentService;
    
    /**
     * 결제 정보 조회 (월 한도, 사용량, 보유 CM)
     */
    @GetMapping("/info")
    public ResponseEntity<ResponseDTO<PaymentInfoDTO>> getPaymentInfo(@RequestParam Integer userIndex) {
        log.info("결제 정보 조회 요청 - userIndex: {}", userIndex);
        
        try {
            PaymentInfoDTO paymentInfo = paymentService.getPaymentInfo(userIndex);
            
            ResponseDTO<PaymentInfoDTO> response = ResponseDTO.<PaymentInfoDTO>builder()
                .resultCode(200)
                .resultMessage("결제 정보를 성공적으로 조회했습니다.")
                .data(paymentInfo)
                .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("결제 정보 조회 중 오류 발생", e);
            
            ResponseDTO<PaymentInfoDTO> response = ResponseDTO.<PaymentInfoDTO>builder()
                .resultCode(500)
                .resultMessage("결제 정보 조회 중 오류가 발생했습니다.")
                .build();
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 가맹점 목록 조회
     */
    @GetMapping("/stores")
    public ResponseEntity<ResponseDTO<List<StoreDTO>>> getStoreList() {
        log.info("가맹점 목록 조회 요청");
        
        try {
            List<StoreDTO> storeList = paymentService.getStoreList();
            
            ResponseDTO<List<StoreDTO>> response = ResponseDTO.<List<StoreDTO>>builder()
                .resultCode(200)
                .resultMessage("가맹점 목록을 성공적으로 조회했습니다.")
                .data(storeList)
                .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("가맹점 목록 조회 중 오류 발생", e);
            
            ResponseDTO<List<StoreDTO>> response = ResponseDTO.<List<StoreDTO>>builder()
                .resultCode(500)
                .resultMessage("가맹점 목록 조회 중 오류가 발생했습니다.")
                .build();
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 사용자의 쿠폰 목록 조회
     */
    @GetMapping("/coupons")
    public ResponseEntity<ResponseDTO<List<CouponDTO>>> getUserCoupons(
            @RequestParam Integer userIndex,
            @RequestParam(required = false) String couponName) {
        log.info("사용자 쿠폰 목록 조회 요청 - userIndex: {}, couponName: {}", userIndex, couponName);
        
        try {
            List<CouponDTO> couponList = paymentService.getUserCoupons(userIndex, couponName);
            
            ResponseDTO<List<CouponDTO>> response = ResponseDTO.<List<CouponDTO>>builder()
                .resultCode(200)
                .resultMessage("쿠폰 목록을 성공적으로 조회했습니다.")
                .data(couponList)
                .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("쿠폰 목록 조회 중 오류 발생", e);
            
            ResponseDTO<List<CouponDTO>> response = ResponseDTO.<List<CouponDTO>>builder()
                .resultCode(500)
                .resultMessage("쿠폰 목록 조회 중 오류가 발생했습니다.")
                .build();
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 특정 가맹점의 쿠폰 목록 조회
     */
    @GetMapping("/store-coupons")
    public ResponseEntity<ResponseDTO<List<CouponDTO>>> getStoreCoupons(
            @RequestParam Integer userIndex,
            @RequestParam Integer storeUserIndex,
            @RequestParam(required = false) String couponName) {
        log.info("가맹점 쿠폰 목록 조회 요청 - userIndex: {}, storeUserIndex: {}, couponName: {}", userIndex, storeUserIndex, couponName);
        
        try {
            List<CouponDTO> couponList = paymentService.getStoreCouponsForUser(userIndex, storeUserIndex, couponName);
            
            ResponseDTO<List<CouponDTO>> response = ResponseDTO.<List<CouponDTO>>builder()
                .resultCode(200)
                .resultMessage("가맹점 쿠폰 목록을 성공적으로 조회했습니다.")
                .data(couponList)
                .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("가맹점 쿠폰 목록 조회 중 오류 발생", e);
            
            ResponseDTO<List<CouponDTO>> response = ResponseDTO.<List<CouponDTO>>builder()
                .resultCode(500)
                .resultMessage("가맹점 쿠폰 목록 조회 중 오류가 발생했습니다.")
                .build();
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 결제 실행
     */
    @PostMapping("/process")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> processPayment(
            @RequestBody PaymentRequestDTO request,
            @RequestParam Integer userIndex) {
        log.info("결제 실행 요청 - userIndex: {}, request: {}", userIndex, request);
        
        try {
            boolean success = paymentService.processPayment(request, userIndex);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", success);
            
            ResponseDTO<Map<String, Object>> response = ResponseDTO.<Map<String, Object>>builder()
                .resultCode(success ? 200 : 400)
                .resultMessage(success ? "결제가 성공적으로 완료되었습니다." : "결제에 실패했습니다.")
                .data(result)
                .build();
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            log.error("결제 실행 중 오류 발생: {}", e.getMessage());
            
            ResponseDTO<Map<String, Object>> response = ResponseDTO.<Map<String, Object>>builder()
                .resultCode(400)
                .resultMessage(e.getMessage())
                .build();
            
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            log.error("결제 실행 중 예상치 못한 오류 발생", e);
            
            ResponseDTO<Map<String, Object>> response = ResponseDTO.<Map<String, Object>>builder()
                .resultCode(500)
                .resultMessage("결제 실행 중 오류가 발생했습니다.")
                .build();
            
            return ResponseEntity.badRequest().body(response);
        }
    }
} 