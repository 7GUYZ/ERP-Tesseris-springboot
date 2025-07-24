package com.jakdang.labs.api.dabin.AdvertisementManagement.controller;

import com.jakdang.labs.api.dabin.AdvertisementManagement.dto.AdvertisementRequest;
import com.jakdang.labs.api.dabin.AdvertisementManagement.dto.AdvertisementResponse;
import com.jakdang.labs.api.dabin.AdvertisementManagement.service.AdvertisementService;
import com.jakdang.labs.entity.UserTesseris;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dabin/advertisement")
@RequiredArgsConstructor
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    /**
     * 광고 목록 조회
     */
    @GetMapping("/list")
    public ResponseEntity<?> getAdvertisementList() {
        try {
            List<AdvertisementResponse> advertisements = advertisementService.getAllAdvertisements();
            return ResponseEntity.ok(advertisements);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "광고 목록 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 특정 광고 조회
     */
    @GetMapping("/{advertisementIndex}")
    public ResponseEntity<?> getAdvertisement(@PathVariable("advertisementIndex") Integer advertisementIndex) {
        try {
            AdvertisementResponse advertisement = advertisementService.getAdvertisement(advertisementIndex);
            if (advertisement != null) {
                return ResponseEntity.ok(advertisement);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "광고 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 광고 등록
     */
    @PostMapping("/create")
    public ResponseEntity<?> createAdvertisement(
            @RequestParam("advertisementUrl") String advertisementUrl,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "userIndex", defaultValue = "1") Integer userIndex) {
        try {
            AdvertisementRequest request = new AdvertisementRequest();
            request.setAdvertisementUrl(advertisementUrl);

            Map<String, Object> result = advertisementService.createAdvertisement(request, file, userIndex);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "광고 등록 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 광고 수정
     */
    @PutMapping("/{advertisementIndex}")
    public ResponseEntity<?> updateAdvertisement(
            @PathVariable("advertisementIndex") Integer advertisementIndex,
            @RequestParam("advertisementUrl") String advertisementUrl,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            AdvertisementRequest request = new AdvertisementRequest();
            request.setAdvertisementUrl(advertisementUrl);

            Map<String, Object> result = advertisementService.updateAdvertisement(advertisementIndex, request, file);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "광고 수정 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 광고 삭제
     */
    @DeleteMapping("/{advertisementIndex}")
    public ResponseEntity<?> deleteAdvertisement(@PathVariable("advertisementIndex") Integer advertisementIndex) {
        try {
            Map<String, Object> result = advertisementService.deleteAdvertisement(advertisementIndex);
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "광고 삭제 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
} 