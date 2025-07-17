package com.jakdang.labs.api.dabin.repository.CmsCouponManage;

import java.util.List;

import com.jakdang.labs.api.dabin.dto.CmsCouponManage.CouponSearchRequestDto;
import com.jakdang.labs.api.dabin.dto.CmsCouponManage.CouponSearchResponseDto;

public interface CouponJdbRepoCustom {
    List<CouponSearchResponseDto> searchCoupons(CouponSearchRequestDto dto);
} 