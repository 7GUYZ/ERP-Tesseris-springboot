package com.jakdang.labs.api.dabin.FrontEventCouponList.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import com.jakdang.labs.api.dabin.FrontEventCouponList.repository.FranchiseRepository;
import com.jakdang.labs.api.dabin.FrontEventCouponList.repository.StoreCategoryRepository;
import com.jakdang.labs.api.dabin.FrontEventCouponList.dto.FranchiseInfoResponse;
import com.jakdang.labs.api.dabin.FrontEventCouponList.dto.FranchiseCategoryResponse;
import com.jakdang.labs.api.dabin.FrontEventCouponList.dto.NearbyFranchiseRequest;
import com.jakdang.labs.entity.StoreCategory;

@Service
@RequiredArgsConstructor
public class FranchiseService {
    
    private final FranchiseRepository franchiseRepository;
    private final StoreCategoryRepository storeCategoryRepository;
    
    /**
     * 안전한 Double 변환 메서드
     */
    private Double parseDoubleSafely(Object value) {
        if (value == null) {
            return null;
        }
        
        if (value instanceof Double) {
            return (Double) value;
        }
        
        if (value instanceof String) {
            String strValue = (String) value;
            if (strValue.trim().isEmpty()) {
                return null;
            }
            try {
                return Double.parseDouble(strValue);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        
        return null;
    }
    
    /**
     * 특정 가맹점 정보 조회
     */
    public Map<String, Object> getFranchiseInfo(Integer storeIndex) {
        try {
            List<Object[]> results = franchiseRepository.findFranchiseInfoByStoreIndex(storeIndex);
            
            if (results == null || results.isEmpty()) {
                return null;
            }
            
            Object[] row = results.get(0);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("franchise", FranchiseInfoResponse.builder()
                .storeIndex((Integer) row[0])
                .name((String) row[1])
                .phone((String) row[2])
                .address((String) row[3])
                .detailAddress((String) row[4])
                .category((String) row[5])
                .latitude(parseDoubleSafely(row[6]))
                .longitude(parseDoubleSafely(row[7]))
                .userCmUse((String) row[9])
                .image((String) row[10])
                .build());
            
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", "가맹점 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * 모든 가맹점 카테고리 조회
     */
    public Map<String, Object> getAllCategories() {
        try {
            List<StoreCategory> categories = storeCategoryRepository.findAllCategories();
            
            List<FranchiseCategoryResponse> categoryResponses = categories.stream()
                .map(category -> FranchiseCategoryResponse.builder()
                    .storeCategoryIndex(category.getStoreCategoryIndex())
                    .storeCategoryName(category.getStoreCategoryName())
                    .build())
                .collect(Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("categories", categoryResponses);
            
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", "카테고리 조회 중 오류가 발생했습니다: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * 주변 가맹점 검색
     */
    public Map<String, Object> getNearbyFranchises(NearbyFranchiseRequest request) {
        try {
            List<Object[]> results = franchiseRepository.findNearbyFranchises(
                request.getLatitude(),
                request.getLongitude(),
                request.getRadius(),
                request.getFranType()
            );
            
            List<FranchiseInfoResponse> franchises = results.stream()
                .map(row -> FranchiseInfoResponse.builder()
                    .storeIndex((Integer) row[0])
                    .name((String) row[1])
                    .phone((String) row[2])
                    .address((String) row[3])
                    .detailAddress((String) row[4])
                    .category((String) row[5])
                    .latitude(parseDoubleSafely(row[6]))
                    .longitude(parseDoubleSafely(row[7]))
                    .userCmUse((String) row[9])
                    .image((String) row[10])
                    .build())
                .collect(Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("franchises", franchises);
            
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("error", "주변 가맹점 검색 중 오류가 발생했습니다: " + e.getMessage());
            return result;
        }
    }
} 