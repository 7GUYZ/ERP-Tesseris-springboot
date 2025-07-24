package com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreRepresentativeImage.service;

import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreRepresentativeImage.repository.StoreRepresentativeImageJdbRepo;
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
public class StoreRepresentativeImageService {
    private final StoreRepresentativeImageJdbRepo storeImageJdbRepo;
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
        storeImageJdbRepo.deleteById(imageIndex);
    }

    @Transactional
    public void saveImages(Integer storeIndex, MultipartFile mainImage, List<MultipartFile> detailImages, List<Integer> deleteIds) throws Exception {
        Store store = storeRepo.findById(storeIndex).orElseThrow(() -> new RuntimeException("Store not found"));
        if (deleteIds != null) {
            for (Integer id : deleteIds) {
                storeImageJdbRepo.deleteById(id);
            }
        }
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

    public Store findStoreByUserIndex(Integer userIndex) {
        return storeRepo.findFirstByUserIndex(userIndex)
            .orElseThrow(() -> new RuntimeException("해당 user_index의 매장이 없습니다."));
    }
} 