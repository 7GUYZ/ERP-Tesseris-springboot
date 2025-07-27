package com.jakdang.labs.api.taekjun.storelist.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.taekjun.storelist.dto.StoreCategoryDTO;
import com.jakdang.labs.api.taekjun.storelist.dto.StoreListDTO;
import com.jakdang.labs.api.taekjun.storelist.service.StoreListService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/storelist")
public class StoreListController {
    private final StoreListService storeService;

    @GetMapping
    public ResponseEntity<ResponseDTO<List<StoreCategoryDTO>>> getStoreCategories(){
        return ResponseEntity.ok().body(storeService.getStoreCategories());
    }

    @GetMapping("/filtered")
    public ResponseEntity<ResponseDTO<List<StoreListDTO>>> getFilteredStoreList(
                        @RequestParam(value = "store_category_index", required = false) Integer store_category_index){
        // 파라미터가 null이면 0으로 설정 (전체 카테고리)
        if (store_category_index == null) {
            store_category_index = 0;
        }
        return ResponseEntity.ok().body(storeService.getFilteredStoreList(store_category_index));
    }

    @GetMapping("/detail")
    public ResponseEntity<ResponseDTO<?>> getStoreDetail(@RequestParam("store_index") Integer store_index){
        return ResponseEntity.ok().body(storeService.getStoreDetail(store_index));
    }
} 