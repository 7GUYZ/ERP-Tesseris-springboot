package com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.dto.StoreInfoResponseDto;
import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.dto.StoreImageResponse;
import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.dto.StoreUpdateRequest;
import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.repository.StoreBasicInfoImageJdbRepo;
import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.repository.StoreInfoJdbRepo;
import com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.repository.StoreCategoryJdbRepo;
import com.jakdang.labs.entity.Store;
import com.jakdang.labs.entity.StoreCategory;
import com.jakdang.labs.entity.StoreImage;
import com.jakdang.labs.entity.UserTesseris;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StoreInfoService {
    
    @Autowired
    private StoreInfoJdbRepo storeInfoRepository;
    
    @Autowired
    private StoreBasicInfoImageJdbRepo storeImageRepository;
    
    @Autowired
    private StoreCategoryJdbRepo storeCategoryRepository;
    
    /**
     * 모든 매장 카테고리 목록 조회
     */
    public List<StoreCategory> getAllStoreCategories() {
        return storeCategoryRepository.findAll();
    }
    
    @Autowired
    private S3ImageService s3ImageService;
    
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
    
    public Map<String, Object> getStoreImages(Integer userIndex) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<StoreImageResponse> images = storeInfoRepository.getStoreImagesByUserIndex(userIndex);
            result.put("success", true);
            result.put("images", images);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "가맹점 이미지 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return result;
    }
    
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
    
    @Transactional
    public Map<String, Object> uploadStoreImage(Integer userIndex, MultipartFile file, String mainImageStatus) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Optional<Store> storeOpt = storeInfoRepository.findByUserIndex(userIndex);
            if (!storeOpt.isPresent()) {
                result.put("success", false);
                result.put("message", "가맹점 정보를 찾을 수 없습니다.");
                return result;
            }
            
            Store store = storeOpt.get();
            
            // 이미지 개수 체크 (최대 9장)
            Long imageCount = storeImageRepository.countByUserIndex(userIndex);
            if (imageCount >= 9) {
                result.put("success", false);
                result.put("message", "이미지는 최대 9장까지 업로드 가능합니다.");
                return result;
            }
            
            // 파일 유효성 검사
            if (file.isEmpty()) {
                result.put("success", false);
                result.put("message", "업로드할 파일을 선택해주세요.");
                return result;
            }
            
            // 파일 크기 검사 (5MB 제한)
            if (file.getSize() > 5 * 1024 * 1024) {
                result.put("success", false);
                result.put("message", "파일 크기는 5MB 이하여야 합니다.");
                return result;
            }
            
            // 파일 타입 검사
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                result.put("success", false);
                result.put("message", "이미지 파일만 업로드 가능합니다.");
                return result;
            }
            
            // S3에 이미지 업로드
            String imageUrl = s3ImageService.uploadImage(file, "store-images");
            
            // 메인 이미지인 경우 기존 메인 이미지 해제
            if ("T".equals(mainImageStatus)) {
                Optional<StoreImage> existingMainImage = storeImageRepository.findMainImageByUserIndex(userIndex);
                if (existingMainImage.isPresent()) {
                    StoreImage mainImage = existingMainImage.get();
                    mainImage.setStoreMainImageStatus("N");
                    storeImageRepository.save(mainImage);
                }
            }
            
            // 새 이미지 저장
            StoreImage newImage = new StoreImage();
            newImage.setStoreUserIndex(store);
            newImage.setStoreImage(imageUrl);
            newImage.setStoreMainImageStatus(mainImageStatus);
            
            storeImageRepository.save(newImage);
            
            result.put("success", true);
            result.put("message", "이미지가 성공적으로 업로드되었습니다.");
            result.put("imageUrl", imageUrl);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "이미지 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return result;
    }
    
    @Transactional
    public Map<String, Object> deleteStoreImage(Integer userIndex, Integer imageIndex) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Optional<StoreImage> imageOpt = storeImageRepository.findById(imageIndex);
            if (!imageOpt.isPresent()) {
                result.put("success", false);
                result.put("message", "이미지를 찾을 수 없습니다.");
                return result;
            }
            
            StoreImage image = imageOpt.get();
            
            // 해당 사용자의 이미지인지 확인
            if (!image.getStoreUserIndex().getUserIndex().getUserIndex().equals(userIndex)) {
                result.put("success", false);
                result.put("message", "삭제 권한이 없습니다.");
                return result;
            }
            
            // S3에서 이미지 삭제
            try {
                s3ImageService.deleteImage(image.getStoreImage());
            } catch (Exception e) {
                // S3 삭제 실패해도 DB에서는 삭제 진행
                System.err.println("S3 이미지 삭제 실패: " + e.getMessage());
            }
            
            storeImageRepository.delete(image);
            
            result.put("success", true);
            result.put("message", "이미지가 성공적으로 삭제되었습니다.");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "이미지 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return result;
    }
} 