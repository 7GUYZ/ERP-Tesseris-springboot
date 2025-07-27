package com.jakdang.labs.api.taekjun.coponlist.service;

import com.jakdang.labs.api.taekjun.coponlist.repository.CouponListJtjRepo;
import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.repository.StoreInfoJdbRepo;
import com.jakdang.labs.entity.Coupon;
import com.jakdang.labs.entity.Store;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponListService {
    
    private final CouponListJtjRepo couponListJtjRepo;
    private final StoreInfoJdbRepo storeInfoJdbRepo;
    
    /**
     * 내가 받은 쿠폰 리스트 조회
     */
    @Transactional
    public List<Map<String, Object>> getMyCoupons(String userIndex) {
        log.info("내 쿠폰 리스트 조회 - userIndex: {}", userIndex);
        
        Integer userIndexInt = Integer.parseInt(userIndex);
        
        // 1. 만료된 쿠폰 상태를 5번으로 변경
        updateExpiredCoupons(userIndexInt);
        
        // 2. 내 쿠폰 리스트 조회
        List<Coupon> coupons = couponListJtjRepo.findMyCoupons(userIndexInt);
        
        // 3. DTO로 변환
        List<Map<String, Object>> couponList = coupons.stream()
            .map(this::toCouponDto)
            .collect(Collectors.toList());
        
        log.info("조회된 쿠폰 수: {}", couponList.size());
        
        return couponList;
    }
    
    /**
     * 만료된 쿠폰 상태를 5번으로 변경
     */
    @Transactional
    public void updateExpiredCoupons(Integer userIndex) {
        LocalDateTime now = LocalDateTime.now();
        couponListJtjRepo.updateExpiredCoupons(userIndex, now);
        log.info("만료된 쿠폰 상태 업데이트 완료 - userIndex: {}", userIndex);
    }
    
    /**
     * Coupon 엔티티를 Map으로 변환
     */
    private Map<String, Object> toCouponDto(Coupon coupon) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("couponIndex", coupon.getCouponIndex());
        dto.put("couponName", coupon.getCouponName());
        dto.put("couponPrice", coupon.getCouponPrice());
        dto.put("couponLimit", coupon.getCouponLimit());
        dto.put("couponIssuanceStatusIndex", coupon.getCouponIssuanceStatusIndex());
        dto.put("couponProvidedStatusIndex", coupon.getCouponProvidedStatusIndex());
        dto.put("couponIssuanceTime", coupon.getCouponIssuanceTime());
        dto.put("couponProvidedTime", coupon.getCouponProvidedTime());
        dto.put("couponLimitTime", coupon.getCouponLimitTime());
        dto.put("couponCondition", coupon.getCouponCondition());
        dto.put("issuanceUserIndex", coupon.getIssuanceUser() != null ? coupon.getIssuanceUser().getUserIndex() : null);
        dto.put("providedUserIndex", coupon.getProvidedUser() != null ? coupon.getProvidedUser().getUserIndex() : null);
        // 날짜 포맷 추가
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        dto.put("issuanceDateStr", coupon.getCouponIssuanceTime() != null ? coupon.getCouponIssuanceTime().format(formatter) : "");
        dto.put("limitDateStr", coupon.getCouponLimitTime() != null ? coupon.getCouponLimitTime().format(formatter) : "");
        // 가맹점 정보 추가
        String storeName = "";
        Integer storeIndex = null;
        if (coupon.getIssuanceUser() != null) {
            Integer userIndex = coupon.getIssuanceUser().getUserIndex();
            Optional<Store> storeOpt = storeInfoJdbRepo.findByUserIndex(userIndex);
            if (storeOpt.isPresent()) {
                storeName = storeOpt.get().getStoreName();
                storeIndex = storeOpt.get().getStoreIndex();
            }
        }
        dto.put("storeName", storeName);
        dto.put("storeIndex", storeIndex);
        return dto;
    }
} 