package com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.controller;




import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.jakdang.labs.api.auth.dto.CustomUserDetails;
import com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.dto.StoreInfoResponseDto;
import com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.service.FrontMyPageStoreInfoService;

import lombok.RequiredArgsConstructor;



@RestController
@RequestMapping("/api/store")
 @RequiredArgsConstructor
public class FrontMyPageStoreInfoController {
    private final FrontMyPageStoreInfoService storeService;

    @GetMapping("/my")
    public StoreInfoResponseDto getMyStoreInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String userId = userDetails.getUserId();
        userId = "store1";
        return storeService.getStoreInfoByUserId(userId);
    }
} 