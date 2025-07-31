package com.jakdang.labs.api.taekjun.storelist.controller;

import java.util.List;
import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.service.S3ImageService;
import com.jakdang.labs.api.taekjun.storelist.dto.StoreCategoryDTO;
import com.jakdang.labs.api.taekjun.storelist.dto.StoreListDTO;
import com.jakdang.labs.api.taekjun.storelist.dto.StoreDetailDTO;
import com.jakdang.labs.api.taekjun.storelist.service.StoreListService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/taekjun/storelist")
public class StoreListController {
    private final StoreListService storeService;
    private final S3ImageService s3ImageService;

    @GetMapping
    public ResponseEntity<ResponseDTO<List<StoreCategoryDTO>>> getStoreCategories(){
        return ResponseEntity.ok().body(storeService.getStoreCategories());
    }

    @GetMapping("/filtered")
    public ResponseEntity<ResponseDTO<List<StoreListDTO>>> getFilteredStoreList(
                        @RequestParam(value = "store_category_index", required = false) Integer store_category_index){
        // 파라미터가 null이면 0으로 설정 (전체 카테고리)
        if (store_category_index == null) {
            store_category_index = 0;
        }
        
        // 기존 서비스 호출
        ResponseDTO<List<StoreListDTO>> response = storeService.getFilteredStoreList(store_category_index);
        
        if (response.getResultCode() == 200 && response.getData() != null) {
            List<StoreListDTO> storeList = response.getData();
            
            // 각 스토어의 이미지 URL 생성
            for (StoreListDTO store : storeList) {
                if (store.getStoreImage() != null && !store.getStoreImage().isEmpty()) {
                    try {
                        String presignedUrl = s3ImageService.generatePresignedUrl(store.getStoreImage());
                        store.setStoreImage(presignedUrl);
                    } catch (Exception e) {
                        log.error("이미지 URL 생성 실패: {}", e.getMessage());
                        // 이미지 URL 생성 실패 시 원본 fileKey 유지
                    }
                }
            }
        }
        
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/detail")
    public ResponseEntity<ResponseDTO<?>> getStoreDetail(@RequestParam("store_index") Integer store_index){
        
        // 기존 서비스 호출
        ResponseDTO<?> response = storeService.getStoreDetail(store_index);
        
        if (response.getResultCode() == 200 && response.getData() != null) {
            // StoreDetailDTO에서 이미지 배열 처리
            if (response.getData() instanceof StoreDetailDTO) {
                StoreDetailDTO detailDTO = (StoreDetailDTO) response.getData();
                List<String> storeImages = detailDTO.getStoreImages();
                
                if (storeImages != null && !storeImages.isEmpty()) {
                    
                    // 각 이미지 fileKey를 presigned URL로 변환
                    List<String> presignedUrls = new ArrayList<>();
                    for (String imageFileKey : storeImages) {
                        if (imageFileKey != null && !imageFileKey.trim().isEmpty()) {
                            try {
                                String presignedUrl = s3ImageService.generatePresignedUrl(imageFileKey.trim());
                                presignedUrls.add(presignedUrl);
                            } catch (Exception e) {
                                log.error("상세 이미지 URL 생성 실패: {}", e.getMessage());
                                // 실패 시 원본 fileKey 유지
                                presignedUrls.add(imageFileKey);
                            }
                        }
                    }
                    
                    // 변환된 URL로 업데이트
                    detailDTO.setStoreImages(presignedUrls);
                }
            }
        }
        
        return ResponseEntity.ok().body(response);
    }
} 