package com.jakdang.labs.api.taekjun.adminmypage.controller;

import com.jakdang.labs.api.taekjun.adminmypage.dto.AdminMyPageResponseDto;
import com.jakdang.labs.api.taekjun.adminmypage.dto.PasswordChangeRequestDto;
import com.jakdang.labs.api.taekjun.adminmypage.service.AdminMyPageService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/mypage")
public class AdminMyPageController {

    private final AdminMyPageService adminMyPageService;
    
    @GetMapping( "/getmypage")
    public ResponseEntity<AdminMyPageResponseDto> getMyInfo(@RequestParam String userIndex) {
        log.info("어드민 마이페이지 요청 - userIndex: {}", userIndex);
        
        if (userIndex == null || userIndex.trim().isEmpty()) {
            log.warn("userIndex 파라미터가 누락되었습니다");
            return ResponseEntity.badRequest().build();
        }
        
        try {
            AdminMyPageResponseDto result = adminMyPageService.getAdminMyPageInfo(userIndex);
            if (result == null) {
                log.warn("어드민 마이페이지 정보를 찾을 수 없습니다 - userIndex: {}", userIndex);
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("어드민 마이페이지 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/changemyinpo")
    public ResponseEntity<String> changeMyInfo(@RequestBody AdminMyPageResponseDto data, @RequestParam String userIndex) {
        log.info("어드민 마이페이지 정보 변경 요청 - userIndex: {}, data: {}", userIndex, data);
        
        if (userIndex == null || userIndex.trim().isEmpty()) {
            log.warn("userIndex 파라미터가 누락되었습니다");
            return ResponseEntity.badRequest().body("userIndex 파라미터가 필요합니다.");
        }
        
        if (data == null) {
            log.warn("요청 데이터가 누락되었습니다");
            return ResponseEntity.badRequest().body("요청 데이터가 필요합니다.");
        }
        
        try {
            boolean success = adminMyPageService.updateAdminMyPageInfo(userIndex, data);
            if (success) {
                return ResponseEntity.ok("정보가 성공적으로 변경되었습니다.");
            } else {
                return ResponseEntity.badRequest().body("정보 변경에 실패했습니다.");
            }
        } catch (Exception e) {
            log.error("어드민 마이페이지 정보 변경 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("정보 변경 중 오류가 발생했습니다.");
        }
    }
    
    @PostMapping("/changepassword")
    public ResponseEntity<String> changePassword(@RequestBody PasswordChangeRequestDto passwordChangeRequest, 
                                               @RequestParam String userIndex) {
        log.info("패스워드 변경 요청 - userIndex: {}", userIndex);
        
        if (userIndex == null || userIndex.trim().isEmpty()) {
            log.warn("userIndex 파라미터가 누락되었습니다");
            return ResponseEntity.badRequest().body("userIndex 파라미터가 필요합니다.");
        }
        
        if (passwordChangeRequest == null) {
            log.warn("패스워드 변경 요청 데이터가 누락되었습니다");
            return ResponseEntity.badRequest().body("패스워드 변경 데이터가 필요합니다.");
        }
        
        // 필수 필드 검증
        if (passwordChangeRequest.getCurrentPassword() == null || passwordChangeRequest.getCurrentPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("현재 패스워드가 필요합니다.");
        }
        
        if (passwordChangeRequest.getNewPassword() == null || passwordChangeRequest.getNewPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("새 패스워드가 필요합니다.");
        }
        
        if (passwordChangeRequest.getConfirmPassword() == null || passwordChangeRequest.getConfirmPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("패스워드 확인이 필요합니다.");
        }
        
        try {
            boolean success = adminMyPageService.changePassword(userIndex, passwordChangeRequest);
            if (success) {
                return ResponseEntity.ok("패스워드가 성공적으로 변경되었습니다.");
            } else {
                return ResponseEntity.badRequest().body("패스워드 변경에 실패했습니다. 현재 패스워드를 확인하거나 새 패스워드 조건을 확인해주세요.");
            }
        } catch (Exception e) {
            log.error("패스워드 변경 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("패스워드 변경 중 오류가 발생했습니다.");
        }
    }
} 