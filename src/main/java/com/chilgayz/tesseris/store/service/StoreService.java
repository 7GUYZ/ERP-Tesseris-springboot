package com.chilgayz.tesseris.store.service;

import org.springframework.stereotype.Service;

import com.chilgayz.tesseris.store.dto.StoreListDto;
import com.chilgayz.tesseris.store.dto.StoreListSearchDto;
import com.chilgayz.tesseris.store.entity.BusinessGrade;
import com.chilgayz.tesseris.store.entity.BusinessMan;
import com.chilgayz.tesseris.store.entity.Store;
import com.chilgayz.tesseris.store.entity.StoreCategory;
import com.chilgayz.tesseris.store.entity.StoreRequestStatus;
import com.chilgayz.tesseris.store.entity.User;
import com.chilgayz.tesseris.store.entity.UserCm;
import com.chilgayz.tesseris.store.repository.StoreCategoryRepository;
import com.chilgayz.tesseris.store.repository.StoreRepository;
import com.chilgayz.tesseris.store.repository.StoreRequestStatusRepository;
import com.chilgayz.tesseris.store.repository.UserCmRepository;
import com.chilgayz.tesseris.store.repository.UserRepository;
import com.chilgayz.tesseris.store.repository.BusinessManRepository;
import com.chilgayz.tesseris.store.repository.BusinessGradeRepository;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor // 생성자 이걸로 만들어줌
@Service
public class StoreService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final StoreCategoryRepository storeCategoryRepository;
    private final StoreRequestStatusRepository storeRequestStatusRepository;
    private final UserCmRepository userCmRepository;
    private final BusinessManRepository businessManRepository;
    private final BusinessGradeRepository businessGradeRepository;

    // 전체 리스트 조회
     public List<StoreListDto> getStoreDtos(StoreListSearchDto filter) {
        List<Store> stores = storeRepository.findAll();

        

        return stores.stream()
            // 검색 조건 검사 
            .filter(store -> {
                // user 쪽 조건 검사
                Optional<User> userOpt = userRepository.findById(store.getUserIndex());
                Optional<StoreRequestStatus> requestId = storeRequestStatusRepository.findById(store.getStoreRequestStatusIndex());

                if (filter.getUserId() != null && !filter.getUserId().isBlank()) {
                    if (userOpt.isEmpty() || userOpt.get().getUserId() == null || 
                    !userOpt.get().getUserId().contains(filter.getUserId())) {
                        return false;
                    }
                }
                if (filter.getUserName() != null && !filter.getUserName().isBlank()) {
                    if (userOpt.isEmpty() || userOpt.get().getUserName() == null ||
                    !userOpt.get().getUserName().contains(filter.getUserName())) {
                        return false;
                    }
                }
                if (filter.getUserPhone() != null && !filter.getUserPhone().isBlank()) {
                    if (userOpt.isEmpty() || userOpt.get().getUserPhone() == null || 
                    !userOpt.get().getUserPhone().contains(filter.getUserPhone())) {
                        return false;
                    }
                }

                // store 쪽 조건 검사
               
                if (filter.getStoreBossName() != null && !filter.getStoreBossName().isBlank()) {
                    if (store.getStoreBossName() == null ||
                    !store.getStoreBossName().contains(filter.getStoreBossName())) {
                        return false;
                    }
                }
                if (filter.getStoreRequestStatusName() != null && !filter.getStoreRequestStatusName().isBlank()) {
                    if (requestId.isEmpty() || requestId.get().getStoreRequestStatusName() == null ||
                    !requestId.get().getStoreRequestStatusName().contains(filter.getStoreRequestStatusName())) {
                        return false;
                    }// StoreRequestStatus 로 접근후 비교
                }
                if (filter.getStoreTransactionStatus() != null && !filter.getStoreTransactionStatus().isBlank()) {
                    Boolean filterTransactionStatus = null;

                    if (filter.getStoreTransactionStatus().equals("정상")) {
                        filterTransactionStatus = true;
                    } else if (filter.getStoreTransactionStatus().equals("정지")) {
                        filterTransactionStatus = false;
                    } else {
                        // 예외 처리 or 무시
                        return false;
                    }

                    if (store.getStoreTransactionStatus() == null || 
                    !store.getStoreTransactionStatus().equals(filterTransactionStatus)) { // boolean으로 최종 비교
                        return false;
                    }
                }
                if (filter.getStoreCorporateName() != null && !filter.getStoreCorporateName().isBlank()) {
                    if (store.getStoreCorporateName() == null || 
                    !store.getStoreCorporateName().contains(filter.getStoreCorporateName())) {
                        return false;
                    }
                }
                if (filter.getStoreName() != null && !filter.getStoreName().isBlank()) {
                    if (store.getStoreName() == null || 
                    !store.getStoreName().contains(filter.getStoreName())) {
                        return false;
                    }
                }

                // 사업자 이름으로 비교
                if (filter.getBusinessUserName() != null && !filter.getBusinessUserName().isBlank()) {
                    Optional<User> businessUserOpt = userRepository.findById(store.getBusinessManUserIndex());
                    if (businessUserOpt.isEmpty() || businessUserOpt.get().getUserName() == null ||
                    !businessUserOpt.get().getUserName().contains(filter.getBusinessUserName())) {
                        return false;
                    }
                }

                // 날짜 처리
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                if (filter.getStoreCreateDateStart() != null && !filter.getStoreCreateDateStart().isBlank()) {
                    LocalDate startDate = LocalDate.parse(filter.getStoreCreateDateStart(), formatter);
                    if (store.getStoreCreateDate().toLocalDate().isBefore(startDate)) {
                        return false;
                    }
                }

                if (filter.getStoreCreateDateEnd() != null && !filter.getStoreCreateDateEnd().isBlank()) {
                    LocalDate endDate = LocalDate.parse(filter.getStoreCreateDateEnd(), formatter);
                    if (store.getStoreCreateDate().toLocalDate().isAfter(endDate)) {
                        return false;
                    }
                }


                return true; // 모든 조건 통과
            })
            .map(store -> {
                User user = userRepository.findById(store.getUserIndex()).orElse(null);
                StoreCategory category = storeCategoryRepository.findById(store.getStoreCategoryIndex()).orElse(null);
                StoreRequestStatus status = storeRequestStatusRepository.findById(store.getStoreRequestStatusIndex()).orElse(null);
                UserCm userCm= userCmRepository.findById(store.getUserIndex()).orElse(null);
                User businessManUser = userRepository.findById(store.getBusinessManUserIndex()).orElse(null);
                BusinessMan businessMan = businessManRepository.findById(store.getBusinessManUserIndex()).orElse(null);
                BusinessGrade businessGrade = null;
                if (businessMan != null && businessMan.getBusinessGradeIndex() != null) { // 가맹점에 사업자가 있으면 조회
                    businessGrade = businessGradeRepository.findById(businessMan.getBusinessGradeIndex()).orElse(null);
                }

                return StoreListDto.builder()
                        .userId(user != null ? user.getUserId() : null)
                        .userName(user != null ? user.getUserName() : null)
                        .userPhone(user != null ? user.getUserPhone() : null)

                        .storeBossName(store.getStoreBossName())
                        .storeRegistrationNum(store.getStoreRegistrationNum())
                        .storeTypeTaxation(store.getStoreTypeTaxation())
                        .storeCorporateName(store.getStoreCorporateName())
                        .storeName(store.getStoreName())
                        .storeTransactionStatus(
                            store.getStoreTransactionStatus() != null && store.getStoreTransactionStatus() ? "정상" : "정지")
                        .storePhone(store.getStorePhone())
                        .storeCreateDate(store.getStoreCreateDate())

                        .storeCategoryName(category != null ? category.getStoreCategoryName() : null)
                        .storeRequestStatusName(status != null ? status.getStoreRequestStatusName() : null)
                        
                        .totalCM(
                            (userCm != null ? Optional.ofNullable(userCm.getUserCmDeposit()).orElse(0) : 0) +
                            (userCm != null ? Optional.ofNullable(userCm.getUserCmWithdrawal()).orElse(0) : 0)
                        )
                        .userCmpInit(
                            userCm != null ? Optional.ofNullable(userCm.getUserCmpInit()).orElse(0) : 0
                        )


                        .businessGradeName(businessGrade != null ? businessGrade.getBusinessGradeName() : null)
                        .businessUserId(businessManUser != null ? businessManUser.getUserId() : null)
                        .businessUserName(businessManUser != null ? businessManUser.getUserName(): null)
                        
                        .build();
            })
            .collect(Collectors.toList());
    }

}
