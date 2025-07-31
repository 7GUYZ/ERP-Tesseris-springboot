package com.jakdang.labs.api.deokkyu.modal_admin.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jakdang.labs.api.deokkyu.modal_admin.dto.StoreImagesPresignedDto;
import com.jakdang.labs.api.deokkyu.store.repository.StorehdkRepo;
import com.jakdang.labs.api.deokkyu.storeRegister.service.S3FileUploadService;
import com.jakdang.labs.entity.Store;

@RequiredArgsConstructor
@Slf4j
@Service
public class ModalImageService {
    
    private final StorehdkRepo storeRepository;
    private final S3FileUploadService s3FileUploadService;

    /**
     * store_index로 가맹점 이미지 조회 + S3 Presigned URL 생성
     * @param storeIndex 가맹점 인덱스
     * @return 가맹점 이미지 정보 및 Presigned URL
     */
    public StoreImagesPresignedDto getStoreImagesWithPresignedUrls(Integer storeIndex) {
        try {
            log.info("=== 🖼️ 가맹점 이미지 Presigned URL 생성 시작 ===");
            log.info("🔍 Store 조회: store_index={}", storeIndex);
            
            // 1. store_index로 Store 조회
            Store store = storeRepository.findById(storeIndex)
                .orElseThrow(() -> new RuntimeException("Store를 찾을 수 없습니다: " + storeIndex));
            
            log.info("✅ Store 조회 성공: store_name={}", store.getStoreName());
            log.info("📋 이미지 URL 정보:");
            log.info("   - 사업자등록증: '{}'", store.getStoreBusinessLicensePhoto());
            log.info("   - 간판 사진: '{}'", store.getStoreSignPhoto());
            log.info("   - 외관 사진: '{}'", store.getStoreProntPhoto());
            
            // 2. 각 이미지 URL에 대해 Presigned URL 생성
            String businessLicensePresignedUrl = null;
            String signPhotoPresignedUrl = null;
            String frontPhotoPresignedUrl = null;
            
            // 사업자등록증 Presigned URL 생성
            if (store.getStoreBusinessLicensePhoto() != null && !store.getStoreBusinessLicensePhoto().trim().isEmpty()) {
                businessLicensePresignedUrl = s3FileUploadService.generatePresignedUrl(store.getStoreBusinessLicensePhoto());
                log.info("✅ 사업자등록증 Presigned URL 생성: {}", businessLicensePresignedUrl != null ? "성공" : "실패");
            } else {
                log.info("ℹ️ 사업자등록증 이미지 URL이 없음");
            }
            
            // 간판 사진 Presigned URL 생성
            if (store.getStoreSignPhoto() != null && !store.getStoreSignPhoto().trim().isEmpty()) {
                signPhotoPresignedUrl = s3FileUploadService.generatePresignedUrl(store.getStoreSignPhoto());
                log.info("✅ 간판 사진 Presigned URL 생성: {}", signPhotoPresignedUrl != null ? "성공" : "실패");
            } else {
                log.info("ℹ️ 간판 사진 이미지 URL이 없음");
            }
            
            // 외관 사진 Presigned URL 생성
            if (store.getStoreProntPhoto() != null && !store.getStoreProntPhoto().trim().isEmpty()) {
                frontPhotoPresignedUrl = s3FileUploadService.generatePresignedUrl(store.getStoreProntPhoto());
                log.info("✅ 외관 사진 Presigned URL 생성: {}", frontPhotoPresignedUrl != null ? "성공" : "실패");
            } else {
                log.info("ℹ️ 외관 사진 이미지 URL이 없음");
            }
            
            // 3. DTO 생성 및 반환
            StoreImagesPresignedDto result = StoreImagesPresignedDto.builder()
                .businessLicensePhotoUrl(store.getStoreBusinessLicensePhoto())
                .signPhotoUrl(store.getStoreSignPhoto())
                .frontPhotoUrl(store.getStoreProntPhoto())
                .businessLicensePhotoPresignedUrl(businessLicensePresignedUrl)
                .signPhotoPresignedUrl(signPhotoPresignedUrl)
                .frontPhotoPresignedUrl(frontPhotoPresignedUrl)
                .build();
            
            log.info("🎯 가맹점 이미지 Presigned URL 생성 완료");
            log.info("=== 🖼️ 가맹점 이미지 Presigned URL 생성 완료 ===");
            
            return result;
            
        } catch (Exception e) {
            log.error("❌ 가맹점 이미지 Presigned URL 생성 실패: store_index={}", storeIndex);
            log.error("❌ 오류 타입: {}", e.getClass().getSimpleName());
            log.error("❌ 오류 메시지: {}", e.getMessage());
            log.error("❌ 오류 상세: ", e);
            throw new RuntimeException("가맹점 이미지 조회에 실패했습니다: " + e.getMessage(), e);
        }
    }
}