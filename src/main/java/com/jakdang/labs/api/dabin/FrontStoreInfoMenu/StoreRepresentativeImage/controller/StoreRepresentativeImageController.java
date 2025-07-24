package com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreRepresentativeImage.controller;

import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreRepresentativeImage.service.StoreRepresentativeImageService;
import com.jakdang.labs.entity.StoreImage;
import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreRepresentativeImage.dto.StoreImageSimpleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Component;
import com.jakdang.labs.entity.Store;

import java.util.List;

@Component("storeRepresentativeImageController")
@RestController
@RequestMapping("/api/store/representative-image")
@RequiredArgsConstructor
public class StoreRepresentativeImageController {
    private final StoreRepresentativeImageService storeImageService;

    @GetMapping("/images")
    public ResponseEntity<List<StoreImageSimpleDto>> getImages(@RequestParam("storeIndex") Integer storeIndex) {
        List<StoreImage> images = storeImageService.getImages(storeIndex);
        List<StoreImageSimpleDto> dtos = images.stream()
            .map(img -> new StoreImageSimpleDto(
                img.getStoreImageIndex(),
                img.getStoreImage(),
                img.getStoreMainImageStatus()
            ))
            .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/images-by-user")
    public ResponseEntity<List<StoreImageSimpleDto>> getImagesByUserIndex(@RequestParam("userIndex") Integer userIndex) {
        // userIndex로 Store(매장) 엔티티 조회
        Store store = storeImageService.findStoreByUserIndex(userIndex);
        // storeIndex로 이미지 조회
        List<StoreImage> images = storeImageService.getImages(store.getStoreIndex());
        // DTO 변환
        List<StoreImageSimpleDto> dtos = images.stream()
            .map(img -> new StoreImageSimpleDto(
                img.getStoreImageIndex(),
                img.getStoreImage(),
                img.getStoreMainImageStatus()
            ))
            .toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/batch")
    public ResponseEntity<?> uploadImages(
            @RequestParam("storeIndex") Integer storeIndex,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "detailImages", required = false) List<MultipartFile> detailImages,
            @RequestParam(value = "deleteIds", required = false) List<Integer> deleteIds
    ) throws Exception {
        storeImageService.saveImages(storeIndex, mainImage, detailImages, deleteIds);
        return ResponseEntity.ok().body("이미지 저장 완료");
    }

    @DeleteMapping("/{imageIndex}")
    public ResponseEntity<?> deleteImage(@PathVariable Integer imageIndex) {
        storeImageService.deleteImage(imageIndex);
        return ResponseEntity.ok().body("이미지 삭제 완료");
    }
} 