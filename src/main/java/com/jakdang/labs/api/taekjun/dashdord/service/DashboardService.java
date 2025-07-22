package com.jakdang.labs.api.taekjun.dashdord.service;

import com.jakdang.labs.api.taekjun.dashdord.dto.DashboardStatisticsDto;
import com.jakdang.labs.api.taekjun.dashdord.repository.UserCmLogJtjRepo;
import com.jakdang.labs.api.taekjun.dashdord.repository.UserCmLogTransactionTypeJtjRepo;
import com.jakdang.labs.api.taekjun.dashdord.repository.UserCmLogValueTypeJtjRepo;
import com.jakdang.labs.api.taekjun.dashdord.repository.UserCmLogPaymentJtjRepo;
import com.jakdang.labs.api.taekjun.dashdord.repository.UserCmJtjRepo;
import com.jakdang.labs.api.taekjun.dashdord.repository.StoreJtjRepo;
import com.jakdang.labs.api.taekjun.dashdord.repository.BusinessManJtjRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {
    private final UserCmLogJtjRepo userCmLogJtjRepo;
    private final UserCmLogTransactionTypeJtjRepo userCmLogTransactionTypeJtjRepo;
    private final UserCmLogValueTypeJtjRepo userCmLogValueTypeJtjRepo;
    private final UserCmLogPaymentJtjRepo userCmLogPaymentJtjRepo;
    private final UserCmJtjRepo userCmJtjRepo;
    private final StoreJtjRepo storeJtjRepo;
    private final BusinessManJtjRepo businessManJtjRepo;
    // StoreJtjRepo, BusinessManJtjRepo 등도 필요시 추가

    /**
     * 대시보드 전체 통계 조회
     */
    @Transactional(readOnly = true)
    public DashboardStatisticsDto getDashboardStatistics() {
        log.info("대시보드 통계 조회 시작");
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        String todayStr = today.toString();
        String yesterdayStr = yesterday.toString();
        try {
            DashboardStatisticsDto statistics = DashboardStatisticsDto.builder()
                    // CM 관련 통계
                    .chargedCmTotal(getChargedCmTotal())
                    .chargedCmYesterday(getChargedCmByDate(yesterdayStr))
                    .chargedCmToday(getChargedCmByDate(todayStr))
                    // 중개수수료 관련 통계
                    .businessCashCommissionTotal(getBusinessCashCommissionTotal())
                    .businessCashCommissionYesterday(getBusinessCashCommissionByDate(yesterdayStr))
                    .businessCashCommissionToday(getBusinessCashCommissionByDate(todayStr))
                    .businessCmCommissionTotal(getBusinessCmCommissionTotal())
                    .businessCmCommissionYesterday(getBusinessCmCommissionByDate(yesterdayStr))
                    .businessCmCommissionToday(getBusinessCmCommissionByDate(todayStr))
                    .companyCashCommissionTotal(getCompanyCashCommissionTotal())
                    .companyCashCommissionYesterday(getCompanyCashCommissionByDate(yesterdayStr))
                    .companyCashCommissionToday(getCompanyCashCommissionByDate(todayStr))
                    .companyCmCashTotal(getCompanyCmCashTotal())
                    .companyCmCashYesterday(getCompanyCmCashByDate(yesterdayStr))
                    .companyCmCashToday(getCompanyCmCashByDate(todayStr))
                    // 본사지급 CM
                    .companyPaidCmTotal(getCompanyPaidCmTotal())
                    .companyPaidCmYesterday(getCompanyPaidCmByDate(yesterdayStr))
                    .companyPaidCmToday(getCompanyPaidCmByDate(todayStr))
                    // 본사회수 CM
                    .companyCollectedCmTotal(getCompanyCollectedCmTotal())
                    .companyCollectedCmYesterday(getCompanyCollectedCmByDate(yesterdayStr))
                    .companyCollectedCmToday(getCompanyCollectedCmByDate(todayStr))
                    // 선물 CM
                    .giftCmTotal(getGiftCmTotal())
                    .giftCmYesterday(getGiftCmByDate(yesterdayStr))
                    .giftCmToday(getGiftCmByDate(todayStr))
                    // 출금 신청 완료
                    .withdrawalCompletedTotal(getWithdrawalCompletedTotal())
                    .withdrawalCompletedYesterday(getWithdrawalCompletedByDate(yesterdayStr))
                    .withdrawalCompletedToday(getWithdrawalCompletedByDate(todayStr))
                    // 가맹점 관련 통계
                    .approvedStoreTotal(getApprovedStoreTotal())
                    .approvedStoreYesterday(getApprovedStoreByDate(yesterdayStr))
                    .approvedStoreToday(getApprovedStoreByDate(todayStr))
                    .pendingStoreTotal(getPendingStoreTotal())
                    // 사업자 관련 통계
                    .businessManTotal(getBusinessManTotal())
                    .businessManYesterday(getBusinessManByDate(yesterdayStr))
                    .businessManToday(getBusinessManByDate(todayStr))
                    // 수수료 수익 (임시로 0으로 설정, 추후 계산 로직 추가 필요)
                    .commissionRevenueTotal(0L)
                    .commissionRevenueYesterday(0L)
                    .commissionRevenueToday(0L)
                    .build();
            log.info("대시보드 통계 조회 완료");
            return statistics;
        } catch (Exception e) {
            log.error("대시보드 통계 조회 중 오류 발생", e);
            throw new RuntimeException("대시보드 통계 조회 중 오류가 발생했습니다.", e);
        }
    }

    // CM 관련 메서드들 (UserCmLogJtjRepo에 쿼리 있다고 가정)
    private Long getChargedCmTotal() {
        return userCmLogJtjRepo.getChargedCmTotal();
    }
    private Long getChargedCmByDate(String date) {
        return userCmLogJtjRepo.getChargedCmByDate(date);
    }
    // 중개수수료 관련 메서드
    private Long getBusinessCashCommissionTotal() {
        return userCmLogJtjRepo.getBusinessCashCommissionTotal();
    }
    private Long getBusinessCashCommissionByDate(String date) {
        return userCmLogJtjRepo.getBusinessCashCommissionByDate(date);
    }
    private Long getBusinessCmCommissionTotal() {
        return userCmLogJtjRepo.getBusinessCmCommissionTotal();
    }
    private Long getBusinessCmCommissionByDate(String date) {
        return userCmLogJtjRepo.getBusinessCmCommissionByDate(date);
    }
    private Long getCompanyCashCommissionTotal() {
        return userCmLogJtjRepo.getCompanyCashCommissionTotal();
    }
    private Long getCompanyCashCommissionByDate(String date) {
        return userCmLogJtjRepo.getCompanyCashCommissionByDate(date);
    }
    private Long getCompanyCmCashTotal() {
        return userCmLogJtjRepo.getCompanyCmCashTotal();
    }
    private Long getCompanyCmCashByDate(String date) {
        return userCmLogJtjRepo.getCompanyCmCashByDate(date);
    }
    // 본사지급 CM
    private Long getCompanyPaidCmTotal() {
        return userCmLogJtjRepo.getCompanyPaidCmTotal();
    }
    private Long getCompanyPaidCmByDate(String date) {
        return userCmLogJtjRepo.getCompanyPaidCmByDate(date);
    }
    // 본사회수 CM
    private Long getCompanyCollectedCmTotal() {
        return userCmLogJtjRepo.getCompanyCollectedCmTotal();
    }
    private Long getCompanyCollectedCmByDate(String date) {
        return userCmLogJtjRepo.getCompanyCollectedCmByDate(date);
    }
    // 선물 CM
    private Long getGiftCmTotal() {
        return userCmLogJtjRepo.getGiftCmTotal();
    }
    private Long getGiftCmByDate(String date) {
        return userCmLogJtjRepo.getGiftCmByDate(date);
    }
    // 출금 신청 완료
    private Long getWithdrawalCompletedTotal() {
        return userCmLogJtjRepo.getWithdrawalCompletedTotal();
    }
    private Long getWithdrawalCompletedByDate(String date) {
        return userCmLogJtjRepo.getWithdrawalCompletedByDate(date);
    }

    // 가맹점 관련 메서드
    private Long getApprovedStoreTotal() {
        return storeJtjRepo.countApprovedStoreTotal();
    }
    private Long getApprovedStoreByDate(String date) {
        return storeJtjRepo.countApprovedStoreByDate(date);
    }
    private Long getPendingStoreTotal() {
        return storeJtjRepo.countPendingStoreTotal();
    }
    // 사업자 관련 메서드
    private Long getBusinessManTotal() {
        return businessManJtjRepo.countBusinessManTotal();
    }
    private Long getBusinessManByDate(String date) {
        return businessManJtjRepo.countBusinessManByDate(date);
    }
} 