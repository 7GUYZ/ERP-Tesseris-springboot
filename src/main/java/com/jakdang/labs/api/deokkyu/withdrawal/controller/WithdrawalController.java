package com.jakdang.labs.api.deokkyu.withdrawal.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jakdang.labs.api.deokkyu.withdrawal.dto.WithdrawalDetailsRequestDto;
import com.jakdang.labs.api.deokkyu.withdrawal.dto.WithdrawalDetailsResponseDto;
import com.jakdang.labs.api.deokkyu.withdrawal.service.WithdrawalService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/withdrawal")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class WithdrawalController {
    
    private final WithdrawalService withdrawalService;
    
    /**
     * 출금 상세 조회 API
     * @param requestDto 시작일, 종료일
     * @return 출금 상세 목록
     */
    @GetMapping("/details")
    public ResponseEntity<Map<String, Object>> getWithdrawalDetails(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        
        try {
            log.info("출금 상세 조회 API 호출: {} ~ {}", startDate, endDate);
            
            WithdrawalDetailsRequestDto requestDto = new WithdrawalDetailsRequestDto(startDate, endDate);
            List<WithdrawalDetailsResponseDto> withdrawalDetails = withdrawalService.getWithdrawalDetails(requestDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", withdrawalDetails);
            
            log.info("출금 상세 조회 완료: {}건", withdrawalDetails.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("출금 상세 조회 실패: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "출금 상세 조회에 실패했습니다: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
} 