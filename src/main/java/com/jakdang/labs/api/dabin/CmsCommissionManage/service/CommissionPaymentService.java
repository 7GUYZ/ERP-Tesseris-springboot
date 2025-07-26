package com.jakdang.labs.api.dabin.CmsCommissionManage.service;


import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.dabin.CmsCommissionManage.dto.CommissionPaymentRequest;
import com.jakdang.labs.api.dabin.CmsCommissionManage.dto.CommissionPaymentResponse;
import com.jakdang.labs.api.dabin.CmsCommissionManage.repository.CommissionPaymentJdbRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommissionPaymentService {
    
    private final CommissionPaymentJdbRepo commissionPaymentRepository;
    
    public ResponseDTO<List<CommissionPaymentResponse>> searchCommissionPayments(CommissionPaymentRequest request) {
        
        try {
            // 날짜 파라미터 처리
            LocalDateTime chargeTimeStart = null;
            LocalDateTime chargeTimeEnd = null;
            
            if (request.getChargeTimeStart() != null && !request.getChargeTimeStart().isEmpty()) {
                chargeTimeStart = LocalDateTime.parse(request.getChargeTimeStart() + " 00:00:00", 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            if (request.getChargeTimeEnd() != null && !request.getChargeTimeEnd().isEmpty()) {
                chargeTimeEnd = LocalDateTime.parse(request.getChargeTimeEnd() + " 23:59:59", 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            
            List<CommissionPaymentResponse> results = commissionPaymentRepository.searchCommissionPayments(
                request.getUserId(),
                request.getUserName(),
                request.getUserPhone(),
                chargeTimeStart,
                chargeTimeEnd,
                request.getTransactionName(),
                request.getSuggestionUserId(),
                request.getSuggestionUserName(),
                request.getUserRoleIndex(),
                request.getPaymentStatus(),
                request.getDescription()
            );
            
            return ResponseDTO.<List<CommissionPaymentResponse>>builder()
                    .resultCode(200)
                    .data(results)
                    .resultMessage("수당 지급 내역 조회 성공")
                    .build();
                    
        } catch (Exception e) {
            log.error("수당 지급 내역 조회 중 오류 발생", e);
            return ResponseDTO.<List<CommissionPaymentResponse>>builder()
                    .resultCode(500)
                    .resultMessage("수당 지급 내역 조회 중 오류가 발생했습니다.")
                    .build();
        }
    }
} 