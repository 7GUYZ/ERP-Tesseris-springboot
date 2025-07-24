package com.jakdang.labs.api.dabin.CmsCouponManage.controller;

import java.util.List;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jakdang.labs.api.dabin.CmsCouponManage.dto.CouponIssuanceStatusDto;
import com.jakdang.labs.api.dabin.CmsCouponManage.dto.CouponProvidedStatusDto;
import com.jakdang.labs.api.dabin.CmsCouponManage.dto.CouponSearchRequestDto;
import com.jakdang.labs.api.dabin.CmsCouponManage.dto.CouponSearchResponseDto;
import com.jakdang.labs.api.dabin.CmsCouponManage.service.CmsCouponManageService;

import lombok.RequiredArgsConstructor;




@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CmsCouponManageController {
    
    private final CmsCouponManageService couponService;

    @PostMapping("/search")
    public List<CouponSearchResponseDto> searchCoupons(@RequestBody CouponSearchRequestDto dto) {
        
        return couponService.searchCoupons(dto);
    }

    @GetMapping("/status/issuance")
    public List<CouponIssuanceStatusDto> getIssuanceStatusList() {
        return couponService.getIssuanceStatusList();
    }

    @GetMapping("/status/provided")
    public List<CouponProvidedStatusDto> getProvidedStatusList() {
        return couponService.getProvidedStatusList();
    }
} 