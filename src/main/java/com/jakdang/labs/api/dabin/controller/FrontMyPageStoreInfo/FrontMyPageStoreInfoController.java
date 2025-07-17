package com.jakdang.labs.api.dabin.controller.FrontMyPageStoreInfo;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.jakdang.labs.api.auth.dto.CustomUserDetails;
import com.jakdang.labs.api.dabin.dto.FrontMyPageStoreInfo.StoreInfoResponse;
import com.jakdang.labs.api.dabin.service.FrontMyPageStoreInfo.FrontMyPageStoreInfoService;

@RestController
@RequestMapping("/api/store")
public class FrontMyPageStoreInfoController {
    @Autowired
    private FrontMyPageStoreInfoService storeService;

    @GetMapping("/my")
    public StoreInfoResponse getMyStoreInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String userId = userDetails.getUserId();
        return storeService.getStoreInfoByUserId(userId);
    }
} 