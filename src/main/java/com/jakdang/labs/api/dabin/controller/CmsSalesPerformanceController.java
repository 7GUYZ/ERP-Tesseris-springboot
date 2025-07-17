package com.jakdang.labs.api.dabin.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.jakdang.labs.api.dabin.dto.BusinessGradeDto;
import com.jakdang.labs.api.dabin.dto.SalesPerformanceSearchRequestDto;
import com.jakdang.labs.api.dabin.dto.SalesPerformanceSearchResponseDto;
import com.jakdang.labs.api.dabin.dto.StoreRequestStatusDto;
import com.jakdang.labs.api.dabin.service.CmsSalesPerformanceService;

@RestController
@RequestMapping("/api/sales-performance")
public class CmsSalesPerformanceController {
    @Autowired
    private CmsSalesPerformanceService salesPerformanceService;

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