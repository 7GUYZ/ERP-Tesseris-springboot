package com.jakdang.labs.api.dabin.CmsSalesPerformance.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jakdang.labs.api.dabin.CmsSalesPerformance.dto.BusinessGradeDto;
import com.jakdang.labs.api.dabin.CmsSalesPerformance.dto.SalesPerformanceSearchRequestDto;
import com.jakdang.labs.api.dabin.CmsSalesPerformance.dto.SalesPerformanceSearchResponseDto;
import com.jakdang.labs.api.dabin.CmsSalesPerformance.dto.StoreRequestStatusDto;
import com.jakdang.labs.api.dabin.CmsSalesPerformance.repository.BusinessGradeJdbRepo;
import com.jakdang.labs.api.dabin.CmsSalesPerformance.repository.SalesPerformanceJdbRepo;
import com.jakdang.labs.api.dabin.CmsSalesPerformance.repository.StoreRequestStatusJdbRepo;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CmsSalesPerformanceService {
  
    @Qualifier("salesPerformanceRepository")
    private final SalesPerformanceJdbRepo salesPerformanceRepository;
   
    private final BusinessGradeJdbRepo businessGradeRepository;
 
    @Qualifier("salesStoreRequestStatusRepository")
    private final StoreRequestStatusJdbRepo storeRequestStatusRepository;

    // 영업실적 검색
    public List<SalesPerformanceSearchResponseDto> searchSalesPerformance(SalesPerformanceSearchRequestDto dto) {
        // Integer → Boolean 변환 (1: true, 0: false, null: null)
        Boolean businessManDistributionFlag = convertToBoolean(dto.getBusinessManDistributionFlag());
        Boolean storeTransactionStatus = convertToBoolean(dto.getStoreTransactionStatus());
        
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

    /**
     * Integer를 Boolean으로 변환하는 헬퍼 메서드
     * 1 → true, 0 → false, null → null
     */
    private Boolean convertToBoolean(Integer value) {
        if (value == null) return null;
        return value == 1;
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