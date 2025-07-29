package com.jakdang.labs.api.jungeun.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.service.S3ImageService;
import com.jakdang.labs.api.jungeun.dto.UserStoreCategoryDTO;
import com.jakdang.labs.api.jungeun.dto.UserStoreListDTO;
import com.jakdang.labs.api.jungeun.service.UserStoreListSvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/user/storeList")
public class UserStoreListController {
    private final UserStoreListSvc storeSvc;
    private final S3ImageService s3ImageService;

    @GetMapping
    public ResponseEntity<ResponseDTO<List<UserStoreCategoryDTO>>> getStoreCategories() {
        return ResponseEntity.ok().body(storeSvc.getStoreCategories());
    }

    @GetMapping("/filtered")
    public ResponseEntity<ResponseDTO<List<UserStoreListDTO>>> getFilteredStoreList(
            @RequestParam("user_index") Integer user_index,
            @RequestParam("store_category_index") Integer store_category_index) {
        return ResponseEntity.ok().body(storeSvc.getFilteredStoreList(user_index, store_category_index));
    }

    @GetMapping("/detail")
    public ResponseEntity<ResponseDTO<?>> getStoreDetail(@RequestParam("store_index") Integer store_index) {
        return ResponseEntity.ok().body(storeSvc.getStoreDetail(store_index));
    }

    // 파일 조회 엔드포인트
    @GetMapping("/file/{fileKey}")
    public ResponseEntity<String> getFileUrl(@PathVariable String fileKey) {
        String presignedUrl = s3ImageService.generatePresignedUrl(fileKey);
        return ResponseEntity.ok(presignedUrl);
    }
}
