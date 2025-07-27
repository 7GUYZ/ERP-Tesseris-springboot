package com.jakdang.labs.api.dabin.AdvertisementManagement.service;

import com.jakdang.labs.api.dabin.AdvertisementManagement.dto.AdvertisementRequest;
import com.jakdang.labs.api.dabin.AdvertisementManagement.dto.AdvertisementResponse;
import com.jakdang.labs.api.dabin.AdvertisementManagement.repository.AdvertisementRepository;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.UserTesserisRepository;
import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.service.S3ImageService;
import com.jakdang.labs.entity.Advertisement;
import com.jakdang.labs.entity.UserTesseris;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final S3ImageService s3ImageService;
    private final UserTesserisRepository userTesserisRepository;

    /**
     * 모든 광고 목록 조회
     */
    public List<AdvertisementResponse> getAllAdvertisements() {
        List<Object[]> results = advertisementRepository.findAllAdvertisementsWithUserInfo();
        
        return results.stream()
                .map(result -> new AdvertisementResponse(
                        (Integer) result[0],
                        (String) result[1],
                        (String) result[2],
                        (String) result[3],
                        (LocalDateTime) result[4]
                ))
                .collect(Collectors.toList());
    }

    /**
     * 특정 광고 조회
     */
    public AdvertisementResponse getAdvertisement(Integer advertisementIndex) {
        List<Object[]> results = advertisementRepository.findAdvertisementWithUserInfo(advertisementIndex);
        if (results != null && !results.isEmpty()) {
            Object[] arr = results.get(0);
            return new AdvertisementResponse(
                    (Integer) arr[0],
                    (String) arr[1],
                    (String) arr[2],
                    (String) arr[3],
                    (LocalDateTime) arr[4]
            );
        }
        return null;
    }

    /**
     * 광고 등록
     */
    @Transactional
    public Map<String, Object> createAdvertisement(AdvertisementRequest request, MultipartFile file, Integer userIndex) {
        Map<String, Object> result = new java.util.HashMap<>();
        
        try {
            // UserTesseris 조회
            Optional<UserTesseris> userOptional = userTesserisRepository.findById(userIndex);
            if (userOptional.isEmpty()) {
                result.put("success", false);
                result.put("message", "사용자를 찾을 수 없습니다.");
                return result;
            }
            
            UserTesseris user = userOptional.get();

            // 이미지 업로드
            String imageUrl = null;
            if (file != null && !file.isEmpty()) {
                imageUrl = s3ImageService.uploadImage(file, "advertisements");
            }

            // 광고 생성
            Advertisement advertisement = new Advertisement();
            advertisement.setAdvertisementPhoto(imageUrl);
            advertisement.setAdvertisementUrl(request.getAdvertisementUrl());
            advertisement.setUserIndex(user);
            advertisement.setAdvertisementCreateTime(LocalDateTime.now());

            advertisementRepository.save(advertisement);

            result.put("success", true);
            result.put("message", "광고를 등록하였습니다.");
            result.put("advertisementIndex", advertisement.getAdvertisementIndex());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "광고 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 광고 수정
     */
    @Transactional
    public Map<String, Object> updateAdvertisement(Integer advertisementIndex, AdvertisementRequest request, MultipartFile file) {
        Map<String, Object> result = new java.util.HashMap<>();
        
        try {
            System.out.println("=== AdvertisementService.updateAdvertisement() 호출 ===");
            System.out.println("advertisementIndex: " + advertisementIndex);
            System.out.println("file: " + (file != null ? file.getOriginalFilename() : "null"));
            
            Optional<Advertisement> optionalAdvertisement = advertisementRepository.findById(advertisementIndex);
            if (optionalAdvertisement.isEmpty()) {
                result.put("success", false);
                result.put("message", "광고를 찾을 수 없습니다.");
                return result;
            }

            Advertisement advertisement = optionalAdvertisement.get();
            
            System.out.println("기존 광고 정보:");
            System.out.println("  - advertisementIndex: " + advertisement.getAdvertisementIndex());
            System.out.println("  - advertisementPhoto: " + advertisement.getAdvertisementPhoto());
            System.out.println("  - advertisementCreateTime: " + advertisement.getAdvertisementCreateTime());

            String newImageUrl = advertisement.getAdvertisementPhoto();
            
            // 새 이미지가 업로드된 경우
            if (file != null && !file.isEmpty()) {
                // 기존 이미지 삭제
                if (advertisement.getAdvertisementPhoto() != null) {
                    try {
                        s3ImageService.deleteImage(advertisement.getAdvertisementPhoto());
                        System.out.println("기존 S3 파일 삭제 완료: " + advertisement.getAdvertisementPhoto());
                    } catch (Exception e) {
                        System.err.println("기존 S3 파일 삭제 실패: " + e.getMessage());
                        // 기존 파일 삭제 실패해도 계속 진행
                    }
                }
                // 새 이미지 업로드
                newImageUrl = s3ImageService.uploadImage(file, "advertisements");
                System.out.println("새 S3 파일 업로드 완료: " + newImageUrl);
            }

            // 등록일 업데이트 (수정 시점으로)
            LocalDateTime newCreateTime = LocalDateTime.now();
            System.out.println("등록일 업데이트: " + newCreateTime);
            
            // 직접 SQL 업데이트로 DB에 반영
            int updateResult = advertisementRepository.updateAdvertisementPhotoUrlAndCreateTime(
                advertisementIndex, 
                newImageUrl, 
                request.getAdvertisementUrl(), 
                newCreateTime
            );
            System.out.println("DB 업데이트 결과: " + updateResult + "행이 업데이트됨");
            
            if (updateResult == 0) {
                throw new RuntimeException("DB 업데이트가 실패했습니다. 업데이트된 행이 없습니다.");
            }

            result.put("success", true);
            result.put("message", "광고를 수정하였습니다.");
        } catch (Exception e) {
            System.err.println("=== AdvertisementService.updateAdvertisement() 실패 ===");
            System.err.println("에러 메시지: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "광고 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 광고 삭제
     */
    @Transactional
    public Map<String, Object> deleteAdvertisement(Integer advertisementIndex) {
        Map<String, Object> result = new java.util.HashMap<>();
        
        try {
            Optional<Advertisement> optionalAdvertisement = advertisementRepository.findById(advertisementIndex);
            if (optionalAdvertisement.isEmpty()) {
                result.put("success", false);
                result.put("message", "광고를 찾을 수 없습니다.");
                return result;
            }

            Advertisement advertisement = optionalAdvertisement.get();

            // S3에서 이미지 삭제
            if (advertisement.getAdvertisementPhoto() != null) {
                s3ImageService.deleteImage(advertisement.getAdvertisementPhoto());
            }

            advertisementRepository.delete(advertisement);

            result.put("success", true);
            result.put("message", "광고를 삭제하였습니다.");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "광고 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return result;
    }
} 