package com.jakdang.labs.api.dabin.CmsSalesPerformance.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;

import com.jakdang.labs.api.dabin.CmsSalesPerformance.dto.BusinessGradeDto;
import com.jakdang.labs.api.dabin.CmsSalesPerformance.dto.SalesPerformanceSearchRequestDto;
import com.jakdang.labs.api.dabin.CmsSalesPerformance.dto.SalesPerformanceSearchResponseDto;
import com.jakdang.labs.api.dabin.CmsSalesPerformance.dto.StoreRequestStatusDto;
import com.jakdang.labs.api.dabin.CmsSalesPerformance.service.CmsSalesPerformanceService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/sales-performance")
@RequiredArgsConstructor
public class CmsSalesPerformanceController {
    
    private final CmsSalesPerformanceService salesPerformanceService;

    @PostMapping("/search")
    public List<SalesPerformanceSearchResponseDto> searchSalesPerformance(@RequestBody SalesPerformanceSearchRequestDto dto) {
        
        return salesPerformanceService.searchSalesPerformance(dto);
    }

    @GetMapping("/grade")
    public List<BusinessGradeDto> getBusinessGradeList() {
        return salesPerformanceService.getBusinessGradeList();
    }

    @GetMapping("/store-request-status")
    public List<StoreRequestStatusDto> getStoreRequestStatusList() {
        return salesPerformanceService.getStoreRequestStatusList();
    }
} 