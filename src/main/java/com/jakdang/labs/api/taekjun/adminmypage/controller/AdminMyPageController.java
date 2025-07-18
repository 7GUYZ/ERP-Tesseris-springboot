package com.jakdang.labs.api.taekjun.adminmypage.controller;

import com.jakdang.labs.api.taekjun.adminmypage.dto.AdminMyPageResponseDto;
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
    
    @GetMapping("/myinfo")
    public ResponseEntity<AdminMyPageResponseDto> getMyInfo(@RequestParam String userIndex) {
        log.info("어드민 마이페이지 요청 - userIndex: {}", userIndex);
        try {
            AdminMyPageResponseDto result = adminMyPageService.getAdminMyPageInfo(userIndex);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("어드민 마이페이지 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/changemyinpo")
    public ResponseEntity<String> changeMyInfo(@RequestBody AdminMyPageResponseDto data) {
        log.info("어드민 마이페이지 정보 변경 요청 - data: {}", data);
        try {
            // TODO: 실제 정보 변경 로직 구현
            return ResponseEntity.ok("정보가 성공적으로 변경되었습니다.");
        } catch (Exception e) {
            log.error("어드민 마이페이지 정보 변경 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("정보 변경 중 오류가 발생했습니다.");
        }
    }
} 