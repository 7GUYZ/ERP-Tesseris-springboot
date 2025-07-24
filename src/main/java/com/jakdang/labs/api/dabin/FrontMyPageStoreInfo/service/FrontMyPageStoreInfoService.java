package com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.service;



import org.springframework.stereotype.Service;


import com.jakdang.labs.entity.Store;
import com.jakdang.labs.entity.StoreCategory;
import com.jakdang.labs.entity.UserTesseris;

import lombok.RequiredArgsConstructor;

import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.api.auth.repository.UserRepository;
import com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.dto.StoreInfoResponseDto;
import com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.repository.FrontMyPageStoreInfoJdbRepo;
import com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.repository.StoreCategoryJdbRepo;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.UserTesserisRepository;

@Service
@RequiredArgsConstructor
public class FrontMyPageStoreInfoService {

    private final FrontMyPageStoreInfoJdbRepo storeRepository;
    private final StoreCategoryJdbRepo storeCategoryRepository;
    private final UserRepository userRepository;
    private final UserTesserisRepository userTesserisRepository;

    public StoreInfoResponseDto getStoreInfoByUserIndex(Integer userIndex) {
        Store store = storeRepository.findFirstByUserIndex(userIndex)
            .orElseThrow(() -> new RuntimeException("Store not found"));
        StoreCategory category = storeCategoryRepository.findById(store.getStoreCategory().getStoreCategoryIndex())
            .orElseThrow(() -> new RuntimeException("Category not found"));
        
        // 사업자 사용자 ID 조회
        String businessUserId = storeRepository.findBusinessUserIdByUserIndex(store.getBusinessManUserIndex())
            .orElse("Unknown"); // 사업자 정보가 없는 경우 기본값

        return new StoreInfoResponseDto(
            store.getStoreIndex(),
            store.getStoreName(),
            category.getStoreCategoryName(),
            store.getStoreAddress(),
            store.getStorePhone(),
            store.getStoreSite(),
            store.getStoreZoneCode(),
            store.getStoreDetailAddress(),
            store.getStoreMemo(),
            businessUserId // 사업자 사용자 ID
        );
    }

    public StoreInfoResponseDto getStoreInfoByUserId(String userId) {
        UserEntity userEntity = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("UserEntity not found for userId: " + userId));
        UserTesseris userTesseris = userTesserisRepository.findByUsersId(userEntity)
            .orElseThrow(() -> new RuntimeException("UserTesseris not found for userId: " + userId));
        return getStoreInfoByUserIndex(userTesseris.getUserIndex());
    }
}