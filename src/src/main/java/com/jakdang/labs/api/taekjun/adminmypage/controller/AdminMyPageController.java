package com.jakdang.labs.api.taekjun.adminmypage.controller;

import com.jakdang.labs.api.taekjun.adminmypage.dto.AdminMyPageResponseDto;
import com.jakdang.labs.api.taekjun.adminmypage.service.AdminMyPageService;
import com.jakdang.labs.entity.UserTesseris;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    public AdminMyPageResponseDto getMyInfo(@RequestParam String userIndex) {
        log.info("어드민 마이페이지 요청 - userIndex: {}", userIndex);
        return adminMyPageService.getAdminMyPageInfo(userIndex);
    }
    
    @Mapping("/changemyinpo")
    public String getMethodName(@RequestParam String userIndex) {
        log.info("어드민 마이페이지 요청 - userIndex: {}", userIndex);
        return new String();
    }
    


} 