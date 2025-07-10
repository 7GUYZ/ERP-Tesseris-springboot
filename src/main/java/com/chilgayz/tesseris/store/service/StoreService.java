package com.chilgayz.tesseris.store.service;

import org.springframework.stereotype.Service;

import com.chilgayz.tesseris.store.dto.StoreDto;
import com.chilgayz.tesseris.store.entity.Store;
import com.chilgayz.tesseris.store.entity.StoreCategory;
import com.chilgayz.tesseris.store.entity.StoreRequestStatus;
import com.chilgayz.tesseris.store.entity.User;
import com.chilgayz.tesseris.store.repository.StoreCategoryRepository;
import com.chilgayz.tesseris.store.repository.StoreRepository;
import com.chilgayz.tesseris.store.repository.StoreRequestStatusRepository;
import com.chilgayz.tesseris.store.repository.UserRepository;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor // 생성자 이걸로 만들어줌
@Service
public class StoreService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final StoreCategoryRepository storeCategoryRepository;
    private final StoreRequestStatusRepository storeRequestStatusRepository;

    // 전체 리스트 조회
     public List<StoreDto> getStoreDtos(String userId,String userName,String storeName) {
        List<Store> stores = storeRepository.findAll();

        return stores.stream()
            .filter(store -> {
                // user 조건 검사
                Optional<User> userOpt = userRepository.findById(store.getUserIndex());
                if (userId != null && !userId.isBlank()) {
                    if (userOpt.isEmpty() || !userOpt.get().getUserId().contains(userId)) {
                        return false;
                    }
                }
                if (userName != null && !userName.isBlank()) {
                    if (userOpt.isEmpty() || !userOpt.get().getUserName().contains(userName)) {
                        return false;
                    }
                }

                // storeName 조건 검사
                if (storeName != null && !storeName.isBlank()) {
                    if (!store.getStoreName().contains(storeName)) {
                        return false;
                    }
                }

                return true; // 모든 조건 통과
            })
            .map(store -> {
                User user = userRepository.findById(store.getUserIndex()).orElse(null);
                StoreCategory category = storeCategoryRepository.findById(store.getStoreCategoryIndex()).orElse(null);
                StoreRequestStatus status = storeRequestStatusRepository.findById(store.getStoreRequestStatusIndex()).orElse(null);

                return StoreDto.builder()
                        .userId(user != null ? user.getUserId() : null)
                        .userName(user != null ? user.getUserName() : null)
                        .userPhone(user != null ? user.getUserPhone() : null)

                        .storeCorporateName(store.getStoreCorporateName())
                        .storeName(store.getStoreName())
                        .storeTransactionStatus(
                            store.getStoreTransactionStatus() != null && store.getStoreTransactionStatus() ? "정상" : "정지")
                        .storeRegistrationNum(store.getStoreRegistrationNum())
                        .storePhone(store.getStorePhone())
                        .storeTypeTaxation(store.getStoreTypeTaxation())

                        .storeCategoryName(category != null ? category.getStoreCategoryName() : null)
                        .storeRequestStatusName(status != null ? status.getStoreRequestStatusName() : null)
                        .build();
            })
            .collect(Collectors.toList());
    }

}
