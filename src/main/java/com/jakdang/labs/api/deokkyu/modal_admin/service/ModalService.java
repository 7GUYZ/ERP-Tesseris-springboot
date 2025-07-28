package com.jakdang.labs.api.deokkyu.modal_admin.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.jakdang.labs.api.deokkyu.modal_admin.dto.StoreDetailDto;
import com.jakdang.labs.api.deokkyu.modal_admin.dto.StoreTransactionHistoryDto;
import com.jakdang.labs.api.deokkyu.modal_admin.dto.BusinessManDetailDto;
import com.jakdang.labs.api.deokkyu.modal_admin.dto.BusinessManTransactionHistoryDto;
import com.jakdang.labs.api.deokkyu.modal_admin.repository.UserCmLogPaymenthdkRepo;
import com.jakdang.labs.api.deokkyu.modal_admin.repository.UserCmLogTransactionTypehdkRepo;
import com.jakdang.labs.api.deokkyu.modal_admin.repository.UserCmLoghdkRepo;
import com.jakdang.labs.api.deokkyu.businessman.repository.TemporaryStoreDetailhdkRepo;
import com.jakdang.labs.api.deokkyu.businessman.repository.TemporaryStoreMasterhdkRepo;
import com.jakdang.labs.api.deokkyu.store.repository.UserhdkRepo;
import com.jakdang.labs.api.deokkyu.store.repository.UserTesserishdkRepo;
import com.jakdang.labs.api.deokkyu.store.repository.StorehdkRepo;
import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.entity.UserCmLog;
import com.jakdang.labs.entity.UserCmLogPayment;
import com.jakdang.labs.entity.UserCmLogTransactionType;
import com.jakdang.labs.entity.Store;
import com.jakdang.labs.entity.TemporaryStoreDetail;
import com.jakdang.labs.entity.TemporaryStoreMaster;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ModalService {
    
    private final UserhdkRepo userRepository;
    private final UserTesserishdkRepo userTesserisRepository;
    private final UserCmLoghdkRepo userCmLogRepository;
    private final UserCmLogPaymenthdkRepo userCmLogPaymentRepository;
    private final UserCmLogTransactionTypehdkRepo userCmLogTransactionTypeRepository;
    private final StorehdkRepo storeRepository;
    private final TemporaryStoreDetailhdkRepo temporaryStoreDetailRepository;
    private final TemporaryStoreMasterhdkRepo temporaryStoreMasterRepository;


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

        // 2. UserEntity로 UserTesseris 조회 (무조건 하나의 값 - 1개 또는 0개)
        List<UserTesseris> tesserisList = userTesserisRepository.findByUsersId(userEntity);
        if (tesserisList.isEmpty()) {
            return List.of();
        }
        
        // 첫 번째 UserTesseris 사용 (무조건 하나)
        UserTesseris userTesseris = tesserisList.get(0);
        Integer userIndex = userTesseris.getUserIndex();

        // 3. user_index로 UserCmLog에서 user_index_event_party와 같은 레코드들 조회
        List<UserCmLog> cmLogs = userCmLogRepository.findByUserIndexEventPartyUserIndexOrderByUserCmLogCreateTimeDesc(userIndex);

        // 4. UserCmLog를 DTO로 변환
        List<StoreTransactionHistoryDto> transactions = cmLogs.stream()
            .<StoreTransactionHistoryDto>map(log -> {
                // user_cm_log_payment_index로 user_cm_log_payment_name 조회
                String userCmLogPaymentName = "";
                if (log.getUserCmLogPaymentIndex() != null) {
                    Optional<UserCmLogPayment> paymentOpt = userCmLogPaymentRepository.findById(log.getUserCmLogPaymentIndex());
                    if (paymentOpt.isPresent()) {
                        userCmLogPaymentName = paymentOpt.get().getUserCmLogPaymentName() != null ? 
                                              paymentOpt.get().getUserCmLogPaymentName() : "";
                    }
                }

                // user_cm_log_transaction_type_index로 user_cm_log_transaction_type_name 조회
                String userCmLogTransactionTypeName = "";
                if (log.getUserCmLogTransactionTypeIndex() != null) {
                    Optional<UserCmLogTransactionType> transactionTypeOpt = userCmLogTransactionTypeRepository.findById(log.getUserCmLogTransactionTypeIndex());
                    if (transactionTypeOpt.isPresent()) {
                        userCmLogTransactionTypeName = transactionTypeOpt.get().getUserCmLogTransactionTypeName() != null ?
                                                      transactionTypeOpt.get().getUserCmLogTransactionTypeName() : "";
                    }
                }

                // user_index_event_trigger 값 자체
                Integer userIndexEventTrigger = 0;
                if (log.getUserIndexEventTrigger() != null) {
                    userIndexEventTrigger = log.getUserIndexEventTrigger().getUserIndex() != null ? 
                                           log.getUserIndexEventTrigger().getUserIndex() : 0;
                }

                // user_index_event_trigger로 UserTesseris → users_id → users.email 조회
                String userIndexEventTriggerId = "";
                if (log.getUserIndexEventTrigger() != null) {
                    UserTesseris triggerUserTesseris = log.getUserIndexEventTrigger();
                    if (triggerUserTesseris != null && triggerUserTesseris.getUsersId() != null) {
                        userIndexEventTriggerId = triggerUserTesseris.getUsersId().getEmail() != null ? 
                                                 triggerUserTesseris.getUsersId().getEmail() : "";
                    }
                }

                // user_cm_log 테이블에서 직접 값 가져오기
                LocalDateTime userCmLogCreateTime = log.getUserCmLogCreateTime();
                String userCmLogReason = log.getUserCmLogReason() != null ? log.getUserCmLogReason() : "";
                Integer userCmLogValue = log.getUserCmLogValue() != null ? log.getUserCmLogValue() : 0;

                return StoreTransactionHistoryDto.builder()
                    .userCmLogPaymentName(userCmLogPaymentName)
                    .userCmLogTransactionTypeName(userCmLogTransactionTypeName)
                    .userIndexEventTrigger(userIndexEventTrigger)
                    .userIndexEventTriggerId(userIndexEventTriggerId)
                    .userCmLogCreateTime(userCmLogCreateTime)
                    .userCmLogReason(userCmLogReason)
                    .userCmLogValue(userCmLogValue)
                    .build();
            })
            .collect(Collectors.toList());

        // 거래 시간 내림차순으로 정렬
        return transactions.stream()
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

    /**
     * 가맹점 상세정보 조회
     * @param storeId 조회할 가맹점 사용자 ID
     * @return 가맹점 상세정보
     */
    public StoreDetailDto getStoreDetail(String storeId) {
        // 1. storeId로 UserEntity 조회
        Optional<UserEntity> userEntityOpt = userRepository.findById(storeId);
        if (userEntityOpt.isEmpty()) {
            return null;
        }
        UserEntity userEntity = userEntityOpt.get();

        // 2. UserEntity로 UserTesseris 리스트 조회
        List<UserTesseris> tesserisList = userTesserisRepository.findByUsersId(userEntity);
        if (tesserisList.isEmpty()) {
            return null;
        }

        // 3. storeId로 Store 정보 조회 (user_index와 비교)
        Store store = null;
        UserTesseris userTesseris = null;
        for (UserTesseris tesseris : tesserisList) {
            store = storeRepository.findByUserIndex(tesseris);
            if (store != null) {
                userTesseris = tesseris;
                break;
            }
        }

        if (store == null || userTesseris == null) {
            return null;
        }

        // 4. DTO 생성
        String userPassword = userEntity.getPassword() != null ? userEntity.getPassword() : "";
        String userBirthday = userTesseris.getUserBirthday() != null ? userTesseris.getUserBirthday().toString() : "";
        Integer userGenderIndex = userTesseris.getUserGender() != null ? userTesseris.getUserGender().getUserGenderIndex() : 0;
        
        // Store 기본 정보
        String storeName = store.getStoreName() != null ? store.getStoreName() : "";
        String storePhone = store.getStorePhone() != null ? store.getStorePhone() : "";
        String storeBossName = store.getStoreBossName() != null ? store.getStoreBossName() : "";
        String storeCorporateName = store.getStoreCorporateName() != null ? store.getStoreCorporateName() : "";
        String storeAddress = store.getStoreAddress() != null ? store.getStoreAddress() : "";
        String storeDetailAddress = store.getStoreDetailAddress() != null ? store.getStoreDetailAddress() : "";
        
        // Store 상태 정보
        Integer storeRequestStatusIndex = store.getStoreRequestStatusIndex();
        Boolean storeTransactionStatus = store.getStoreTransactionStatus();
        
        // Store 사진 정보
        String storeProntPhoto = store.getStoreProntPhoto() != null ? store.getStoreProntPhoto() : "";
        String storeBusinessLicensePhoto = store.getStoreBusinessLicensePhoto() != null ? store.getStoreBusinessLicensePhoto() : "";
        String storeSignPhoto = store.getStoreSignPhoto() != null ? store.getStoreSignPhoto() : "";

        return StoreDetailDto.builder()
            .userPassword(userPassword)
            .userBirthday(userBirthday)
            .userGenderIndex(userGenderIndex)
            .storeName(storeName)
            .storePhone(storePhone)
            .storeBossName(storeBossName)
            .storeCorporateName(storeCorporateName)
            .storeAddress(storeAddress)
            .storeDetailAddress(storeDetailAddress)
            .storeRequestStatusIndex(storeRequestStatusIndex)
            .storeTransactionStatus(storeTransactionStatus)
            .storeProntPhoto(storeProntPhoto)
            .storeBusinessLicensePhoto(storeBusinessLicensePhoto)
            .storeSignPhoto(storeSignPhoto)
            .build();
    }

    /**
     * 사업자 상세정보 조회
     * @param businessManId 조회할 사업자 ID
     * @return 사업자 상세정보
     */
    public BusinessManDetailDto getBusinessManDetail(String businessManId) {
        // 1. businessManId로 UserEntity 조회
        Optional<UserEntity> userEntityOpt = userRepository.findById(businessManId);
        if (userEntityOpt.isEmpty()) {
            return null;
        }
        UserEntity userEntity = userEntityOpt.get();

        // 2. UserEntity로 UserTesseris 리스트 조회
        List<UserTesseris> tesserisList = userTesserisRepository.findByUsersId(userEntity);
        if (tesserisList.isEmpty()) {
            return null;
        }

        // 3. 첫 번째 UserTesseris 사용 (사업자는 보통 하나의 UserTesseris만 가짐)
        UserTesseris userTesseris = tesserisList.get(0);

        // 4. DTO 생성 - BusinessManDetailDto의 현재 필드들만 사용
        String userPassword = userEntity.getPassword() != null ? userEntity.getPassword() : "";
        String userName = userEntity.getName() != null ? userEntity.getName() : "";
        String userBirthday = userTesseris.getUserBirthday() != null ? userTesseris.getUserBirthday().toString() : "";
        Integer userGenderIndex = userTesseris.getUserGender() != null ? userTesseris.getUserGender().getUserGenderIndex() : 0;

        return BusinessManDetailDto.builder()
            .userPassword(userPassword)
            .userName(userName)
            .userBirthday(userBirthday)
            .userGenderIndex(userGenderIndex)
            .build();
    }

    /**
     * 사업자 거래내역 조회
     * @param businessManId 조회할 사업자 ID
     * @return 거래내역 리스트
     */
    public List<BusinessManTransactionHistoryDto> getBusinessManTransactionHistory(String businessManId) {
        // 1. businessManId로 UserEntity 조회
        Optional<UserEntity> userEntityOpt = userRepository.findById(businessManId);
        if (userEntityOpt.isEmpty()) {
            return List.of();
        }
        UserEntity userEntity = userEntityOpt.get();

        // 2. UserEntity로 UserTesseris 조회 (무조건 하나의 값 - 1개 또는 0개)
        List<UserTesseris> tesserisList = userTesserisRepository.findByUsersId(userEntity);
        if (tesserisList.isEmpty()) {
            return List.of();
        }
    
        // 첫 번째 UserTesseris 사용 (무조건 하나)
        UserTesseris userTesseris = tesserisList.get(0);

        // 3. user_index로 TemporaryStoreDetail에서 해당하는 레코드들 조회
        List<TemporaryStoreDetail> temporaryStoreDetails = temporaryStoreDetailRepository.findByUserIndex(userTesseris);

        // 4. TemporaryStoreDetail를 DTO로 변환
        List<BusinessManTransactionHistoryDto> transactions = temporaryStoreDetails.stream()
            .<BusinessManTransactionHistoryDto>map(detail -> {
                // 1. temporaryStoreMasterIndexName 조회
                String temporaryStoreMasterIndexName = "";
                LocalDateTime temporaryStoreMasterDistributionTime = null;
                
                if (detail.getTemporaryStoreMasterIndex() != null) {
                    TemporaryStoreMaster master = detail.getTemporaryStoreMasterIndex();
                    
                    // temporary_store_master_charge_time 가져오기
                    temporaryStoreMasterDistributionTime = master.getTemporaryStoreMasterChargeTime();
                    
                    // store_user_index로 Store 조회 후 user_index로 UserTesseris → users → user_name 조회
                    if (master.getStoreUserIndex() != null) {
                        Store store = master.getStoreUserIndex();
                        if (store.getUserIndex() != null) {
                            UserTesseris storeUserTesseris = store.getUserIndex();
                            if (storeUserTesseris.getUsersId() != null) {
                                temporaryStoreMasterIndexName = storeUserTesseris.getUsersId().getName() != null ? 
                                                               storeUserTesseris.getUsersId().getName() : "";
                            }
                        }
                    }
                }

                // 2. temporaryStoreCmValue - TemporaryStoreDetail에서 분배 받은 금액
                Integer temporaryStoreCmValue = detail.getTemporaryStoreCmValue() != null ? 
                                               detail.getTemporaryStoreCmValue().intValue() : 0;

                return BusinessManTransactionHistoryDto.builder()
                    .temporaryStoreMasterIndexName(temporaryStoreMasterIndexName)
                    .temporaryStoreMasterDistributionTime(temporaryStoreMasterDistributionTime)
                    .temporaryStoreCmValue(temporaryStoreCmValue)
                    .build();
            })
            .collect(Collectors.toList());

        // 시간 내림차순으로 정렬
        return transactions.stream()
            .sorted((a, b) -> {
                if (a.getTemporaryStoreMasterDistributionTime() == null && b.getTemporaryStoreMasterDistributionTime() == null) {
                    return 0;
                }
                if (a.getTemporaryStoreMasterDistributionTime() == null) {
                    return 1;
                }
                if (b.getTemporaryStoreMasterDistributionTime() == null) {
                    return -1;
                }
                return b.getTemporaryStoreMasterDistributionTime().compareTo(a.getTemporaryStoreMasterDistributionTime());
            })
            .collect(Collectors.toList());
    }




    
    /**
     * 가맹점 정보 수정
     * @param storeId 수정할 가맹점 사용자 ID
     * @param data 수정할 데이터
     * @return 수정 성공 여부
     */
    public boolean updateStore(String storeId, StoreDetailDto data) {
        try {
            // 1. storeId로 UserEntity 조회
            Optional<UserEntity> userEntityOpt = userRepository.findById(storeId);
            if (userEntityOpt.isEmpty()) {
                return false;
            }
            UserEntity userEntity = userEntityOpt.get();

            // 2. UserEntity로 UserTesseris 조회
            List<UserTesseris> tesserisList = userTesserisRepository.findByUsersId(userEntity);
            if (tesserisList.isEmpty()) {
                return false;
            }
            UserTesseris userTesseris = tesserisList.get(0);

            // 3. Store 정보 조회
            Store store = storeRepository.findByUserIndex(userTesseris);
            if (store == null) {
                return false;
            }

            // 4. 데이터 업데이트
            
            // Users 테이블 업데이트
            if (data.getUserPassword() != null && !data.getUserPassword().trim().isEmpty()) {
                userEntity.setPassword(data.getUserPassword());
                userRepository.save(userEntity);
            }
            
            // UserTesseris 테이블 업데이트
            boolean userTesserisUpdated = false;
            if (data.getUserBirthday() != null && !data.getUserBirthday().trim().isEmpty()) {
                try {
                    java.time.LocalDate birthday = java.time.LocalDate.parse(data.getUserBirthday());
                    userTesseris.setUserBirthday(birthday);
                    userTesserisUpdated = true;
                } catch (Exception e) {
                    // 날짜 파싱 실패 시 무시
                }
            }
            if (data.getUserGenderIndex() != null) {
                // UserGender 관련 로직이 필요한 경우 추가
                userTesserisUpdated = true;
            }
            if (userTesserisUpdated) {
                userTesserisRepository.save(userTesseris);
            }
            
            // Store 테이블 업데이트 - 기본 정보
            if (data.getStoreName() != null && !data.getStoreName().trim().isEmpty()) {
                store.setStoreName(data.getStoreName());
            }
            if (data.getStorePhone() != null && !data.getStorePhone().trim().isEmpty()) {
                store.setStorePhone(data.getStorePhone());
            }
            if (data.getStoreBossName() != null && !data.getStoreBossName().trim().isEmpty()) {
                store.setStoreBossName(data.getStoreBossName());
            }
            if (data.getStoreCorporateName() != null && !data.getStoreCorporateName().trim().isEmpty()) {
                store.setStoreCorporateName(data.getStoreCorporateName());
            }
            if (data.getStoreAddress() != null && !data.getStoreAddress().trim().isEmpty()) {
                store.setStoreAddress(data.getStoreAddress());
            }
            if (data.getStoreDetailAddress() != null && !data.getStoreDetailAddress().trim().isEmpty()) {
                store.setStoreDetailAddress(data.getStoreDetailAddress());
            }
            
            // Store 테이블 업데이트 - 상태 정보
            if (data.getStoreRequestStatusIndex() != null) {
                store.setStoreRequestStatusIndex(data.getStoreRequestStatusIndex());
            }
            
            // storeTransactionStatus 처리 (Object 타입 - Boolean 또는 String)
            if (data.getStoreTransactionStatus() != null) {
                if (data.getStoreTransactionStatus() instanceof Boolean) {
                    store.setStoreTransactionStatus((Boolean) data.getStoreTransactionStatus());
                } else if (data.getStoreTransactionStatus() instanceof String) {
                    String statusStr = (String) data.getStoreTransactionStatus();
                    Boolean transactionStatus = "정상".equals(statusStr);
                    store.setStoreTransactionStatus(transactionStatus);
                }
            }
            // 문자열로 온 거래 상태를 Boolean으로 변환 (storeTransactionStatusString)
            if (data.getStoreTransactionStatusString() != null && !data.getStoreTransactionStatusString().trim().isEmpty()) {
                Boolean transactionStatus = "정상".equals(data.getStoreTransactionStatusString());
                store.setStoreTransactionStatus(transactionStatus);
            }
            
            // Store 테이블 업데이트 - 사진 정보
            if (data.getStoreProntPhoto() != null && !data.getStoreProntPhoto().trim().isEmpty()) {
                store.setStoreProntPhoto(data.getStoreProntPhoto());
            }
            if (data.getStoreBusinessLicensePhoto() != null && !data.getStoreBusinessLicensePhoto().trim().isEmpty()) {
                store.setStoreBusinessLicensePhoto(data.getStoreBusinessLicensePhoto());
            }
            if (data.getStoreSignPhoto() != null && !data.getStoreSignPhoto().trim().isEmpty()) {
                store.setStoreSignPhoto(data.getStoreSignPhoto());
            }
            
            storeRepository.save(store);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 사업자 정보 수정
     * @param businessManId 수정할 사업자 ID
     * @param data 수정할 데이터
     * @return 수정 성공 여부
     */
    public boolean updateBusinessMan(String businessManId, BusinessManDetailDto data) {
        try {
            // 1. businessManId로 UserEntity 조회
            Optional<UserEntity> userEntityOpt = userRepository.findById(businessManId);
            if (userEntityOpt.isEmpty()) {
                return false;
            }
            UserEntity userEntity = userEntityOpt.get();

            // 2. UserEntity로 UserTesseris 조회
            List<UserTesseris> tesserisList = userTesserisRepository.findByUsersId(userEntity);
            if (tesserisList.isEmpty()) {
                return false;
            }
            UserTesseris userTesseris = tesserisList.get(0);

            // 3. 데이터 업데이트 - BusinessManDetailDto의 현재 필드들만 사용
            
            // Users 테이블 업데이트
            boolean userUpdated = false;
            if (data.getUserPassword() != null && !data.getUserPassword().trim().isEmpty()) {
                userEntity.setPassword(data.getUserPassword());
                userUpdated = true;
            }
            if (data.getUserName() != null && !data.getUserName().trim().isEmpty()) {
                userEntity.setName(data.getUserName());
                userUpdated = true;
            }
            if (userUpdated) {
                userRepository.save(userEntity);
            }
            
            // UserTesseris 테이블 업데이트
            boolean userTesserisUpdated = false;
            if (data.getUserBirthday() != null && !data.getUserBirthday().trim().isEmpty()) {
                try {
                    java.time.LocalDate birthday = java.time.LocalDate.parse(data.getUserBirthday());
                    userTesseris.setUserBirthday(birthday);
                    userTesserisUpdated = true;
                } catch (Exception e) {
                    // 날짜 파싱 실패 시 무시
                    System.err.println("생년월일 파싱 실패: " + data.getUserBirthday());
                }
            }
            if (data.getUserGenderIndex() != null) {
                // UserGender 관련 로직이 필요한 경우 추가
                // 현재는 userGenderIndex만 설정
                userTesserisUpdated = true;
            }
            if (userTesserisUpdated) {
                userTesserisRepository.save(userTesseris);
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("사업자 정보 수정 중 오류 발생: " + e.getMessage());
            return false;
        }
    }

    /**
     * 가맹점 신청 상세정보 조회
     * @param storeId 조회할 가맹점 사용자 ID
     * @return 가맹점 신청 상세정보
     */
    public StoreDetailDto getStoreRegisterDetail(String storeId) {
        // 기본적으로 getStoreDetail과 동일한 로직 사용
        // 필요시 추가 로직 구현
        return getStoreDetail(storeId);
    }

    /**
     * 가맹점 신청 정보 수정
     * @param storeId 수정할 가맹점 사용자 ID
     * @param data 수정할 데이터
     * @return 수정 성공 여부
     */
    public boolean updateStoreRegister(String storeId, StoreDetailDto data) {
        // 기본적으로 updateStore와 동일한 로직 사용
        // 필요시 추가 로직 구현
        return updateStore(storeId, data);
    }


}

