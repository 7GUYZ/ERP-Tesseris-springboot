package com.jakdang.labs.api.dabin.service.FrontMyPageStoreInfo;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jakdang.labs.api.dabin.dto.FrontMyPageStoreInfo.StoreInfoResponse;
import com.jakdang.labs.api.dabin.repository.FrontMyPageStoreInfo.FrontMyPageStoreInfoJdbRepo;
import com.jakdang.labs.api.dabin.repository.FrontMyPageStoreInfo.StoreCategoryJdbRepo;
import com.jakdang.labs.entity.Store;
import com.jakdang.labs.entity.StoreCategory;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.api.auth.repository.UserRepository;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.UserTesserisRepository;

@Service
public class FrontMyPageStoreInfoService {
    @Autowired
    private FrontMyPageStoreInfoJdbRepo storeRepository;
    @Autowired
    private StoreCategoryJdbRepo storeCategoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserTesserisRepository userTesserisRepository;

    public StoreInfoResponse getStoreInfoByUserIndex(Integer userIndex) {
        Store store = storeRepository.findByUserIndex(userIndex)
            .orElseThrow(() -> new RuntimeException("Store not found"));
        StoreCategory category = storeCategoryRepository.findById(store.getStoreCategory().getStoreCategoryIndex())
            .orElseThrow(() -> new RuntimeException("Category not found"));
        UserTesseris user = store.getUserIndex();

        return new StoreInfoResponse(
            store.getStoreName(),
            category.getStoreCategoryName(),
            store.getStoreAddress(),
            store.getStorePhone(),
            store.getStoreSite(),
            user.getUsersId().getId() // 실제 로그인 계정의 id(UUID)
        );
    }

    public StoreInfoResponse getStoreInfoByUserId(String userId) {
        UserEntity userEntity = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("UserEntity not found for userId: " + userId));
        UserTesseris userTesseris = userTesserisRepository.findByUsersId(userEntity)
            .orElseThrow(() -> new RuntimeException("UserTesseris not found for userId: " + userId));
        return getStoreInfoByUserIndex(userTesseris.getUserIndex());
    }
}