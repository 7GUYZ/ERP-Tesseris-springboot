package com.jakdang.labs.api.jihun.charge.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.jakdang.labs.api.jihun.charge.repository.AjgTemporaryRegularDetail;
import com.jakdang.labs.api.jihun.charge.repository.AjgTemporaryRegularMaster;

import com.jakdang.labs.api.jihun.charge.repository.AjgTemporaryRegularDetail;
import com.jakdang.labs.api.jihun.charge.repository.AjgTemporaryRegularMaster;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChageService {
    private final String TOSS_SECRET_KET = "test_sk_Gv6LjeKD8aEWZO419N7k8wYxAdXy";
    private final AjgTemporaryRegularMaster ajgTemporaryRegularMaster;
    private final AjgTemporaryRegularDetail ajgTemporaryRegularDetail;
    private final HttpServletRequest request;

    @Transactional
    public ResponseEntity<Map<String, Object>> confirmPayment(Map<String, Object> data) {
        String requestURI = ((HttpServletRequest) request).getRequestURI();
        String referer = request.getHeader("Referer");
        Map<String, Object> response = new HashMap<>();
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
            log.info("다음값  {}", referer);
            log.info("여기서requestURI : {}", requestURI);
            log.info("결과값 : {}", result.body());
            // DB처리하는곳
            switch (getPathType(requestURI)) {
                case "charge":
                    chargeProcess(data);
                    break;
                case "mypage":
                    mypageProcess(data);
                    break;
                default:
                    break;
            }
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("result", result.body()));
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    private String getPathType(String requestURI) {
        if (requestURI.contains("/charge")) {
            return "charge";
        } else if (requestURI.contains("/mypage")) {
            return "mypage";
        }
        return null;
    }

    private void chargeProcess(Map<String, Object> data) {
        log.info("결제 처리 시작: {}", data);
    }

    private void mypageProcess(Map<String, Object> data) {
        log.info("마이페이지 처리 시작: {}", data);
    }
}
