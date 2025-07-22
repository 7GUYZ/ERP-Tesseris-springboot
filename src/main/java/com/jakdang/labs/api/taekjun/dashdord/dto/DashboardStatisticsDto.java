package com.jakdang.labs.api.taekjun.dashdord.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatisticsDto {
    // CM 관련 통계
    private Long chargedCmTotal;        // 충전된 CM - 전체 총량
    private Long chargedCmYesterday;    // 충전된 CM - 어제 총량
    private Long chargedCmToday;        // 충전된 CM - 오늘 총량

    // 수수료 관련 통계
    private Long businessCashCommissionTotal;    // 사업자 현금 수수료 - 전체 총량
    private Long businessCashCommissionYesterday; // 사업자 현금 수수료 - 어제 총량
    private Long businessCashCommissionToday;     // 사업자 현금 수수료 - 오늘 총량
    
    private Long businessCmCommissionTotal;      // 사업자 CM 수수료 - 전체 총량
    private Long businessCmCommissionYesterday;   // 사업자 CM 수수료 - 어제 총량
    private Long businessCmCommissionToday;       // 사업자 CM 수수료 - 오늘 총량
    
    private Long companyCashCommissionTotal;     // 본사 현금 수수료 - 전체 총량
    private Long companyCashCommissionYesterday;  // 본사 현금 수수료 - 어제 총량
    private Long companyCashCommissionToday;      // 본사 현금 수수료 - 오늘 총량
    
    private Long companyCmCashTotal;             // 본사 CM 현금 - 전체 총량
    private Long companyCmCashYesterday;          // 본사 CM 현금 - 어제 총량
    private Long companyCmCashToday;              // 본사 CM 현금 - 오늘 총량

    // 본사지급 CM
    private Long companyPaidCmTotal;    // 본사지급 CM - 전체 총량
    private Long companyPaidCmYesterday; // 본사지급 CM - 어제 총량
    private Long companyPaidCmToday;     // 본사지급 CM - 오늘 총량
    
    // 본사회수 CM
    private Long companyCollectedCmTotal;    // 본사회수 CM - 전체 총량
    private Long companyCollectedCmYesterday; // 본사회수 CM - 어제 총량
    private Long companyCollectedCmToday;     // 본사회수 CM - 오늘 총량

    // 선물 CM
    private Long giftCmTotal;           // 선물 CM - 전체 총량
    private Long giftCmYesterday;       // 선물 CM - 어제 총량
    private Long giftCmToday;           // 선물 CM - 오늘 총량

    // 출금 신청 완료
    private Long withdrawalCompletedTotal;    // 출금 신청 완료 - 전체 총량
    private Long withdrawalCompletedYesterday; // 출금 신청 완료 - 어제 총량
    private Long withdrawalCompletedToday;     // 출금 신청 완료 - 오늘 총량
    
    // 가맹점 관련 통계
    private Long approvedStoreTotal;    // 승인된 가맹점 - 전체 총량
    private Long approvedStoreYesterday; // 승인된 가맹점 - 어제 총량
    private Long approvedStoreToday;     // 승인된 가맹점 - 오늘 총량
    
    private Long pendingStoreTotal;     // 대기중인 가맹점 - 전체 총량
    
    private Long businessManTotal;      // 사업자 - 전체 총량
    private Long businessManYesterday;   // 사업자 - 어제 총량
    private Long businessManToday;       // 사업자 - 오늘 총량
    
    // 수수료 수익 (Controller에서 사용하는 필드)
    private Long commissionRevenueTotal;    // 수수료 수익 - 전체 총량
    private Long commissionRevenueYesterday; // 수수료 수익 - 어제 총량
    private Long commissionRevenueToday;     // 수수료 수익 - 오늘 총량
} 