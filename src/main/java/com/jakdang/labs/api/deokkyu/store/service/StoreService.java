package com.jakdang.labs.api.deokkyu.store.service;

import org.springframework.stereotype.Service;

import com.jakdang.labs.api.deokkyu.modal_admin.dto.StoreTransactionHistoryDto;
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

import java.time.LocalDateTime;
import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.api.deokkyu.store.repository.StoreCategoryhdkRepo;
import com.jakdang.labs.api.deokkyu.store.repository.StorehdkRepo;
import com.jakdang.labs.api.deokkyu.store.repository.StoreRequestStatushdkRepo;
import com.jakdang.labs.api.deokkyu.store.repository.StoreSubscriptionFeehdkRepo;
import com.jakdang.labs.api.deokkyu.store.repository.UserCmhdkRepo;
import com.jakdang.labs.api.deokkyu.store.repository.BusinessManhdkRepo;
import com.jakdang.labs.api.deokkyu.store.repository.UserhdkRepo;
import com.jakdang.labs.api.deokkyu.store.repository.BusinessGradehdkRepo;
import com.jakdang.labs.api.deokkyu.store.repository.StoreCustomerhdkRepo;
import com.jakdang.labs.api.deokkyu.store.repository.UserTesserishdkRepo;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor // 생성자 이걸로 만들어줌
@Slf4j
@Service
public class StoreService {
    private final StorehdkRepo storeRepository;
    private final UserhdkRepo userRepository;
    private final StoreCategoryhdkRepo storeCategoryRepository;
    private final StoreRequestStatushdkRepo storeRequestStatusRepository;
    private final UserCmhdkRepo userCmRepository;
    private final BusinessManhdkRepo businessManRepository;
    private final BusinessGradehdkRepo businessGradeRepository;
    private final StoreCustomerhdkRepo storeCustomerRepository;
    private final UserTesserishdkRepo userTesserisRepository;
    private final StoreSubscriptionFeehdkRepo storeSubscriptionFeeRepository;


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
                if (filter.getStoreRequestStatusName() != null && !filter.getStoreRequestStatusName().isBlank() && !filter.getStoreRequestStatusName().equals("전체")) {
                    if (requestId.isEmpty() || requestId.get().getStoreRequestStatusName() == null ||
                    !requestId.get().getStoreRequestStatusName().contains(filter.getStoreRequestStatusName())) {
                        return false;
                    }// StoreRequestStatus 로 접근후 비교
                }
                if (filter.getStoreTransactionStatus() != null && !filter.getStoreTransactionStatus().isBlank() && !filter.getStoreTransactionStatus().equals("전체")) {
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
                String userName = null;
                String userPhone = null;
                if (userTesseris != null && userTesseris.getUsersId() != null) {
                    userId = userTesseris.getUsersId().getId();
                    userName = userTesseris.getUsersId().getName();
                    userPhone = userTesseris.getUsersId().getPhone();
                }

                StoreCategory category = store.getStoreCategory();
                String storeCategoryName = null;
                if (category != null) {
                    storeCategoryName = category.getStoreCategoryName();
                }

                StoreRequestStatus status = null;
                String storeRequestStatusName = null;
                if (store.getStoreRequestStatusIndex() != null) {
                    status = storeRequestStatusRepository.findById(store.getStoreRequestStatusIndex()).orElse(null);
                    if (status != null) {
                        storeRequestStatusName = status.getStoreRequestStatusName();
                    }
                }

                UserCm userCm = null;
                Integer totalCM = 0;
                Integer userCmpInit = 0;
                if (userTesseris != null && userTesseris.getUserIndex() != null) {
                    userCm = userCmRepository.findById(userTesseris.getUserIndex()).orElse(null);
                    if (userCm != null) {
                        Integer deposit = userCm.getUserCmDeposit() != null ? userCm.getUserCmDeposit() : 0;
                        Integer withdrawal = userCm.getUserCmWithdrawal() != null ? userCm.getUserCmWithdrawal() : 0;
                        totalCM = deposit + withdrawal;
                        userCmpInit = userCm.getUserCmpInit() != null ? userCm.getUserCmpInit() : 0;
                    }
                }

                // businessMan → userTesseris → usersId(UserEntity) 경로로 사업자 정보 조회
                BusinessMan businessMan = null;
                String businessUserName = null;
                String businessUserId = null;
                String businessGradeName = null;
                if (store.getBusinessManUserIndex() != null) {
                    businessMan = businessManRepository.findById(store.getBusinessManUserIndex()).orElse(null);
                    if (businessMan != null) {
                        // businessMan의 userIndex로 UserTesseris 조회
                        Integer businessUserIndex = null;
                        if (businessMan.getUserIndex() != null) {
                            businessUserIndex = businessMan.getUserIndex().getUserIndex();
                        }
                        if (businessUserIndex != null) {
                            UserTesseris businessUserTesseris = userTesserisRepository.findByUserIndex(businessUserIndex).orElse(null);
                            if (businessUserTesseris != null && businessUserTesseris.getUsersId() != null) {
                                businessUserName = businessUserTesseris.getUsersId().getName();
                                businessUserId = businessUserTesseris.getUsersId().getId();
                            }
                        }
                        if (businessMan.getBusinessGrade() != null) {
                            businessGradeName = businessMan.getBusinessGrade().getBusinessGradeName();
                        }
                    }
                }

                String storeTransactionStatus = "정지";
                if (store.getStoreTransactionStatus() != null && store.getStoreTransactionStatus()) {
                    storeTransactionStatus = "정상";
                }

                String storeCreateDate = null;
                if (store.getStoreCreateDate() != null) {
                    storeCreateDate = store.getStoreCreateDate().format(formatter);
                }

                return StoreListDto.builder()
                        .userId(userId)
                        .userName(userName)
                        .userPhone(userPhone)
                        .storeBossName(store.getStoreBossName())
                        .storeRegistrationNum(store.getStoreRegistrationNum())
                        .storeTypeTaxation(store.getStoreTypeTaxation())
                        .storeCorporateName(store.getStoreCorporateName())
                        .storeName(store.getStoreName())
                        .storeTransactionStatus(storeTransactionStatus)
                        .storePhone(store.getStorePhone())
                        .storeCreateDate(storeCreateDate)
                        .storeCategoryName(storeCategoryName)
                        .storeRequestStatusName(storeRequestStatusName)
                        .totalCM(totalCM)
                        .userCmpInit(userCmpInit)
                        .businessGradeName(businessGradeName)
                        .businessUserId(businessUserId)
                        .businessUserName(businessUserName)
                        .build();
            })
            .sorted((dto1, dto2) -> {
                // storeCreateDate가 최근인 것이 위로 오도록 내림차순 정렬
                if (dto1.getStoreCreateDate() == null && dto2.getStoreCreateDate() == null) {
                    return 0;
                }
                if (dto1.getStoreCreateDate() == null) {
                    return 1; // null은 뒤로
                }
                if (dto2.getStoreCreateDate() == null) {
                    return -1; // null은 뒤로
                }
                return dto2.getStoreCreateDate().compareTo(dto1.getStoreCreateDate()); // 내림차순
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
        // 1. store_subscription_fee 테이블의 모든 데이터 조회
        List<StoreSubscriptionFee> feeList = storeSubscriptionFeeRepository.findAll();
        
        // 2. 각 StoreSubscriptionFee로부터 DTO 생성
        List<StoreRegisterdListDto> result = new ArrayList<>();
        
        for (StoreSubscriptionFee fee : feeList) {
            // ✅ StoreSubscriptionFee에서 Store와 BusinessMan 직접 가져오기
            Store store = fee.getStoreUserIndex();  // store_user_index → Store
            BusinessMan businessMan = fee.getBusinessManUserIndex();  // business_man_user_index → BusinessMan
            
            if (store == null) continue;
            
            // ✅ Store에서 가맹점 사용자 정보 가져오기 (Store → UserTesseris → UserEntity)
            UserTesseris storeUserTesseris = store.getUserIndex();
            UserEntity storeUser = storeUserTesseris != null ? storeUserTesseris.getUsersId() : null;
            String userId = storeUser != null ? storeUser.getId() : null;
            
            // ✅ BusinessMan에서 사업자 정보 가져오기 (BusinessMan → UserTesseris → UserEntity)
            UserTesseris businessUserTesseris = businessMan != null ? businessMan.getUserIndex() : null;
            UserEntity businessUser = businessUserTesseris != null ? businessUserTesseris.getUsersId() : null;
            String businessUserId = businessUser != null ? businessUser.getId() : null;
            String businessUserName = businessUser != null ? businessUser.getName() : null;
            String businessGradeName = businessMan != null && businessMan.getBusinessGrade() != null ? 
                                     businessMan.getBusinessGrade().getBusinessGradeName() : null;
            
            // filter 조건 체크
            if (filter.getUserId() != null && !filter.getUserId().isBlank()) {
                if (userId == null || !userId.contains(filter.getUserId())) continue;
            }
            if (filter.getUserName() != null && !filter.getUserName().isBlank()) {
                if (storeUser == null || storeUser.getName() == null || !storeUser.getName().contains(filter.getUserName())) continue;
            }
            if (filter.getUserPhone() != null && !filter.getUserPhone().isBlank()) {
                if (storeUser == null || storeUser.getPhone() == null || !storeUser.getPhone().contains(filter.getUserPhone())) continue;
            }
            if (filter.getStoreBossName() != null && !filter.getStoreBossName().isBlank()) {
                if (store.getStoreBossName() == null || !store.getStoreBossName().contains(filter.getStoreBossName())) continue;
            }
            if (filter.getStoreCorporateName() != null && !filter.getStoreCorporateName().isBlank()) {
                if (store.getStoreCorporateName() == null || !store.getStoreCorporateName().contains(filter.getStoreCorporateName())) continue;
            }
            if (filter.getStoreName() != null && !filter.getStoreName().isBlank()) {
                if (store.getStoreName() == null || !store.getStoreName().contains(filter.getStoreName())) continue;
            }
            
            // ✅ storeRequestStatusName 필터링 추가 (직접 매핑)
            if (filter.getStoreRequestStatusName() != null && !filter.getStoreRequestStatusName().isBlank() && !filter.getStoreRequestStatusName().equals("전체")) {
                String currentStatusName = "대기";  // 기본값
                if (store.getStoreRequestStatusIndex() != null) {
                    switch (store.getStoreRequestStatusIndex()) {
                        case 1:
                            currentStatusName = "대기";
                            break;
                        case 2:
                            currentStatusName = "승인";
                            break;
                        case 3:
                            currentStatusName = "거절";
                            break;
                        default:
                            currentStatusName = "알 수 없음";
                            break;
                    }
                }
                
                if (!currentStatusName.contains(filter.getStoreRequestStatusName())) {
                    continue;
                }
            }
            
            // storeTransactionStatus 필터링 추가
            if (filter.getStoreTransactionStatus() != null && !filter.getStoreTransactionStatus().isBlank() && !filter.getStoreTransactionStatus().equals("전체")) {
                Boolean filterTransactionStatus = null;

                if (filter.getStoreTransactionStatus().equals("정상")) {
                    filterTransactionStatus = true;
                } else if (filter.getStoreTransactionStatus().equals("정지")) {
                    filterTransactionStatus = false;
                } else {
                    // 예외 처리 or 무시
                    continue;
                }

                if (store.getStoreTransactionStatus() == null || 
                !store.getStoreTransactionStatus().equals(filterTransactionStatus)) {
                    continue;
                }
            }
            
            // ✅ storeRequestStatusName 설정 (직접 매핑)
            String storeRequestStatusName = "대기";  // 기본값
            if (store.getStoreRequestStatusIndex() != null) {
                switch (store.getStoreRequestStatusIndex()) {
                    case 1:
                        storeRequestStatusName = "대기";
                        break;
                    case 2:
                        storeRequestStatusName = "승인";
                        break;
                    case 3:
                        storeRequestStatusName = "거절";
                        break;
                    default:
                        storeRequestStatusName = "알 수 없음";
                        break;
                }
            }
            
            // storeTransactionStatus 설정
            String storeTransactionStatus = "정지";
            if (store.getStoreTransactionStatus() != null && store.getStoreTransactionStatus()) {
                storeTransactionStatus = "정상";
            }
            
            // ✅ UserCm 정보 조회 (가맹점 사용자 기준)
            UserCm userCm = null;
            Integer totalCM = 0;
            Integer userCmpInit = 0;
            if (storeUserTesseris != null && storeUserTesseris.getUserIndex() != null) {
                userCm = userCmRepository.findById(storeUserTesseris.getUserIndex()).orElse(null);
                if (userCm != null) {
                    Integer deposit = userCm.getUserCmDeposit() != null ? userCm.getUserCmDeposit() : 0;
                    Integer withdrawal = userCm.getUserCmWithdrawal() != null ? userCm.getUserCmWithdrawal() : 0;
                    totalCM = deposit + withdrawal;
                    userCmpInit = userCm.getUserCmpInit() != null ? userCm.getUserCmpInit() : 0;
                }
            }
            
            // ✅ DTO 생성 (올바른 변수명 사용)
            StoreRegisterdListDto dto = StoreRegisterdListDto.builder()
                .businessUserId(businessUserId)
                .businessUserName(businessUserName)
                .businessGradeName(businessGradeName)
                .userId(userId)
                .userName(storeUser != null ? storeUser.getName() : null)  // ✅ storeUser 사용
                .userPhone(storeUser != null ? storeUser.getPhone() : null)  // ✅ storeUser 사용
                .storeBossName(store.getStoreBossName())
                .storeCorporateName(store.getStoreCorporateName())
                .storeName(store.getStoreName())
                .storeRequestStatusName(storeRequestStatusName)
                .storeTransactionStatus(storeTransactionStatus)
                .userCmpInit(userCmpInit)
                .totalCM(totalCM)
                .storeCreateDate(store.getStoreCreateDate() != null ? store.getStoreCreateDate().toLocalDate().toString() : null)
                .storeSubscriptionFeeValue(fee.getStoreSubscriptionFeeValue())  // ✅ 현재 fee 객체 사용
                .franchiseFee(store.getFranchiseFee())
                .storeSubscriptionFeeCommissionCheck(fee.getStoreSubscriptionFeeCommissionCheck())  // ✅ 현재 fee 객체 사용
                .build();
                
            result.add(dto);
        }
        
        // storeCreateDate가 최근인 것이 위로 오도록 내림차순 정렬
        result.sort((dto1, dto2) -> {
            if (dto1.getStoreCreateDate() == null && dto2.getStoreCreateDate() == null) {
                return 0;
            }
            if (dto1.getStoreCreateDate() == null) {
                return 1; // null은 뒤로
            }
            if (dto2.getStoreCreateDate() == null) {
                return -1; // null은 뒤로
            }
            return dto2.getStoreCreateDate().compareTo(dto1.getStoreCreateDate()); // 내림차순
        });
        
        return result;
    }



}
