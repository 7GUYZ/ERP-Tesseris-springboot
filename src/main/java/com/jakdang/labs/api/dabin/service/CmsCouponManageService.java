package com.jakdang.labs.api.dabin.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jakdang.labs.api.dabin.dto.CouponIssuanceStatusDto;
import com.jakdang.labs.api.dabin.dto.CouponProvidedStatusDto;
import com.jakdang.labs.api.dabin.dto.CouponSearchRequestDto;
import com.jakdang.labs.api.dabin.dto.CouponSearchResponseDto;
import com.jakdang.labs.api.dabin.repository.CouponIssuanceStatusJdbRepo;
import com.jakdang.labs.api.dabin.repository.CouponProvidedStatusJdbRepo;
import com.jakdang.labs.api.dabin.repository.CouponJdbRepo;

@Service
public class CmsCouponManageService {
    @Autowired
    private CouponJdbRepo couponJdbRepo;
    @Autowired
    private CouponIssuanceStatusJdbRepo couponIssuanceStatusJdbRepo;
    @Autowired
    private CouponProvidedStatusJdbRepo couponProvidedStatusJdbRepo;


    // 쿠폰 검색 (동적 쿼리/조건 검색은 추후 추가)
    public List<CouponSearchResponseDto> searchCoupons(CouponSearchRequestDto dto) {
        return couponJdbRepo.searchCoupons(dto);
    }

    // 발행상태 리스트
    public List<CouponIssuanceStatusDto> getIssuanceStatusList() {
        return couponIssuanceStatusJdbRepo.findAll().stream().map(status -> {
            CouponIssuanceStatusDto dto = new CouponIssuanceStatusDto();
            dto.setCouponIssuanceStatusIndex(status.getCouponIssuanceStatusIndex());
            dto.setCouponIssuanceStatus(status.getCouponIssuanceStatus());
            return dto;
        }).collect(Collectors.toList());
    }

    // 지급상태 리스트
    public List<CouponProvidedStatusDto> getProvidedStatusList() {
        return couponProvidedStatusJdbRepo.findAll().stream().map(status -> {
            CouponProvidedStatusDto dto = new CouponProvidedStatusDto();
            dto.setCouponProvidedStatusIndex(status.getCouponProvidedStatusIndex());
            dto.setCouponProvidedStatus(status.getCouponProvidedStatus());
            return dto;
        }).collect(Collectors.toList());
    }
} 