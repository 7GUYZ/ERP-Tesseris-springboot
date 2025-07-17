package com.jakdang.labs.api.dabin.service.CmsSalesPerformanceController;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jakdang.labs.api.dabin.dto.CmsSalesPerformanceController.BusinessGradeDto;
import com.jakdang.labs.api.dabin.dto.CmsSalesPerformanceController.SalesPerformanceSearchRequestDto;
import com.jakdang.labs.api.dabin.dto.CmsSalesPerformanceController.SalesPerformanceSearchResponseDto;
import com.jakdang.labs.api.dabin.dto.CmsSalesPerformanceController.StoreRequestStatusDto;
import com.jakdang.labs.api.dabin.repository.CmsSalesPerformance.BusinessGradeJdbRepo;
import com.jakdang.labs.api.dabin.repository.CmsSalesPerformance.SalesPerformanceJdbRepo;
import com.jakdang.labs.api.dabin.repository.CmsSalesPerformance.StoreRequestStatusJdbRepo;

@Service
public class CmsSalesPerformanceService {
    @Autowired
    @Qualifier("salesPerformanceRepository")
    private SalesPerformanceJdbRepo salesPerformanceRepository;
    @Autowired
    private BusinessGradeJdbRepo businessGradeRepository;
    @Autowired
    @Qualifier("salesStoreRequestStatusRepository")
    private StoreRequestStatusJdbRepo storeRequestStatusRepository;

    // 영업실적 검색
    public List<SalesPerformanceSearchResponseDto> searchSalesPerformance(SalesPerformanceSearchRequestDto dto) {
        Boolean businessManDistributionFlag = null;
        if (dto.getBusinessManDistributionFlag() != null) {
            businessManDistributionFlag = dto.getBusinessManDistributionFlag() == 1;
        }
        Boolean storeTransactionStatus = null;
        if (dto.getStoreTransactionStatus() != null) {
            storeTransactionStatus = dto.getStoreTransactionStatus() == 1;
        }
        // 빈 문자열 파라미터를 null로 변환
        if (dto.getBusinessUserId() != null && dto.getBusinessUserId().isBlank()) {
            dto.setBusinessUserId(null);
        }
        if (dto.getUserName() != null && dto.getUserName().isBlank()) {
            dto.setUserName(null);
        }
        if (dto.getStoreUserId() != null && dto.getStoreUserId().isBlank()) {
            dto.setStoreUserId(null);
        }
        if (dto.getStoreName() != null && dto.getStoreName().isBlank()) {
            dto.setStoreName(null);
        }
        return salesPerformanceRepository.searchSalesPerformance(
            dto.getBusinessUserId() != null ? dto.getBusinessUserId() : "",
            dto.getBusinessGradeIndex() != null ? dto.getBusinessGradeIndex() : 0,
            dto.getUserName() != null ? dto.getUserName() : "",
            businessManDistributionFlag,
            dto.getStoreUserId() != null ? dto.getStoreUserId() : "",
            dto.getStoreName() != null ? dto.getStoreName() : "",
            dto.getStoreRequestStatusIndex() != null ? dto.getStoreRequestStatusIndex() : 0,
            storeTransactionStatus
        );
    }

    // 사업자 등급 리스트
    public List<BusinessGradeDto> getBusinessGradeList() {
        return businessGradeRepository.findAll().stream()
            .filter(grade -> grade.getBusinessGradeLevel() > 1)
            .map(grade -> {
                BusinessGradeDto dto = new BusinessGradeDto();
                dto.setBusinessGradeIndex(grade.getBusinessGradeIndex());
                dto.setBusinessGradeName(grade.getBusinessGradeName());
                return dto;
            }).collect(Collectors.toList());
    }

    // 승인 상태 리스트
    public List<StoreRequestStatusDto> getStoreRequestStatusList() {
        return storeRequestStatusRepository.findAll().stream().map(status -> {
            StoreRequestStatusDto dto = new StoreRequestStatusDto();
            dto.setStoreRequestStatusIndex(status.getStoreRequestStatusIndex());
            dto.setStoreRequestStatusName(status.getStoreRequestStatusName());
            return dto;
        }).collect(Collectors.toList());
    }
} 