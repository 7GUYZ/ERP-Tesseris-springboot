package com.jakdang.labs.api.deokkyu.store.service;

import org.springframework.stereotype.Service;

import com.jakdang.labs.api.deokkyu.store.dto.CusStoreListDto;
import com.jakdang.labs.api.deokkyu.store.dto.CustomerDto;
import com.jakdang.labs.api.deokkyu.store.dto.StoreListDto;
import com.jakdang.labs.api.deokkyu.store.dto.StoreListSearchDto;
import com.jakdang.labs.api.deokkyu.store.dto.StoreRegisterdListDto;
import com.jakdang.labs.entity.BusinessGrade;
import com.jakdang.labs.entity.BusinessMan;
import com.jakdang.labs.entity.Store;
import com.jakdang.labs.entity.StoreCategory;
import com.jakdang.labs.entity.StoreCustomer;
import com.jakdang.labs.entity.StoreRequestStatus;
import com.jakdang.labs.entity.UserCm;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.entity.StoreSubscriptionFee;
import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.api.deokkyu.store.repository.StoreCategoryhdkRepository;
import com.jakdang.labs.api.deokkyu.store.repository.StorehdkRepository;
import com.jakdang.labs.api.deokkyu.store.repository.StoreRequestStatushdkRepository;
import com.jakdang.labs.api.deokkyu.store.repository.UserCmhdkRepository;
import com.jakdang.labs.api.deokkyu.store.repository.BusinessManhdkRepository;
import com.jakdang.labs.api.deokkyu.store.repository.UserhdkRepository;
import com.jakdang.labs.api.deokkyu.store.repository.BusinessGradehdkRepository;
import com.jakdang.labs.api.deokkyu.store.repository.StoreCustomerhdkRepository;
import com.jakdang.labs.api.deokkyu.store.repository.UserTesserishdkRepository;
import com.jakdang.labs.api.deokkyu.store.repository.StoreSubscriptionFeehdkRepository;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor // 생성자 이걸로 만들어줌
@Service
public class StoreService {
    private final StorehdkRepository storeRepository;
    private final UserhdkRepository userRepository;
    private final StoreCategoryhdkRepository storeCategoryRepository;
    private final StoreRequestStatushdkRepository storeRequestStatusRepository;
    private final UserCmhdkRepository userCmRepository;
    private final BusinessManhdkRepository businessManRepository;
    private final BusinessGradehdkRepository businessGradeRepository;
    private final StoreCustomerhdkRepository storeCustomerRepository;
    private final UserTesserishdkRepository userTesserisRepository;
    private final StoreSubscriptionFeehdkRepository storeSubscriptionFeeRepository;

