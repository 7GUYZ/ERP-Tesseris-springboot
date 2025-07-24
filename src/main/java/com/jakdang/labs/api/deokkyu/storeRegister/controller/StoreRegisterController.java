package com.jakdang.labs.api.deokkyu.storeRegister.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.jakdang.labs.api.deokkyu.storeRegister.dto.PaymentCreateRequestDto;
import com.jakdang.labs.api.deokkyu.storeRegister.dto.PaymentConfirmRequestDto;
import com.jakdang.labs.api.deokkyu.storeRegister.dto.StoreRegisterRequestDto;
import com.jakdang.labs.api.deokkyu.storeRegister.service.PaymentService;
import com.jakdang.labs.api.deokkyu.storeRegister.service.StoreRegisterService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class StoreRegisterController {
    
    private final StoreRegisterService storeRegisterService;
    private final PaymentService paymentService;

    /**
     * 가맹점 신청 등록
     * @param formData 가맹점 신청 데이터
     * @return 등록 결과
     */
    @PostMapping("/store/register")
    public ResponseEntity<Map<String, Object>> registerStore(
            @RequestParam("storeData") String storeData,
            @RequestParam(value = "files", required = false) MultipartFile[] files) {
        try {
            log.info("가맹점 신청 등록 요청: {}", storeData);
            Map<String, Object> result = storeRegisterService.registerStore(storeData, files);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("가맹점 신청 등록 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                Map.of(
                    "success", false,
                    "message", "가맹점 신청 등록에 실패했습니다: " + e.getMessage()
                )
            );
        }
    }

    /**
     * 결제 요청 생성 (토스페이먼츠)
     * @param paymentData 결제 정보
     * @return 결제 요청 결과
     */
    @PostMapping("/payment/create")
    public ResponseEntity<Map<String, Object>> createPaymentRequest(@RequestBody PaymentCreateRequestDto paymentData) {
        try {
            log.info("결제 요청 생성: {}", paymentData);
            Map<String, Object> result = paymentService.createPaymentRequest(paymentData);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("결제 요청 생성 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                Map.of(
                    "success", false,
                    "message", "결제 요청 생성에 실패했습니다: " + e.getMessage()
                )
            );
        }
    }

    /**
     * 결제 승인 처리
     * @param confirmData 결제 승인 데이터
     * @return 결제 승인 결과
     */
    @PostMapping("/payment/confirm")
    public ResponseEntity<Map<String, Object>> confirmPayment(@RequestBody PaymentConfirmRequestDto confirmData) {
        try {
            log.info("결제 승인 처리: {}", confirmData);
            Map<String, Object> result = paymentService.confirmPayment(confirmData);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("결제 승인 처리 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                Map.of(
                    "success", false,
                    "message", "결제 승인 처리에 실패했습니다: " + e.getMessage()
                )
            );
        }
    }
} 