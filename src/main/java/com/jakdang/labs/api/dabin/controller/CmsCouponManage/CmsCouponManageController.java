package com.jakdang.labs.api.dabin.controller.CmsCouponManage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jakdang.labs.api.dabin.dto.CmsCouponManage.CouponIssuanceStatusDto;
import com.jakdang.labs.api.dabin.dto.CmsCouponManage.CouponProvidedStatusDto;
import com.jakdang.labs.api.dabin.dto.CmsCouponManage.CouponSearchRequestDto;
import com.jakdang.labs.api.dabin.dto.CmsCouponManage.CouponSearchResponseDto;
import com.jakdang.labs.api.dabin.service.CmsCouponManage.CmsCouponManageService;


@RestController
@RequestMapping("/api/coupons")
public class CmsCouponManageController {
    @Autowired
    private CmsCouponManageService couponService;

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