package com.jakdang.labs.api.jihun.charge.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jakdang.labs.api.jihun.charge.service.ChageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/charge")
@Slf4j
@RequiredArgsConstructor
public class ChageController {
    private final ChageService chargeService;
    @PostMapping("/confirm/{source}")
    public ResponseEntity<Map<String, Object>> ChargeConfirm(@RequestBody Map<String, Object> data, @PathVariable String source) {
        try {
            chargeService.confirmPayment(data, source);
            return ResponseEntity.status(HttpStatus.SC_OK).body(
                    Map.of(
                            "success", true,
                            "message", "결제 성공"));
        } catch (Exception e) {
            log.error("결제 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(
                    Map.of(
                            "success", false,
                            "message", "결제 실패"));
        }
    }

}
