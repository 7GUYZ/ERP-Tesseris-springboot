package com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.dto.StoreInfoResponseDto;
import com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.repository.UserTesserisJdbRepo;
import com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.repository.FrontMyPageStoreInfoJdbRepo;
import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.dto.StoreUpdateRequest;
import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.repository.StoreInfoJdbRepo;
import com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.repository.StoreCategoryJdbRepo;
import com.jakdang.labs.entity.Store;
import com.jakdang.labs.entity.StoreCategory;
import com.jakdang.labs.entity.UserTesseris;

@Service
public class StoreInfoService {
    
    @Autowired
    private StoreInfoJdbRepo storeInfoRepository;
    
    @Autowired
    private StoreCategoryJdbRepo storeCategoryRepository;
    
    @Autowired
    private UserTesserisJdbRepo userTesserisRepository;
    
    @Autowired
    private FrontMyPageStoreInfoJdbRepo frontMyPageStoreInfoJdbRepo;
    
    /**
     * 모든 매장 카테고리 목록 조회
     */
    public List<StoreCategory> getAllStoreCategories() {
        return storeCategoryRepository.findAll();
    }
    
    public Map<String, Object> getStoreInfo(Integer userIndex) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 가맹점 정보 조회
            Optional<com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.dto.StoreInfoResponse> storeInfo = storeInfoRepository.getStoreInfoByUserIndex(userIndex);
            if (storeInfo.isPresent()) {
                // DTO 변환: FrontStoreInfoMenu.StoreBasicInfo.dto.StoreInfoResponse -> FrontMyPageStoreInfo.dto.StoreInfoResponse
                com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.dto.StoreInfoResponse basicInfo = storeInfo.get();
                StoreInfoResponseDto convertedResponse = new StoreInfoResponseDto(
                    basicInfo.getStoreIndex(),
                    basicInfo.getStoreName(),
                    basicInfo.getStoreCategoryName(),
                    basicInfo.getStoreAddress(),
                    basicInfo.getStorePhone(),
                    basicInfo.getStoreSite(),
                    basicInfo.getStoreZoneCode(),
                    basicInfo.getStoreDetailAddress(),
                    basicInfo.getStoreMemo(),
                    "" // busineeUserId는 별도로 조회해야 함
                );
                
                result.put("success", true);
                result.put("storeInfo", convertedResponse);
            } else {
                result.put("success", false);
                result.put("message", "가맹점 정보를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "가맹점 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * JWT 방식의 가맹점 정보 조회
     */
    public Map<String, Object> getStoreInfoByUserId(String userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // userId로 UserTesseris 조회
            UserTesseris userTesseris = userTesserisRepository.findByUsersId_Id(userId)
                .orElseThrow(() -> new RuntimeException("UserTesseris not found for userId: " + userId));
            
            // userIndex로 가맹점 정보 조회
            Optional<com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.dto.StoreInfoResponse> storeInfo = storeInfoRepository.getStoreInfoByUserIndex(userTesseris.getUserIndex());
            if (storeInfo.isPresent()) {
                // DTO 변환: FrontStoreInfoMenu.StoreBasicInfo.dto.StoreInfoResponse -> FrontMyPageStoreInfo.dto.StoreInfoResponse
                com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.dto.StoreInfoResponse basicInfo = storeInfo.get();
                
                // 담당자 아이디 조회
                String businessUserId = "";
                if (basicInfo.getBusinessManUserIndex() != null) {
                    businessUserId = frontMyPageStoreInfoJdbRepo.findBusinessUserIdByUserIndex(basicInfo.getBusinessManUserIndex())
                        .orElse("Unknown");
                }
                
                StoreInfoResponseDto convertedResponse = new StoreInfoResponseDto(
                    basicInfo.getStoreIndex(),
                    basicInfo.getStoreName(),
                    basicInfo.getStoreCategoryName(),
                    basicInfo.getStoreAddress(),
                    basicInfo.getStorePhone(),
                    basicInfo.getStoreSite(),
                    basicInfo.getStoreZoneCode(),
                    basicInfo.getStoreDetailAddress(),
                    basicInfo.getStoreMemo(),
                    businessUserId // 담당자 아이디
                );
                
                result.put("success", true);
                result.put("data", convertedResponse);
            } else {
                result.put("success", false);
                result.put("message", "가맹점 정보를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "가맹점 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return result;
    }
    
    // 이미지 관련 메서드 전체 삭제 (StoreImageService로 통합)
    
    @Transactional
    public Map<String, Object> updateStoreInfo(Integer userIndex, StoreUpdateRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Optional<Store> storeOpt = storeInfoRepository.findByUserIndex(userIndex);
            if (!storeOpt.isPresent()) {
                result.put("success", false);
                result.put("message", "가맹점 정보를 찾을 수 없습니다.");
                return result;
            }
            
            Store store = storeOpt.get();
            
            // 카테고리 업데이트 (DB에서 조회)
            if (request.getStoreCategoryName() != null && !request.getStoreCategoryName().isEmpty()) {
                Optional<StoreCategory> newCategoryOpt = storeCategoryRepository.findByStoreCategoryName(request.getStoreCategoryName());
                if (newCategoryOpt.isPresent()) {
                    store.setStoreCategory(newCategoryOpt.get());
                } else {
                    result.put("success", false);
                    result.put("message", "존재하지 않는 카테고리입니다: " + request.getStoreCategoryName());
                    return result;
                }
            }
            
            // 가맹점 정보 업데이트
            store.setStoreName(request.getStoreName());
            store.setStorePhone(request.getStorePhone());
            store.setStoreSite(request.getStoreSite());
            store.setStoreZoneCode(request.getStoreZoneCode());
            store.setStoreAddress(request.getStoreAddress());
            store.setStoreDetailAddress(request.getStoreDetailAddress());
            store.setStoreMemo(request.getStoreMemo());
            store.setStorePos1(request.getStorePos1());
            store.setStorePos2(request.getStorePos2());
            
            storeInfoRepository.save(store);
            
            result.put("success", true);
            result.put("message", "가맹점 정보가 성공적으로 수정되었습니다.");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "가맹점 정보 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * JWT 방식의 가맹점 정보 수정 (userId로 조회)
     */
    @Transactional
    public Map<String, Object> updateStoreInfoByUserId(String userId, StoreUpdateRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // userId로 UserTesseris 조회
            UserTesseris userTesseris = userTesserisRepository.findByUsersId_Id(userId)
                .orElseThrow(() -> new RuntimeException("UserTesseris not found for userId: " + userId));
            
            // userIndex로 가맹점 정보 업데이트
            return updateStoreInfo(userTesseris.getUserIndex(), request);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "가맹점 정보 수정 중 오류가 발생했습니다: " + e.getMessage());
            return result;
        }
    }
    
    // 이미지 관련 메서드 전체 삭제 (StoreImageService로 통합)
} 