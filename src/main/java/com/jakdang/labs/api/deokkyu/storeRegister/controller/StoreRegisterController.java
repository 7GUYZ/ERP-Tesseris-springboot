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
     * @param storeData 가맹점 신청 데이터 (JSON 문자열)
     * @param storeBusinessLicensePhoto 사업자등록증 사진
     * @param storeSignPhoto 간판 사진
     * @param storeFrontPhoto 외관 사진
     * @return 등록 결과
     */
    @PostMapping("/store/register")
    public ResponseEntity<Map<String, Object>> registerStore(
            @RequestParam("storeData") String storeData,
            @RequestParam(value = "storeBusinessLicensePhoto", required = false) MultipartFile storeBusinessLicensePhoto,
            @RequestParam(value = "storeSignPhoto", required = false) MultipartFile storeSignPhoto,
            @RequestParam(value = "storeFrontPhoto", required = false) MultipartFile storeFrontPhoto) {
        try {
            log.info("=== 가맹점 신청 등록 API 호출됨 ===");
            log.info("storeData: {}", storeData);
            log.info("storeBusinessLicensePhoto: {}", storeBusinessLicensePhoto != null ? storeBusinessLicensePhoto.getOriginalFilename() : "없음");
            log.info("storeSignPhoto: {}", storeSignPhoto != null ? storeSignPhoto.getOriginalFilename() : "없음");
            log.info("storeFrontPhoto: {}", storeFrontPhoto != null ? storeFrontPhoto.getOriginalFilename() : "없음");
            
            Map<String, Object> result = storeRegisterService.registerStore(storeData, storeBusinessLicensePhoto, storeSignPhoto, storeFrontPhoto);
            
            log.info("가맹점 신청 등록 완료: {}", result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("=== 가맹점 신청 등록 실패 ===");
            log.error("에러 메시지: {}", e.getMessage());
            log.error("에러 상세: ", e);
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