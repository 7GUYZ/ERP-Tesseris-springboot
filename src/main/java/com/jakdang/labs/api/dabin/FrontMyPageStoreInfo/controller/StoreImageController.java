package com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.controller;

import com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.dto.StoreImageDto;
import com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.service.StoreImageService;
import com.jakdang.labs.entity.Store;
import com.jakdang.labs.entity.StoreImage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.jakdang.labs.api.auth.dto.CustomUserDetails;
import com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.service.FrontMyPageStoreInfoService;
import java.util.List;

@RestController
@RequestMapping("/api/store/images")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class StoreImageController {
    private final StoreImageService storeImageService;
    private final FrontMyPageStoreInfoService storeInfoService;

    // JWT 인증을 사용한 가맹점 이미지 조회
    @GetMapping("/my")
    public ResponseEntity<List<StoreImageDto>> getMyStoreImages(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String userId = userDetails.getUserId();
        Store store = storeInfoService.getStoreByUserId(userId);
        List<StoreImage> images = storeImageService.getImages(store.getStoreIndex());
        List<StoreImageDto> dtos = images.stream()
            .map(img -> new StoreImageDto(
                img.getStoreImageIndex(),
                img.getStoreImage(),
                img.getStoreMainImageStatus()
            ))
            .toList();
        return ResponseEntity.ok(dtos);
    }

    // 대표/상세/삭제 일괄 저장
    @PostMapping("/batch")
    public ResponseEntity<?> uploadImages(
            @RequestParam Integer storeIndex,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "detailImages", required = false) List<MultipartFile> detailImages,
            @RequestParam(value = "deleteIds", required = false) List<Integer> deleteIds
    ) throws Exception {
        System.out.println("[Controller] 받은 deleteIds: " + deleteIds);
        storeImageService.saveImages(storeIndex, mainImage, detailImages, deleteIds);
        return ResponseEntity.ok().body("이미지 저장 완료");
    }

    // 이미지 단건 삭제
    @DeleteMapping("/{imageIndex}")
    public ResponseEntity<?> deleteImage(@PathVariable Integer imageIndex) {
        storeImageService.deleteImage(imageIndex);
        return ResponseEntity.ok().body("이미지 삭제 완료");
    }

    // Presigned URL 발급
    @GetMapping("/presigned")
    public ResponseEntity<String> getPresignedImageUrl(@RequestParam("fileKey") String fileKey) {
        String url = storeImageService.getPresignedImageUrl(fileKey);
        return ResponseEntity.ok(url);
    }
} 