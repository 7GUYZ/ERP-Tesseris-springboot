package com.jakdang.labs.api.dabin.BannerManagement.service;

import com.jakdang.labs.entity.Banner;
import com.jakdang.labs.api.dabin.BannerManagement.dto.BannerResponseDto;
import com.jakdang.labs.api.dabin.BannerManagement.repository.BannerRepository;
import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.service.S3ImageService;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.UserTesserisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BannerService {
    
    private final BannerRepository bannerRepository;
    private final UserTesserisRepository userTesserisRepository;
    private final S3ImageService s3ImageService;
    
    public List<BannerResponseDto> getAllBanners() {
        System.out.println("=== BannerService.getAllBanners() 호출 ===");
        List<BannerResponseDto> banners = bannerRepository.findAllBannersWithUserInfo();
        System.out.println("Repository에서 반환된 배너 수: " + banners.size());
        return banners;
    }
    
    public Optional<BannerResponseDto> getBannerById(Integer bannerIndex) {
        return bannerRepository.findBannerWithUserInfo(bannerIndex);
    }
    
    @Transactional
    public BannerResponseDto createBanner(MultipartFile file, Integer userIndex) throws IOException {
        // S3에 파일 업로드
        String fileKey = s3ImageService.uploadImage(file, "banner");
        
        // UserTesseris 조회
        UserTesseris userTesseris = userTesserisRepository.findById(userIndex)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Banner 엔티티 생성
        Banner banner = new Banner();
        banner.setBannerPhoto(fileKey);
        banner.setUserIndex(userTesseris);
        banner.setBannerCreateTime(LocalDateTime.now());
        
        Banner savedBanner = bannerRepository.save(banner);
        
        return new BannerResponseDto(
            savedBanner.getBannerIndex(),
            savedBanner.getUserIndex().getUsersId().getName(),
            savedBanner.getBannerPhoto(),
            savedBanner.getBannerCreateTime()
        );
    }
    
    @Transactional
    public BannerResponseDto updateBanner(Integer bannerIndex, MultipartFile file, Integer userIndex) throws IOException {
        try {
            System.out.println("=== BannerService.updateBanner() 호출 ===");
            System.out.println("bannerIndex: " + bannerIndex);
            System.out.println("userIndex: " + userIndex);
            System.out.println("file: " + (file != null ? file.getOriginalFilename() : "null"));
            
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("업로드된 파일이 없습니다.");
            }
            
            Banner banner = bannerRepository.findById(bannerIndex)
                .orElseThrow(() -> new RuntimeException("Banner not found with index: " + bannerIndex));
            
            System.out.println("기존 배너 정보:");
            System.out.println("  - bannerIndex: " + banner.getBannerIndex());
            System.out.println("  - bannerPhoto: " + banner.getBannerPhoto());
            System.out.println("  - bannerCreateTime: " + banner.getBannerCreateTime());
            
            // 기존 파일 삭제
            if (banner.getBannerPhoto() != null) {
                try {
                    s3ImageService.deleteImage(banner.getBannerPhoto());
                    System.out.println("기존 S3 파일 삭제 완료: " + banner.getBannerPhoto());
                } catch (Exception e) {
                    System.err.println("기존 S3 파일 삭제 실패: " + e.getMessage());
                    // 기존 파일 삭제 실패해도 계속 진행
                }
            }
            
            // 새 파일 업로드
            String fileKey = s3ImageService.uploadImage(file, "banner");
            System.out.println("새 S3 파일 업로드 완료: " + fileKey);
            
            // 등록일 업데이트 (수정 시점으로)
            LocalDateTime newCreateTime = LocalDateTime.now();
            System.out.println("등록일 업데이트: " + newCreateTime);
            
            // 직접 SQL 업데이트로 DB에 반영
            int updateResult = bannerRepository.updateBannerPhotoAndCreateTime(bannerIndex, fileKey, newCreateTime);
            System.out.println("DB 업데이트 결과: " + updateResult + "행이 업데이트됨");
            
            if (updateResult == 0) {
                throw new RuntimeException("DB 업데이트가 실패했습니다. 업데이트된 행이 없습니다.");
            }
            
            // 업데이트 후 다시 조회
            Banner updatedBanner = bannerRepository.findById(bannerIndex)
                .orElseThrow(() -> new RuntimeException("Updated banner not found"));
            
            System.out.println("업데이트 후 배너 정보:");
            System.out.println("  - bannerIndex: " + updatedBanner.getBannerIndex());
            System.out.println("  - bannerPhoto: " + updatedBanner.getBannerPhoto());
            System.out.println("  - bannerCreateTime: " + updatedBanner.getBannerCreateTime());
            
            return new BannerResponseDto(
                updatedBanner.getBannerIndex(),
                updatedBanner.getUserIndex().getUsersId().getName(),
                updatedBanner.getBannerPhoto(),
                updatedBanner.getBannerCreateTime()
            );
            
        } catch (Exception e) {
            System.err.println("=== BannerService.updateBanner() 실패 ===");
            System.err.println("에러 메시지: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    @Transactional
    public void deleteBanner(Integer bannerIndex) {
        Banner banner = bannerRepository.findById(bannerIndex)
            .orElseThrow(() -> new RuntimeException("Banner not found"));
        
        // S3에서 파일 삭제
        if (banner.getBannerPhoto() != null) {
            s3ImageService.deleteImage(banner.getBannerPhoto());
        }
        
        bannerRepository.delete(banner);
    }
    
    /**
     * 기존 배너들의 bannerCreateTime을 현재 시간으로 업데이트
     */
    @Transactional
    public void updateBannerCreateTimes() {
        List<Banner> banners = bannerRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        
        for (Banner banner : banners) {
            if (banner.getBannerCreateTime() == null) {
                banner.setBannerCreateTime(now);
                bannerRepository.save(banner);
            }
        }
    }
} 