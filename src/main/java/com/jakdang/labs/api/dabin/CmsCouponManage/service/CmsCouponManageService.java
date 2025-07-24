package com.jakdang.labs.api.dabin.CmsCouponManage.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jakdang.labs.api.dabin.CmsCouponManage.dto.CouponIssuanceStatusDto;
import com.jakdang.labs.api.dabin.CmsCouponManage.dto.CouponProvidedStatusDto;
import com.jakdang.labs.api.dabin.CmsCouponManage.dto.CouponSearchRequestDto;
import com.jakdang.labs.api.dabin.CmsCouponManage.dto.CouponSearchResponseDto;
import com.jakdang.labs.api.dabin.CmsCouponManage.repository.CouponIssuanceStatusJdbRepo;
import com.jakdang.labs.api.dabin.CmsCouponManage.repository.CouponJdbRepo;
import com.jakdang.labs.api.dabin.CmsCouponManage.repository.CouponProvidedStatusJdbRepo;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CmsCouponManageService {

    private final CouponJdbRepo couponJdbRepo;
    private final CouponIssuanceStatusJdbRepo couponIssuanceStatusJdbRepo;
    private final CouponProvidedStatusJdbRepo couponProvidedStatusJdbRepo;


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