    // 전체 리스트 조회
     public List<StoreListDto> getStoreDtos(StoreListSearchDto filter) {
        List<Store> stores = storeRepository.findAll();
        

        return stores.stream()
            // 검색 조건 검사 
            .filter(store -> {
                UserTesseris userTesseris = store.getUserIndex();
                String userId = null;
                UserEntity user = null;
                
                if (userTesseris != null) {
                    userId = userTesseris.getUsersId().getId(); // UserEntity의 id
                    user = userTesseris.getUsersId(); // UserEntity 객체
                }
                
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
                    Optional<UserEntity> businessUserOpt = userRepository.findById(String.valueOf(store.getBusinessManUserIndex()));
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
                // Store.userIndex는 UserTesseris 객체
                UserTesseris userTesseris = store.getUserIndex();
                String userId = null;
                UserEntity user = null;
                
                if (userTesseris != null) {
                    userId = userTesseris.getUsersId().getId(); // UserEntity의 id
                    user = userTesseris.getUsersId(); // UserEntity 객체
                }
                
                StoreCategory category = store.getStoreCategory(); // Store에서 직접 가져오기
                StoreRequestStatus status = storeRequestStatusRepository.findById(store.getStoreRequestStatusIndex()).orElse(null);
                UserCm userCm= userCmRepository.findById(userTesseris != null ? userTesseris.getUserIndex() : null).orElse(null);

                // businessMan → userTesseris → usersId(UserEntity) 경로로 사업자 정보 조회
                BusinessMan businessMan = businessManRepository.findById(store.getBusinessManUserIndex()).orElse(null);
                String businessUserName = null;
                String businessUserId = null;
                String businessGradeName = null;
                if (businessMan != null) {
                    Optional<UserTesseris> businessUserTesserisOpt = userTesserisRepository.findByUserIndex(
                        businessMan.getUserIndex() != null ? businessMan.getUserIndex().getUserIndex() : null
                    );
                    if (businessUserTesserisOpt.isPresent()) {
                        UserTesseris businessUserTesseris = businessUserTesserisOpt.get();
                        UserEntity businessUserEntity = businessUserTesseris.getUsersId();
                        if (businessUserEntity != null) {
                            businessUserName = businessUserEntity.getName();
                            businessUserId = businessUserEntity.getId();
                        }
                    }
                    if (businessMan.getBusinessGrade() != null) {
                        businessGradeName = businessMan.getBusinessGrade().getBusinessGradeName();
                    }
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

                        .businessGradeName(businessGradeName)
                        .businessUserId(businessUserId)
                        .businessUserName(businessUserName)
                        
                        .build();
            })
            .collect(Collectors.toList());
    }

    // 전체 가맹점 + 고객 수만 반환
    public List<CusStoreListDto> getStoresInCustomerList(StoreListSearchDto filter) {
        List<Store> stores = storeRepository.findAll();

        return stores.stream()
            .filter(store -> {
                UserTesseris userTesseris = store.getUserIndex();
                String userId = null;
                UserEntity user = null;
                if (userTesseris != null) {
                    userId = userTesseris.getUsersId().getId();
                    user = userTesseris.getUsersId();
                }
                if (filter.getUserId() != null && !filter.getUserId().isBlank() && (userId == null || !userId.contains(filter.getUserId()))) return false;
                if (filter.getUserName() != null && !filter.getUserName().isBlank() && (user == null || user.getName() == null || !user.getName().contains(filter.getUserName()))) return false;
                if (filter.getStoreCorporateName() != null && !filter.getStoreCorporateName().isBlank() && (store.getStoreCorporateName() == null || !store.getStoreCorporateName().contains(filter.getStoreCorporateName()))) return false;
                if (filter.getStoreName() != null && !filter.getStoreName().isBlank() && (store.getStoreName() == null || !store.getStoreName().contains(filter.getStoreName()))) return false;
                return true;
            })
            .map(store -> {
                // store의 user_index를 String으로 변환
                String storeUserIndex = store.getUserIndex() != null ? String.valueOf(store.getUserIndex().getUserIndex()) : null;
                int customerCount = 0;
                if (storeUserIndex != null) {
                    customerCount = storeCustomerRepository.findByStoreStoreUserIndex(storeUserIndex).size();
                }
                UserTesseris userTesseris = store.getUserIndex();
                String storeUserId = null;
                UserEntity storeUser = null;
                if (userTesseris != null) {
                    storeUserId = userTesseris.getUsersId().getId();
                    storeUser = userTesseris.getUsersId();
                }
                return CusStoreListDto.builder()
                    .userId(storeUserId)
                    .userName(storeUser != null ? storeUser.getName() : null)
                    .storeCorporateName(store.getStoreCorporateName())
                    .storeName(store.getStoreName())
                    .customerCount(customerCount)
                    .build();
            })
            .collect(Collectors.toList());
    }


    // 선택한 가맹점의 고객 리스트 반환
    public List<CustomerDto> getStoreCustomerListByStoreId(String storeUsersId) {
        // 1. users_id(String)로 UserEntity 조회
        Optional<UserEntity> userEntityOpt = userRepository.findById(storeUsersId);
        if (userEntityOpt.isEmpty()) {
            return List.of();
        }
        UserEntity userEntity = userEntityOpt.get();

        // 2. UserEntity로 UserTesseris 리스트 조회
        List<UserTesseris> tesserisList = userTesserisRepository.findByUsersId(userEntity);
        List<Integer> userIndexList = tesserisList.stream()
            .map(UserTesseris::getUserIndex)
            .collect(Collectors.toList());

        // 3. store_customer에서 store_store_user_index in (userIndexList)
        List<StoreCustomer> customers = userIndexList.isEmpty() ? List.of() : storeCustomerRepository.findByStoreStoreUserIndexIn(userIndexList);

        return customers.stream()
            .map(sc -> {
                Optional<UserTesseris> userTesserisOpt = userTesserisRepository.findByUserIndex(Integer.parseInt(sc.getStoreCustomerUserIndex()));
                String userId = null;
                String userName = null;
                if (userTesserisOpt.isPresent()) {
                    UserEntity userEnt = userTesserisOpt.get().getUsersId();
                    if (userEnt != null) {
                        userId = userEnt.getId();
                        userName = userEnt.getName();
                    }
                }
                return CustomerDto.builder()
                    .userId(userId)
                    .userName(userName)
                    .storeCustomerStatus(sc.getStoreCustomerStatus())
                    .build();
            })
            .collect(Collectors.toList());
    }

 // 가맹점 신청 현황 페이지에서 가맹점 리스트(등록된 가맹점) 반환
    public List<StoreRegisterdListDto> getFilteredRegisterdStores(StoreListSearchDto filter) {
        // 1. store_subscription_fee에 존재하는 user_index만 추출
        List<StoreSubscriptionFee> feeList = storeSubscriptionFeeRepository.findAll();
        List<Integer> userIndexes = new ArrayList<>();
        for (StoreSubscriptionFee fee : feeList) {
            Store store = fee.getStoreUserIndex();
            if (store != null && store.getUserIndex() != null) {
                userIndexes.add(store.getUserIndex().getUserIndex());
            }
        }
        userIndexes = userIndexes.stream().distinct().collect(Collectors.toList());

        // 2. userIndexes로 UserTesseris 리스트 조회
        List<UserTesseris> tesserisList = userTesserisRepository.findAllById(userIndexes);

        // 3. tesserisList로 필요한 DTO 생성
        List<StoreRegisterdListDto> result = new ArrayList<>();
        List<Store> stores = storeRepository.findAll();
        for (UserTesseris userTesseris : tesserisList) {
            // filter 조건 체크 (getStoreDtos와 동일하게 적용)
            UserEntity user = userTesseris.getUsersId();
            String userId = user != null ? user.getId() : null;
            if (filter.getUserId() != null && !filter.getUserId().isBlank()) {
                if (userId == null || !userId.contains(filter.getUserId())) continue;
            }
            if (filter.getUserName() != null && !filter.getUserName().isBlank()) {
                if (user == null || user.getName() == null || !user.getName().contains(filter.getUserName())) continue;
            }
            if (filter.getUserPhone() != null && !filter.getUserPhone().isBlank()) {
                if (user == null || user.getPhone() == null || !user.getPhone().contains(filter.getUserPhone())) continue;
            }
            // 연결된 Store 찾기 (userIndex로)
            Store store = storeRepository.findByUserIndex(userTesseris);
            if (store == null) continue;
            if (filter.getStoreBossName() != null && !filter.getStoreBossName().isBlank()) {
                if (store.getStoreBossName() == null || !store.getStoreBossName().contains(filter.getStoreBossName())) continue;
            }
            if (filter.getStoreCorporateName() != null && !filter.getStoreCorporateName().isBlank()) {
                if (store.getStoreCorporateName() == null || !store.getStoreCorporateName().contains(filter.getStoreCorporateName())) continue;
            }
            if (filter.getStoreName() != null && !filter.getStoreName().isBlank()) {
                if (store.getStoreName() == null || !store.getStoreName().contains(filter.getStoreName())) continue;
            }
            // StoreSubscriptionFee 정보 (가장 최근 값 1개만)
            List<StoreSubscriptionFee> fees = storeSubscriptionFeeRepository.findByStoreUserIndex(store);
            StoreSubscriptionFee fee = null;
            if (!fees.isEmpty()) {
                fee = fees.stream().sorted((a, b) -> b.getStoreSubscriptionFeeTime().compareTo(a.getStoreSubscriptionFeeTime())).findFirst().orElse(null);
            }
            StoreRegisterdListDto dto = StoreRegisterdListDto.builder()
                .userId(userId)
                .userName(user != null ? user.getName() : null)
                .userPhone(user != null ? user.getPhone() : null)
                .storeName(store.getStoreName())
                .storeCreateDate(store.getStoreCreateDate() != null ? store.getStoreCreateDate().toLocalDate().toString() : null)
                .storeSubscriptionFeeValue(fee != null ? fee.getStoreSubscriptionFeeValue() : null)
                .franchiseFee(store.getFranchiseFee())
                .storeSubscriptionFeeCommissionCheck(fee != null ? fee.getStoreSubscriptionFeeCommissionCheck() : null)
                .build();
            result.add(dto);
        }
        return result;
    }

    

}
