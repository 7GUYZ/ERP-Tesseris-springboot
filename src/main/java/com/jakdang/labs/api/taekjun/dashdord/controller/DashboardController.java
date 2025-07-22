package com.jakdang.labs.api.taekjun.dashdord.controller;

import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.taekjun.dashdord.dto.DashboardStatisticsDto;
import com.jakdang.labs.api.taekjun.dashdord.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    /**
     * 대시보드 전체 통계 조회
     */
    @GetMapping("/statistics")
    public ResponseEntity<ResponseDTO<?>> getDashboardStatistics() {
        log.info("대시보드 통계 조회 API 호출");
        
        try {
            DashboardStatisticsDto statistics = dashboardService.getDashboardStatistics();
            
            log.info("대시보드 통계 조회 성공");
            return ResponseEntity.ok(ResponseDTO.<DashboardStatisticsDto>builder()
                    .resultCode(200)
                    .resultMessage("대시보드 통계 조회 성공")
                    .data(statistics)
                    .build());
                    
        } catch (Exception e) {
            log.error("대시보드 통계 조회 실패", e);
            return ResponseEntity.ok(ResponseDTO.<DashboardStatisticsDto>createErrorResponse(500, "대시보드 통계 조회 실패: " + e.getMessage()));
        }
    }
    
    /**
     * CM 관련 통계 조회
     */
    @GetMapping("/cm-statistics")
    public ResponseEntity<ResponseDTO<?>> getCmStatistics() {
        log.info("CM 통계 조회 API 호출");
        
        try {
            DashboardStatisticsDto statistics = dashboardService.getDashboardStatistics();
            
            // CM 관련 데이터만 추출
            DashboardStatisticsDto cmStatistics = DashboardStatisticsDto.builder()
                    .chargedCmTotal(statistics.getChargedCmTotal())
                    .chargedCmYesterday(statistics.getChargedCmYesterday())
                    .chargedCmToday(statistics.getChargedCmToday())
                    .commissionRevenueTotal(statistics.getCommissionRevenueTotal())
                    .commissionRevenueYesterday(statistics.getCommissionRevenueYesterday())
                    .commissionRevenueToday(statistics.getCommissionRevenueToday())
                    .companyPaidCmTotal(statistics.getCompanyPaidCmTotal())
                    .companyPaidCmYesterday(statistics.getCompanyPaidCmYesterday())
                    .companyPaidCmToday(statistics.getCompanyPaidCmToday())
                    .companyCollectedCmTotal(statistics.getCompanyCollectedCmTotal())
                    .companyCollectedCmYesterday(statistics.getCompanyCollectedCmYesterday())
                    .companyCollectedCmToday(statistics.getCompanyCollectedCmToday())
                    .build();
            
            log.info("CM 통계 조회 성공");
            return ResponseEntity.ok(ResponseDTO.<DashboardStatisticsDto>builder()
                    .resultCode(200)
                    .resultMessage("CM 통계 조회 성공")
                    .data(cmStatistics)
                    .build());
                    
        } catch (Exception e) {
            log.error("CM 통계 조회 실패", e);
            return ResponseEntity.ok(ResponseDTO.<DashboardStatisticsDto>createErrorResponse(500, "CM 통계 조회 실패: " + e.getMessage()));
        }
    }
    
    /**
     * 수수료 관련 통계 조회
     */
    @GetMapping("/commission-statistics")
    public ResponseEntity<ResponseDTO<?>> getCommissionStatistics() {
        log.info("수수료 통계 조회 API 호출");
        
        try {
            DashboardStatisticsDto statistics = dashboardService.getDashboardStatistics();
            
            // 수수료 관련 데이터만 추출
            DashboardStatisticsDto commissionStatistics = DashboardStatisticsDto.builder()
                    .businessCashCommissionTotal(statistics.getBusinessCashCommissionTotal())
                    .businessCashCommissionYesterday(statistics.getBusinessCashCommissionYesterday())
                    .businessCashCommissionToday(statistics.getBusinessCashCommissionToday())
                    .businessCmCommissionTotal(statistics.getBusinessCmCommissionTotal())
                    .businessCmCommissionYesterday(statistics.getBusinessCmCommissionYesterday())
                    .businessCmCommissionToday(statistics.getBusinessCmCommissionToday())
                    .companyCashCommissionTotal(statistics.getCompanyCashCommissionTotal())
                    .companyCashCommissionYesterday(statistics.getCompanyCashCommissionYesterday())
                    .companyCashCommissionToday(statistics.getCompanyCashCommissionToday())
                    .companyCmCashTotal(statistics.getCompanyCmCashTotal())
                    .companyCmCashYesterday(statistics.getCompanyCmCashYesterday())
                    .companyCmCashToday(statistics.getCompanyCmCashToday())
                    .build();
            
            log.info("수수료 통계 조회 성공");
            return ResponseEntity.ok(ResponseDTO.<DashboardStatisticsDto>builder()
                    .resultCode(200)
                    .resultMessage("수수료 통계 조회 성공")
                    .data(commissionStatistics)
                    .build());
                    
        } catch (Exception e) {
            log.error("수수료 통계 조회 실패", e);
            return ResponseEntity.ok(ResponseDTO.<DashboardStatisticsDto>createErrorResponse(500, "수수료 통계 조회 실패: " + e.getMessage()));
        }
    }
    
    /**
     * 가맹점 관련 통계 조회
     */
    @GetMapping("/store-statistics")
    public ResponseEntity<ResponseDTO<?>> getStoreStatistics() {
        log.info("가맹점 통계 조회 API 호출");
        
        try {
            DashboardStatisticsDto statistics = dashboardService.getDashboardStatistics();
            
            // 가맹점 관련 데이터만 추출
            DashboardStatisticsDto storeStatistics = DashboardStatisticsDto.builder()
                    .approvedStoreTotal(statistics.getApprovedStoreTotal())
                    .approvedStoreYesterday(statistics.getApprovedStoreYesterday())
                    .approvedStoreToday(statistics.getApprovedStoreToday())
                    .pendingStoreTotal(statistics.getPendingStoreTotal())
                    .businessManTotal(statistics.getBusinessManTotal())
                    .businessManYesterday(statistics.getBusinessManYesterday())
                    .businessManToday(statistics.getBusinessManToday())
                    .build();
            
            log.info("가맹점 통계 조회 성공");
            return ResponseEntity.ok(ResponseDTO.<DashboardStatisticsDto>builder()
                    .resultCode(200)
                    .resultMessage("가맹점 통계 조회 성공")
                    .data(storeStatistics)
                    .build());
                    
        } catch (Exception e) {
            log.error("가맹점 통계 조회 실패", e);
            return ResponseEntity.ok(ResponseDTO.<DashboardStatisticsDto>createErrorResponse(500, "가맹점 통계 조회 실패: " + e.getMessage()));
        }
    }
} 