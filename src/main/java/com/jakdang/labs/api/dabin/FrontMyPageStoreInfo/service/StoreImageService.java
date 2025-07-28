package com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.service;

import com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.repository.StoreImageJdbRepo;
import com.jakdang.labs.entity.Store;
import com.jakdang.labs.entity.StoreImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.service.S3ImageService;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreImageService {
    private final StoreImageJdbRepo storeImageJdbRepo;
    private final com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.repository.FrontMyPageStoreInfoJdbRepo storeRepo;
    private final S3ImageService s3ImageService;

    public List<StoreImage> getImages(Integer storeIndex) {
        return storeImageJdbRepo.findAllByStoreIndex(storeIndex);
    }

    public List<StoreImage> getImagesByStatus(Integer storeIndex, String status) {
        return storeImageJdbRepo.findByStoreIndexAndStatus(storeIndex, status);
    }

    @Transactional
    public void deleteImage(Integer imageIndex) {
        storeImageJdbRepo.findById(imageIndex).ifPresent(img -> {
            s3ImageService.deleteImage(img.getStoreImage());
            storeImageJdbRepo.deleteById(imageIndex);
        });
    }

    @Transactional
    public void saveImages(Integer storeIndex, MultipartFile mainImage, List<MultipartFile> detailImages, List<Integer> deleteIds) throws Exception {
        Store store = storeRepo.findById(storeIndex).orElseThrow(() -> new RuntimeException("Store not found"));
        // 1. 삭제
        if (deleteIds != null) {
            for (Integer id : deleteIds) {
                System.out.println("[Service] 삭제 시도 이미지 id: " + id);
                storeImageJdbRepo.findById(id).ifPresent(img -> {
                    System.out.println("[Service] S3에서 삭제할 key: " + img.getStoreImage());
                    try {
                        s3ImageService.deleteImage(img.getStoreImage());
                        System.out.println("[Service] S3에서 삭제 성공: " + img.getStoreImage());
                    } catch (Exception e) {
                        System.out.println("[Service] S3 삭제 실패: " + e.getMessage());
                        e.printStackTrace();
                    }
                    storeImageJdbRepo.deleteById(id);
                });
            }
        }
        // 2. 대표 이미지 업로드 및 기존 대표 삭제
        if (mainImage != null && !mainImage.isEmpty()) {
            List<StoreImage> oldMain = storeImageJdbRepo.findMainImageByStoreIndex(storeIndex);
            for (StoreImage img : oldMain) {
                s3ImageService.deleteImage(img.getStoreImage());
                storeImageJdbRepo.deleteById(img.getStoreImageIndex());
            }
            String key = s3ImageService.uploadImage(mainImage, "store/" + storeIndex);
            StoreImage main = new StoreImage();
            main.setStoreUserIndex(store);
            main.setStoreImage(key);
            main.setStoreMainImageStatus("T");
            storeImageJdbRepo.save(main);
        }
        // 3. 상세 이미지 업로드
        if (detailImages != null) {
            for (MultipartFile file : detailImages) {
                if (file != null && !file.isEmpty()) {
                    String key = s3ImageService.uploadImage(file, "store/" + storeIndex);
                    StoreImage detail = new StoreImage();
                    detail.setStoreUserIndex(store);
                    detail.setStoreImage(key);
                    detail.setStoreMainImageStatus("N");
                    storeImageJdbRepo.save(detail);
                }
            }
        }
    }

    public String getPresignedImageUrl(String fileKey) {
        return s3ImageService.generatePresignedUrl(fileKey);
    }

    public Store findStoreByUserIndex(Integer userIndex) {
        return storeRepo.findFirstByUserIndex(userIndex)
            .orElseThrow(() -> new RuntimeException("해당 user_index의 매장이 없습니다."));
    }
} 