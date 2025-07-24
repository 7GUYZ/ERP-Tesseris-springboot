package com.jakdang.labs.api.dabin.CmsCommissionManage.controller;



import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.dabin.CmsCommissionManage.dto.CommissionPaymentRequest;
import com.jakdang.labs.api.dabin.CmsCommissionManage.dto.CommissionPaymentResponse;
import com.jakdang.labs.api.dabin.CmsCommissionManage.dto.CommissionPaymentUpdateRequest;
import com.jakdang.labs.api.dabin.CmsCommissionManage.service.CommissionPaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dabin/commission-payment")
@RequiredArgsConstructor
@Slf4j
public class CommissionPaymentController {
    
    private final CommissionPaymentService commissionPaymentService;
    
    @PostMapping("/search")
    public ResponseDTO<List<CommissionPaymentResponse>> searchCommissionPayments(@RequestBody CommissionPaymentRequest request) {
        log.info("수당 지급 내역 조회 요청: {}", request);
        return commissionPaymentService.searchCommissionPayments(request);
    }
    
    @PostMapping("/update-status")
    public ResponseDTO<String> updatePaymentStatus(@RequestBody CommissionPaymentUpdateRequest request) {
        log.info("수당 지급 상태 업데이트 요청: {}", request);
        return commissionPaymentService.updatePaymentStatus(request);
    }
    
    @GetMapping("/validate/{detailIndex}")
    public ResponseDTO<Boolean> validatePaymentEligibility(@PathVariable Integer detailIndex) {
        log.info("수당 지급 자격 검증 요청: detailIndex={}", detailIndex);
        return commissionPaymentService.validatePaymentEligibility(detailIndex);
    }
} 