package com.jakdang.labs.api.deokkyu.store.service;

import org.springframework.stereotype.Service;

import com.jakdang.labs.api.deokkyu.store.dto.CusStoreListDto;
import com.jakdang.labs.api.deokkyu.store.dto.StoreCustomerDto;
import com.jakdang.labs.api.deokkyu.store.dto.StoreListDto;
import com.jakdang.labs.api.deokkyu.store.dto.StoreListSearchDto;
import com.jakdang.labs.api.entity.BusinessGrade;
import com.jakdang.labs.api.entity.BusinessMan;
import com.jakdang.labs.api.entity.Store;
import com.jakdang.labs.api.entity.StoreCategory;
import com.jakdang.labs.api.entity.StoreCustomer;
import com.jakdang.labs.api.entity.StoreRequestStatus;
import com.jakdang.labs.api.entity.User;
import com.jakdang.labs.api.entity.UserCm;
import com.jakdang.labs.api.entity.UserTesseris;
import com.jakdang.labs.api.deokkyu.store.repository.StoreCategoryRepository;
import com.jakdang.labs.api.deokkyu.store.repository.StoreRepository;
import com.jakdang.labs.api.deokkyu.store.repository.StoreRequestStatusRepository;
import com.jakdang.labs.api.deokkyu.store.repository.UserCmRepository;
import com.jakdang.labs.api.deokkyu.store.repository.BusinessManRepository;
import com.jakdang.labs.api.deokkyu.store.repository.DKUserRepository;
import com.jakdang.labs.api.deokkyu.store.repository.BusinessGradeRepository;
import com.jakdang.labs.api.deokkyu.store.repository.StoreCustomerRepository;
import com.jakdang.labs.api.deokkyu.store.repository.UserTesserisRepository;
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
    private final DKUserRepository userRepository;
    private final StoreCategoryRepository storeCategoryRepository;
    private final StoreRequestStatusRepository storeRequestStatusRepository;
    private final UserCmRepository userCmRepository;
    private final BusinessManRepository businessManRepository;
    private final BusinessGradeRepository businessGradeRepository;
    private final StoreCustomerRepository storeCustomerRepository;
    private final UserTesserisRepository userTesserisRepository;

    // 전체 리스트 조회
     public List<StoreListDto> getStoreDtos(StoreListSearchDto filter) {
        List<Store> stores = storeRepository.findAll();
        

        return stores.stream()
            // 검색 조건 검사 
            .filter(store -> {
                Optional<UserTesseris> userTesserisOpt = userTesserisRepository.findByUserIndex(store.getUserIndex());
                String userId = userTesserisOpt.map(UserTesseris::getUsersId).orElse(null);
                User user = (store.getUserIndex() != null) ? userRepository.findById(store.getUserIndex()).orElse(null) : null;
                Optional<StoreRequestStatus> requestId = storeRequestStatusRepository.findById(store.getStoreRequestStatusIndex());

                if (filter.getUserId() != null && !filter.getUserId().isBlank()) {
                    if (userId == null || !userId.contains(filter.getUserId())) {
                        return false;
                    }
                }
                if (filter.getUserName() != null && !filter.getUserName().isBlank()) {
                    if (user == null || user.getName() == null || !user.getName().contains(filter.getUserName())) {
                        return false;
                    }
                }
                if (filter.getUserPhone() != null && !filter.getUserPhone().isBlank()) {
                    if (user == null || user.getPhone() == null || !user.getPhone().contains(filter.getUserPhone())) {
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
                    if (businessUserOpt.isEmpty() || businessUserOpt.get().getName() == null ||
                    !businessUserOpt.get().getName().contains(filter.getBusinessUserName())) {
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
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                Optional<UserTesseris> userTesserisOpt = userTesserisRepository.findByUserIndex(store.getUserIndex());
                String userId = userTesserisOpt.map(UserTesseris::getUsersId).orElse(null);
                User user = (store.getUserIndex() != null) ? userRepository.findById(store.getUserIndex()).orElse(null) : null;
                StoreCategory category = storeCategoryRepository.findById(store.getStoreCategoryIndex()).orElse(null);
                StoreRequestStatus status = storeRequestStatusRepository.findById(store.getStoreRequestStatusIndex()).orElse(null);
                UserCm userCm= userCmRepository.findById(store.getUserIndex()).orElse(null);
                User businessManUser = userRepository.findById(store.getBusinessManUserIndex()).orElse(null);
                String businessManUserId = null;
                if (businessManUser != null) {
                    Optional<UserTesseris> businessManTesserisOpt = userTesserisRepository.findByUserIndex(businessManUser.getId());
                    businessManUserId = businessManTesserisOpt.map(UserTesseris::getUsersId).orElse(null);
                }
                BusinessMan businessMan = businessManRepository.findById(store.getBusinessManUserIndex()).orElse(null);
                BusinessGrade businessGrade = null;
                if (businessMan != null && businessMan.getBusinessGradeIndex() != null) { // 가맹점에 사업자가 있으면 조회
                    businessGrade = businessGradeRepository.findById(businessMan.getBusinessGradeIndex()).orElse(null);
                }

                return StoreListDto.builder()
                        .userId(userId)
                        .userName(user != null ? user.getName() : null)
                        .userPhone(user != null ? user.getPhone() : null)

                        .storeBossName(store.getStoreBossName())
                        .storeRegistrationNum(store.getStoreRegistrationNum())
                        .storeTypeTaxation(store.getStoreTypeTaxation())
                        .storeCorporateName(store.getStoreCorporateName())
                        .storeName(store.getStoreName())
                        .storeTransactionStatus(
                            store.getStoreTransactionStatus() != null && store.getStoreTransactionStatus() ? "정상" : "정지")
                        .storePhone(store.getStorePhone())
                        .storeCreateDate(store.getStoreCreateDate() != null ? store.getStoreCreateDate().format(formatter) : null)

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
                        .businessUserId(businessManUserId)
                        .businessUserName(businessManUser != null ? businessManUser.getName() : null)
                        
                        .build();
            })
            .collect(Collectors.toList());
    }

     public List<CusStoreListDto> getStoresInCustomerList(StoreListSearchDto filter) {
        List<Store> stores = storeRepository.findAll();

        return stores.stream()
            .filter(store -> {
                Optional<UserTesseris> userTesserisOpt = userTesserisRepository.findByUserIndex(store.getUserIndex());
                String userId = userTesserisOpt.map(UserTesseris::getUsersId).orElse(null);
                User user = (store.getUserIndex() != null) ? userRepository.findById(store.getUserIndex()).orElse(null) : null;
                if (user == null) return false;
                if (filter.getUserId() != null && !filter.getUserId().isBlank() && (userId == null || !userId.contains(filter.getUserId()))) return false;
                if (filter.getUserName() != null && !filter.getUserName().isBlank() && (user.getName() == null || !user.getName().contains(filter.getUserName()))) return false;
                if (filter.getStoreCorporateName() != null && !filter.getStoreCorporateName().isBlank() && (store.getStoreCorporateName() == null || !store.getStoreCorporateName().contains(filter.getStoreCorporateName()))) return false;
                if (filter.getStoreName() != null && !filter.getStoreName().isBlank() && (store.getStoreName() == null || !store.getStoreName().contains(filter.getStoreName()))) return false;
                return true;
            })
            .map(store -> {
                List<StoreCustomer> customers = storeCustomerRepository.findByStoreStoreUserIndex(store.getUserIndex());
                List<StoreCustomerDto> customerList = customers.stream()
                    .map(sc -> {
                        Optional<UserTesseris> customerTesserisOpt = userTesserisRepository.findByUserIndex(sc.getStoreCustomerUserIndex());
                        String customerUserId = customerTesserisOpt.map(UserTesseris::getUsersId).orElse(null);
                        User customerUser = (sc.getStoreCustomerUserIndex() != null) ? userRepository.findById(sc.getStoreCustomerUserIndex()).orElse(null) : null;
                        return StoreCustomerDto.builder()
                            .userId(customerUserId)
                            .userName(customerUser != null ? customerUser.getName() : null)
                            .storeCustomerStatus(sc.getStoreCustomerStatus())
                            .build();
                    })
                    .collect(Collectors.toList());

                Optional<UserTesseris> storeUserTesserisOpt = userTesserisRepository.findByUserIndex(store.getUserIndex());
                String storeUserId = storeUserTesserisOpt.map(UserTesseris::getUsersId).orElse(null);
                User storeUser = (store.getUserIndex() != null) ? userRepository.findById(store.getUserIndex()).orElse(null) : null;
                return CusStoreListDto.builder()
                    .userId(storeUserId)
                    .userName(storeUser != null ? storeUser.getName() : null)
                    .storeCorporateName(store.getStoreCorporateName())
                    .storeName(store.getStoreName())
                    .customerCount(customerList.size())
                    .build();
            })
            .collect(Collectors.toList());
    }
}
