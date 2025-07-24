package com.jakdang.labs.api.dabin.CmsCouponManage.repository;

import java.util.List;

import com.jakdang.labs.api.dabin.CmsCouponManage.dto.CouponSearchRequestDto;
import com.jakdang.labs.api.dabin.CmsCouponManage.dto.CouponSearchResponseDto;


public interface CouponJdbRepoCustom {
    List<CouponSearchResponseDto> searchCoupons(CouponSearchRequestDto dto);
} 