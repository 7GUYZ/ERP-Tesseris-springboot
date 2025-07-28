package com.jakdang.labs.api.deokkyu.storeRegister.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jakdang.labs.api.deokkyu.storeRegister.dto.PaymentCreateRequestDto;
import com.jakdang.labs.api.deokkyu.storeRegister.dto.PaymentConfirmRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
    // 토스페이먼츠 설정 (하드코딩)
    private final String TOSS_SECRET_KEY = "test_sk_Z61JOxRQVENyokBAqGmRrW0X9bAq";
    private final String TOSS_CLIENT_KEY = "test_ck_KNbdOvk5rkmna9Q6ZzJ23n07xlzm";
    private final String TOSS_BASE_URL = "https://api.tosspayments.com";

    /**
     * 결제 요청 생성
     * @param paymentData 결제 정보
     * @return 결제 요청 결과
     */
    public Map<String, Object> createPaymentRequest(PaymentCreateRequestDto paymentData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("결제 요청 생성 - 데이터: {}", paymentData);
            
            // 결제 요청에 필요한 기본 정보 설정
            response.put("success", true);
            response.put("message", "결제 요청이 생성되었습니다");
            response.put("orderId", paymentData.getOrderId());
            response.put("amount", paymentData.getAmount());
            response.put("orderName", paymentData.getOrderName());
            response.put("clientKey", TOSS_CLIENT_KEY); // 클라이언트 키 반환
            
            return response;
            
        } catch (Exception e) {
            log.error("결제 요청 생성 실패: {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }
    }

    /**
     * 결제 승인 처리 (토스페이먼츠)
     * @param confirmData 결제 승인 데이터
     * @return 결제 승인 결과
     */
    @Transactional
    public Map<String, Object> confirmPayment(PaymentConfirmRequestDto confirmData) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("결제 승인 처리 - 데이터: {}", confirmData);
            log.info("토스페이먼츠 시크릿 키: {}", TOSS_SECRET_KEY);
            log.info("토스페이먼츠 클라이언트 키: {}", TOSS_CLIENT_KEY);
            log.info("토스페이먼츠 API URL: {}", TOSS_BASE_URL + "/v1/payments/confirm");
            
            // Basic 인증 헤더 생성 (시크릿 키만 사용)
            String authHeader = "Basic " + Base64.getEncoder().encodeToString((TOSS_SECRET_KEY + ":").getBytes());
            log.info("인증 헤더: {}", authHeader);
            
            // 요청 본문 생성
            String requestBody = String.format("{\"paymentKey\":\"%s\",\"orderId\":\"%s\",\"amount\":%s}",
                    confirmData.getPaymentKey(),
                    confirmData.getOrderId(),
                    confirmData.getAmount());
            log.info("요청 본문: {}", requestBody);
            
            // 토스페이먼츠 API 호출
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TOSS_BASE_URL + "/v1/payments/confirm"))
                    .header("Authorization", authHeader)
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            
            HttpResponse<String> result = HttpClient.newHttpClient().send(request,
                    HttpResponse.BodyHandlers.ofString());
            
            log.info("토스페이먼츠 응답 상태코드: {}", result.statusCode());
            log.info("토스페이먼츠 응답: {}", result.body());
            
            if (result.statusCode() == 200) {
                response.put("success", true);
                response.put("message", "결제가 성공적으로 승인되었습니다");
                response.put("result", result.body());
            } else {
                response.put("success", false);
                response.put("message", "결제 승인에 실패했습니다. 상태코드: " + result.statusCode());
                response.put("error", result.body());
                log.error("토스페이먼츠 API 오류 - 상태코드: {}, 응답: {}", result.statusCode(), result.body());
            }
            
            return response;
            
        } catch (Exception e) {
            log.error("결제 승인 처리 실패: {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }
    }
} 