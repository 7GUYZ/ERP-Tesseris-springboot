package com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.service;

import com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.repository.FrontMyPageStoreImageJdbRepo;
import com.jakdang.labs.entity.Store;
import com.jakdang.labs.entity.StoreImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.service.S3ImageService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreImageService {
    private final FrontMyPageStoreImageJdbRepo storeImageJdbRepo;
    private final com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.repository.FrontMyPageStoreInfoJdbRepo storeRepo;
    private final S3ImageService s3ImageService; // 기존 S3Uploader 대신 S3ImageService 사용

    // 이미지 전체 조회
    public List<StoreImage> getImages(Integer storeIndex) {
        return storeImageJdbRepo.findAllByStoreIndex(storeIndex);
    }

    // 대표/상세 이미지 구분 조회
    public List<StoreImage> getImagesByStatus(Integer storeIndex, String status) {
        return storeImageJdbRepo.findByStoreIndexAndStatus(storeIndex, status);
    }

    // 이미지 단건 삭제
    @Transactional
    public void deleteImage(Integer imageIndex) {
        storeImageJdbRepo.deleteById(imageIndex);
    }

    // 이미지 일괄 저장 (대표/상세/삭제)
    @Transactional
    public void saveImages(Integer storeIndex, MultipartFile mainImage, List<MultipartFile> detailImages, List<Integer> deleteIds) throws Exception {
        Store store = storeRepo.findById(storeIndex).orElseThrow(() -> new RuntimeException("Store not found"));
        // 1. 삭제
        if (deleteIds != null) {
            for (Integer id : deleteIds) {
                storeImageJdbRepo.deleteById(id);
            }
        }
        // 2. 대표 이미지 업로드 및 기존 대표 삭제
        if (mainImage != null && !mainImage.isEmpty()) {
            List<StoreImage> oldMain = storeImageJdbRepo.findMainImageByStoreIndex(storeIndex);
            for (StoreImage img : oldMain) {
                storeImageJdbRepo.deleteById(img.getStoreImageIndex());
            }
            String url = s3ImageService.uploadImage(mainImage, "store/" + storeIndex);
            StoreImage main = new StoreImage();
            main.setStoreUserIndex(store);
            main.setStoreImage(url);
            main.setStoreMainImageStatus("T");
            storeImageJdbRepo.save(main);
        }
        // 3. 상세 이미지 업로드
        if (detailImages != null) {
            for (MultipartFile file : detailImages) {
                if (file != null && !file.isEmpty()) {
                    String url = s3ImageService.uploadImage(file, "store/" + storeIndex);
                    StoreImage detail = new StoreImage();
                    detail.setStoreUserIndex(store);
                    detail.setStoreImage(url);
                    detail.setStoreMainImageStatus("N");
                    storeImageJdbRepo.save(detail);
                }
            }
        }
    }
} 