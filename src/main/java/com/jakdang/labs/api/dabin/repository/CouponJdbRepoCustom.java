package com.jakdang.labs.api.dabin.repository;

import java.util.List;

import com.jakdang.labs.api.dabin.dto.CouponSearchRequestDto;
import com.jakdang.labs.api.dabin.dto.CouponSearchResponseDto;

public interface CouponJdbRepoCustom {
    List<CouponSearchResponseDto> searchCoupons(CouponSearchRequestDto dto);
} 