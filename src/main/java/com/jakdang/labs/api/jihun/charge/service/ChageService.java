package com.jakdang.labs.api.jihun.charge.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.jakdang.labs.api.jihun.charge.repository.AjgTemporaryRegularDetail;
import com.jakdang.labs.api.jihun.charge.repository.AjgTemporaryRegularMaster;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChageService {
    private final String TOSS_SECRET_KET = "test_sk_Gv6LjeKD8aEWZO419N7k8wYxAdXy";
    private final AjgTemporaryRegularMaster ajgTemporaryRegularMaster;
    private final AjgTemporaryRegularDetail ajgTemporaryRegularDetail;

    @Transactional
    public ResponseEntity<Map<String, Object>> confirmPayment(Map<String, Object> data, String source) {
        Map<String, Object> response = new HashMap<>();
        log.info("source : {}", source);
        try {
            log.info("결제 요청 데이터: {}", data);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
                    .header("Authorization",
                            "Basic " + Base64.getEncoder().encodeToString((TOSS_SECRET_KET + ":").getBytes()))
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString(
                            "{\"paymentKey\":\"" + data.get("paymentKey") + "\",\"orderId\":\"" + data.get("orderId")
                                    + "\",\"amount\":" + data.get("amount") + "}"))
                    .build();
            HttpResponse<String> result = HttpClient.newHttpClient().send(request,
                    HttpResponse.BodyHandlers.ofString());
            log.info("결과값 : {}", result.body());
            if (result.body().contains("DONE")) {
                // DB처리하는곳
                switch (source) {
                    case "charge":
                        chargeProcess(data);
                        break;
                    case "take":
                        mypageProcess(data);
                        break;
                    default: break;
                }
                return ResponseEntity.status(HttpStatus.OK).body(Map.of("result", result.body()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("result", result.body()));
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    private void chargeProcess(Map<String, Object> data) {
        log.info("결제 처리 시작: {}", data);
    }

    private void mypageProcess(Map<String, Object> data) {
        log.info("마이페이지 처리 시작: {}", data);
    }
}
