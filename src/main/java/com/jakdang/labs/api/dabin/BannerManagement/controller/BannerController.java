package com.jakdang.labs.api.dabin.BannerManagement.controller;

import com.jakdang.labs.api.dabin.BannerManagement.dto.BannerResponseDto;
import com.jakdang.labs.api.dabin.BannerManagement.service.BannerService;
import com.jakdang.labs.api.common.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/dabin/banner")
@RequiredArgsConstructor
public class BannerController {
    
    private final BannerService bannerService;
    
    @GetMapping("/list")
    public ResponseEntity<ResponseDTO<?>> getAllBanners() {
        try {
            List<BannerResponseDto> banners = bannerService.getAllBanners();
            System.out.println("=== 배너 목록 조회 결과 ===");
            for (BannerResponseDto banner : banners) {
                System.out.println("배너: " + banner);
                System.out.println("  - bannerIndex: " + banner.getBannerIndex());
                System.out.println("  - userId: " + banner.getUserId());
                System.out.println("  - bannerPhoto: " + banner.getBannerPhoto());
                System.out.println("  - bannerCreateTime: " + banner.getBannerCreateTime());
                System.out.println("  - bannerCreateTime type: " + (banner.getBannerCreateTime() != null ? banner.getBannerCreateTime().getClass().getName() : "null"));
            }
            return ResponseEntity.ok(ResponseDTO.createSuccessResponse("배너 목록 조회 성공", banners));
        } catch (Exception e) {
            System.err.println("배너 목록 조회 실패: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ResponseDTO.createErrorResponse(500, "배너 목록 조회에 실패했습니다."));
        }
    }
    
    @GetMapping("/{bannerIndex}")
    public ResponseEntity<ResponseDTO<?>> getBannerById(@PathVariable Integer bannerIndex) {
        try {
            Optional<BannerResponseDto> bannerOpt = bannerService.getBannerById(bannerIndex);
            if (bannerOpt.isPresent()) {
                return ResponseEntity.ok(ResponseDTO.createSuccessResponse("배너 조회 성공", bannerOpt.get()));
            } else {
                return ResponseEntity.badRequest().body(ResponseDTO.createErrorResponse(404, "배너를 찾을 수 없습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDTO.createErrorResponse(500, "배너 조회에 실패했습니다."));
        }
    }
    
    @PostMapping("/create")
    public ResponseEntity<ResponseDTO<?>> createBanner(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println("=== 배너 생성 요청 시작 ===");
            System.out.println("파일명: " + file.getOriginalFilename());
            System.out.println("파일 크기: " + file.getSize());
            
            // 현재 로그인한 사용자의 userIndex 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            System.out.println("사용자 이메일: " + userEmail);
            
            // userEmail로 userIndex 조회 (실제 구현에서는 UserService를 통해 조회)
            Integer userIndex = 1; // 임시로 1 설정, 실제로는 UserService에서 조회
            System.out.println("사용자 인덱스: " + userIndex);
            
            BannerResponseDto createdBanner = bannerService.createBanner(file, userIndex);
            System.out.println("배너 생성 성공: " + createdBanner);
            return ResponseEntity.ok(ResponseDTO.createSuccessResponse("배너를 등록하였습니다.", createdBanner));
        } catch (Exception e) {
            System.err.println("=== 배너 생성 실패 ===");
            System.err.println("에러 메시지: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ResponseDTO.createErrorResponse(500, "배너 등록에 실패했습니다: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{bannerIndex}")
    public ResponseEntity<?> updateBanner(
            @PathVariable Integer bannerIndex,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            // 현재 로그인한 사용자의 userIndex 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();
            
            // userEmail로 userIndex 조회 (실제 구현에서는 UserService를 통해 조회)
            Integer userIndex = 1; // 임시로 1 설정, 실제로는 UserService에서 조회
            
            BannerResponseDto updatedBanner = bannerService.updateBanner(bannerIndex, file, userIndex);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "배너를 수정하였습니다.",
                "data", updatedBanner
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "배너 수정에 실패했습니다: " + e.getMessage()
            ));
        }
    }
    
    @DeleteMapping("/{bannerIndex}")
    public ResponseEntity<?> deleteBanner(@PathVariable Integer bannerIndex) {
        try {
            bannerService.deleteBanner(bannerIndex);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "배너를 삭제하였습니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "배너 삭제에 실패했습니다."
            ));
        }
    }
    
    /**
     * 기존 배너들의 생성 시간 업데이트 (임시용)
     */
    @PostMapping("/update-create-times")
    public ResponseEntity<ResponseDTO<?>> updateBannerCreateTimes() {
        try {
            bannerService.updateBannerCreateTimes();
            return ResponseEntity.ok(ResponseDTO.createSuccessResponse("배너 생성 시간이 업데이트되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDTO.createErrorResponse(500, "배너 생성 시간 업데이트에 실패했습니다: " + e.getMessage()));
        }
    }
} 