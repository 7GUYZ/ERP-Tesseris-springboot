package com.jakdang.labs.api.jungeun.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.service.S3ImageService;
import com.jakdang.labs.api.jungeun.dto.PopUpDTO;
import com.jakdang.labs.api.jungeun.repository.AdvertisementLjeRepo;
import com.jakdang.labs.entity.Advertisement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PopUpSvc {
    private final AdvertisementLjeRepo advertisementLjeRepo;
    private final S3ImageService s3ImageService;
    
    public List<PopUpDTO> getPopupImages() {
        List<Advertisement> advertisements = advertisementLjeRepo.findActiveAdvertisements();
        
        return advertisements.stream()
            .map(ad -> {
                String presignedUrl = null;
                if (ad.getAdvertisementPhoto() != null && !ad.getAdvertisementPhoto().isEmpty()) {
                    try {
                        presignedUrl = s3ImageService.generatePresignedUrl(ad.getAdvertisementPhoto());
                    } catch (Exception e) {
                        log.error("이미지 URL 생성 실패: {}", e.getMessage());
                    }
                }
                
                return PopUpDTO.builder()
                    .adIndex(ad.getAdvertisementIndex())
                    .adPhoto(presignedUrl)
                    .adUrl(ad.getAdvertisementUrl())
                    .build();
            })
            .collect(Collectors.toList());
    }
}
