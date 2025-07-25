package com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.controller;

import com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.service.StoreImageService;
import com.jakdang.labs.entity.StoreImage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/store/images")
@RequiredArgsConstructor
public class StoreImageController {
    private final StoreImageService storeImageService;

    // 이미지 목록 조회
    @GetMapping
    public ResponseEntity<List<StoreImage>> getImages(@RequestParam Integer storeIndex) {
        return ResponseEntity.ok(storeImageService.getImages(storeIndex));
    }

    // 대표/상세/삭제 일괄 저장
    @PostMapping("/batch")
    public ResponseEntity<?> uploadImages(
            @RequestParam Integer storeIndex,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "detailImages", required = false) List<MultipartFile> detailImages,
            @RequestParam(value = "deleteIds", required = false) List<Integer> deleteIds
    ) throws Exception {
        storeImageService.saveImages(storeIndex, mainImage, detailImages, deleteIds);
        return ResponseEntity.ok().body("이미지 저장 완료");
    }

    // 이미지 단건 삭제
    @DeleteMapping("/{imageIndex}")
    public ResponseEntity<?> deleteImage(@PathVariable Integer imageIndex) {
        storeImageService.deleteImage(imageIndex);
        return ResponseEntity.ok().body("이미지 삭제 완료");
    }
} 