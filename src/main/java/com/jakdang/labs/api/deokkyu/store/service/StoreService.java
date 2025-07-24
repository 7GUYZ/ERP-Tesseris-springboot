package com.jakdang.labs.api.deokkyu.store.service;

import org.springframework.stereotype.Service;

import com.jakdang.labs.api.deokkyu.store.dto.CusStoreListDto;
import com.jakdang.labs.api.deokkyu.store.dto.CustomerDto;
import com.jakdang.labs.api.deokkyu.store.dto.StoreListDto;
import com.jakdang.labs.api.deokkyu.store.dto.StoreListSearchDto;
import com.jakdang.labs.api.deokkyu.store.dto.StoreRegisterdListDto;
import com.jakdang.labs.api.deokkyu.store.dto.StoreTransactionHistoryDto;
import com.jakdang.labs.entity.BusinessGrade;
import com.jakdang.labs.entity.BusinessMan;
import com.jakdang.labs.entity.Store;
import com.jakdang.labs.entity.StoreCategory;
import com.jakdang.labs.entity.StoreCustomer;
import com.jakdang.labs.entity.StoreRequestStatus;
import com.jakdang.labs.entity.UserCm;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.entity.StoreSubscriptionFee;
import com.jakdang.labs.entity.UserCmLog;
import com.jakdang.labs.entity.UserCmLogPayment;
import com.jakdang.labs.entity.UserCmLogTransactionType;
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
import com.jakdang.labs.api.deokkyu.store.repository.UserCmLoghdkRepo;
import com.jakdang.labs.api.deokkyu.store.repository.UserCmLogPaymenthdkRepo;
import com.jakdang.labs.api.deokkyu.store.repository.UserCmLogTransactionTypehdkRepo;
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
    private final UserCmLoghdkRepo userCmLogRepository;
    private final UserCmLogPaymenthdkRepo userCmLogPaymentRepository;
    private final UserCmLogTransactionTypehdkRepo userCmLogTransactionTypeRepository;

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
            
            // storeRequestStatusName 필터링 추가
            if (filter.getStoreRequestStatusName() != null && !filter.getStoreRequestStatusName().isBlank() && !filter.getStoreRequestStatusName().equals("전체")) {
                Optional<StoreRequestStatus> requestId = storeRequestStatusRepository.findById(store.getStoreRequestStatusIndex());
                if (requestId.isEmpty() || requestId.get().getStoreRequestStatusName() == null ||
                !requestId.get().getStoreRequestStatusName().contains(filter.getStoreRequestStatusName())) {
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
            // StoreSubscriptionFee 정보 (가장 최근 값 1개만)
            List<StoreSubscriptionFee> fees = storeSubscriptionFeeRepository.findByStoreUserIndex(store);
            StoreSubscriptionFee fee = null;
            if (!fees.isEmpty()) {
                fee = fees.stream().sorted((a, b) -> b.getStoreSubscriptionFeeTime().compareTo(a.getStoreSubscriptionFeeTime())).findFirst().orElse(null);
            }
            
            // storeRequestStatusName 설정
            String storeRequestStatusName = null;
            if (store.getStoreRequestStatusIndex() != null) {
                Optional<StoreRequestStatus> status = storeRequestStatusRepository.findById(store.getStoreRequestStatusIndex());
                if (status.isPresent()) {
                    storeRequestStatusName = status.get().getStoreRequestStatusName();
                }
            }
            
            // storeTransactionStatus 설정
            String storeTransactionStatus = "정지";
            if (store.getStoreTransactionStatus() != null && store.getStoreTransactionStatus()) {
                storeTransactionStatus = "정상";
            }
            
            // 사업자 정보 조회 (businessMan → userTesseris → usersId(UserEntity) 경로)
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
            
            // UserCm 정보 조회 (초기지급 CMP, 보유 CM)
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
            
            StoreRegisterdListDto dto = StoreRegisterdListDto.builder()
                .businessUserId(businessUserId)
                .businessUserName(businessUserName)
                .businessGradeName(businessGradeName)
                .userId(userId)
                .userName(user != null ? user.getName() : null)
                .userPhone(user != null ? user.getPhone() : null)
                .storeBossName(store.getStoreBossName())
                .storeCorporateName(store.getStoreCorporateName())
                .storeName(store.getStoreName())
                .storeRequestStatusName(storeRequestStatusName)
                .storeTransactionStatus(storeTransactionStatus)
                .userCmpInit(userCmpInit)
                .totalCM(totalCM)
                .storeCreateDate(store.getStoreCreateDate() != null ? store.getStoreCreateDate().toLocalDate().toString() : null)
                .storeSubscriptionFeeValue(fee != null ? fee.getStoreSubscriptionFeeValue() : null)
                .franchiseFee(store.getFranchiseFee())
                .storeSubscriptionFeeCommissionCheck(fee != null ? fee.getStoreSubscriptionFeeCommissionCheck() : null)
                .build();
            result.add(dto);
        }
        return result;
    }

    /**
     * 가맹점 거래내역 조회
     * @param userId 조회할 사용자 ID
     * @return 거래내역 리스트
     */
    public List<StoreTransactionHistoryDto> getStoreTransactionHistory(String userId) {
        // 1. userId로 UserEntity 조회
        Optional<UserEntity> userEntityOpt = userRepository.findById(userId);
        if (userEntityOpt.isEmpty()) {
            return List.of();
        }
        UserEntity userEntity = userEntityOpt.get();

        // 2. UserEntity로 UserTesseris 리스트 조회
        List<UserTesseris> tesserisList = userTesserisRepository.findByUsersId(userEntity);
        if (tesserisList.isEmpty()) {
            return List.of();
        }

        // 3. 모든 UserTesseris의 거래내역 조회
        List<StoreTransactionHistoryDto> allTransactions = new ArrayList<>();
        for (UserTesseris userTesseris : tesserisList) {
            // user_index_event_party = userTesseris.userIndex인 거래내역 조회
            List<UserCmLog> cmLogs = userCmLogRepository.findByUserIndexEventPartyUserIndexOrderByUserCmLogCreateTimeDesc(
                userTesseris.getUserIndex()
            );

            // UserCmLog를 DTO로 변환
            List<StoreTransactionHistoryDto> transactions = cmLogs.stream()
                .map(log -> {
                    // 1. user_password (users 테이블에서)
                    String userPassword = userEntity.getPassword() != null ? userEntity.getPassword() : "";

                    // 2, 3. user_birthday, user_gender_index (user_tesseris 테이블에서)
                    String userBirthday = userTesseris.getUserBirthday() != null ? userTesseris.getUserBirthday().toString() : "";
                    Integer userGenderIndex = userTesseris.getUserGender() != null ? userTesseris.getUserGender().getUserGenderIndex() : 0;

                    // 4. user_cm_log_payment_name (user_cm_log_payment 테이블에서)
                    String paymentName = "";
                    if (log.getUserCmLogPaymentIndex() != null) {
                        Optional<UserCmLogPayment> paymentOpt = userCmLogPaymentRepository.findById(log.getUserCmLogPaymentIndex());
                        if (paymentOpt.isPresent()) {
                            paymentName = paymentOpt.get().getUserCmLogPaymentName() != null ? 
                                         paymentOpt.get().getUserCmLogPaymentName() : "";
                        }
                    }

                    // 5. user_cm_log_transaction_type_name (user_cm_log_transaction_type 테이블에서)
                    String transactionTypeName = "";
                    if (log.getUserCmLogTransactionTypeIndex() != null) {
                        Optional<UserCmLogTransactionType> transactionTypeOpt = userCmLogTransactionTypeRepository.findById(log.getUserCmLogTransactionTypeIndex());
                        if (transactionTypeOpt.isPresent()) {
                            transactionTypeName = transactionTypeOpt.get().getUserCmLogTransactionTypeName() != null ?
                                                 transactionTypeOpt.get().getUserCmLogTransactionTypeName() : "";
                        }
                    }

                    // 6. user_index_event_trigger (거래 요청인)
                    Integer triggerUserIndex = 0;
                    if (log.getUserIndexEventTrigger() != null) {
                        triggerUserIndex = log.getUserIndexEventTrigger().getUserIndex() != null ? 
                                          log.getUserIndexEventTrigger().getUserIndex() : 0;
                    }

                    // 7. user_cm_log_create_time (거래 발생 시간)
                    LocalDateTime createTime = log.getUserCmLogCreateTime();

                    // 8. user_cm_log_reason (거래에 대한 메모)
                    String reason = log.getUserCmLogReason() != null ? log.getUserCmLogReason() : "";

                    return StoreTransactionHistoryDto.builder()
                        .userPassword(userPassword)                          // 1
                        .userBirthday(userBirthday)                         // 2
                        .userGenderIndex(userGenderIndex)                   // 3
                        .userCmLogPaymentName(paymentName)                  // 4
                        .userCmLogTransactionTypeName(transactionTypeName)  // 5
                        .userIndexEventTrigger(triggerUserIndex)            // 6
                        .userCmLogCreateTime(createTime)                    // 7
                        .userCmLogReason(reason)                            // 8
                        .build();
                })
                .collect(Collectors.toList());

            allTransactions.addAll(transactions);
        }

        // 거래 시간 내림차순으로 정렬
        return allTransactions.stream()
            .sorted((a, b) -> {
                if (a.getUserCmLogCreateTime() == null && b.getUserCmLogCreateTime() == null) {
                    return 0;
                }
                if (a.getUserCmLogCreateTime() == null) {
                    return 1;
                }
                if (b.getUserCmLogCreateTime() == null) {
                    return -1;
                }
                return b.getUserCmLogCreateTime().compareTo(a.getUserCmLogCreateTime());
            })
            .collect(Collectors.toList());
    }

}
