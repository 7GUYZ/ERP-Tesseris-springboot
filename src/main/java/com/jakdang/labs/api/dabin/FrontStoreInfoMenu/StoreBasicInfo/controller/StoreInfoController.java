package com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.dto.StoreUpdateRequest;
import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.service.StoreInfoService;
import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.entity.StoreCategory;
import lombok.RequiredArgsConstructor;

import java.util.List;


@RestController
@RequestMapping("/api/store/basic-info")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class StoreInfoController {
    
    private final StoreInfoService storeInfoService;
    
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getStoreInfo(@RequestParam("userIndex") Integer userIndex) {
        Map<String, Object> result = storeInfoService.getStoreInfo(userIndex);
        
        return ResponseEntity.ok(result);
    }
    
    // 이미지 관련 API는 StoreImageController로 통합되어 더 이상 사용하지 않습니다.
    /*
    @GetMapping("/images")
    public ResponseEntity<Map<String, Object>> getStoreImages(@RequestParam("userIndex") Integer userIndex) {
        Map<String, Object> result = storeInfoService.getStoreImages(userIndex);
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/info")
    public ResponseEntity<Map<String, Object>> updateStoreInfo(
            @RequestParam("userIndex") Integer userIndex,
            @RequestBody StoreUpdateRequest request) {
        Map<String, Object> result = storeInfoService.updateStoreInfo(userIndex, request);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/image/upload")
    public ResponseEntity<Map<String, Object>> uploadStoreImage(
            @RequestParam("userIndex") Integer userIndex,
            @RequestParam("file") MultipartFile file,
            @RequestParam("mainImageStatus") String mainImageStatus) {
        Map<String, Object> result = storeInfoService.uploadStoreImage(userIndex, file, mainImageStatus);
        return ResponseEntity.ok(result);
    }
    
    @DeleteMapping("/image/{imageIndex}")
    public ResponseEntity<Map<String, Object>> deleteStoreImage(
            @RequestParam("userIndex") Integer userIndex,
            @PathVariable("imageIndex") Integer imageIndex) {
        Map<String, Object> result = storeInfoService.deleteStoreImage(userIndex, imageIndex);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Store Info API is working!");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/image/presigned")
    public ResponseEntity<String> getPresignedImageUrl(@RequestParam String fileKey) {
        String url = storeInfoService.getPresignedImageUrl(fileKey);
        return ResponseEntity.ok(url);
    }
    */
    
    /**
     * 매장 카테고리 목록 조회 (PHP와 동일한 하드코딩된 카테고리)
     */
    @GetMapping("/categories")
    public ResponseDTO<List<StoreCategory>> getStoreCategories() {
        // PHP와 동일한 하드코딩된 카테고리 반환
        List<StoreCategory> categories = List.of(
            new StoreCategory(1, "슈퍼 / 마트"),
            new StoreCategory(2, "레저"),
            new StoreCategory(3, "미용 / 뷰티 / 위생"),
            new StoreCategory(4, "병원 / 약국"),
            new StoreCategory(5, "스포츠 / 헬스"),
            new StoreCategory(6, "식품"),
            new StoreCategory(7, "학원 / 교육"),
            new StoreCategory(8, "서비스업"),
            new StoreCategory(9, "가구 / 인테리어"),
            new StoreCategory(10, "디지털 / 가전"),
            new StoreCategory(11, "생활 / 주방용품"),
            new StoreCategory(12, "음식점 / 카페"),
            new StoreCategory(13, "패션잡화"),
            new StoreCategory(14, "기타 도소매")
        );
        return ResponseDTO.createSuccessResponse("카테고리 목록 조회 성공", categories);
    }
} 