package com.jakdang.labs.api.deokkyu.storeRegister.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakdang.labs.api.deokkyu.storeRegister.dto.StoreRegisterRequestDto;
import com.jakdang.labs.entity.Store;
import com.jakdang.labs.entity.TemporaryStoreMaster;
import com.jakdang.labs.entity.TemporaryStoreDetail;
import com.jakdang.labs.api.alarm.service.AlarmSvc;
import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.api.deokkyu.store.repository.StorehdkRepo;
import com.jakdang.labs.api.deokkyu.businessman.repository.TemporaryStoreMasterhdkRepo;
import com.jakdang.labs.api.deokkyu.businessman.repository.TemporaryStoreDetailhdkRepo;
import com.jakdang.labs.api.deokkyu.store.repository.UserhdkRepo;
import com.jakdang.labs.api.deokkyu.store.repository.UserTesserishdkRepo;
import com.jakdang.labs.api.deokkyu.store.repository.BusinessManhdkRepo;
import com.jakdang.labs.api.deokkyu.store.repository.BusinessGradehdkRepo;
import com.jakdang.labs.api.deokkyu.store.repository.BusinessAreahdkRepo;
import com.jakdang.labs.entity.BusinessMan;
import com.jakdang.labs.entity.BusinessGrade;
import com.jakdang.labs.entity.BusinessArea;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreRegisterService {
    
    private final ObjectMapper objectMapper;
    private final StorehdkRepo storeRepository;
    private final TemporaryStoreMasterhdkRepo temporaryStoreMasterRepository;
    private final TemporaryStoreDetailhdkRepo temporaryStoreDetailRepository;
    private final UserhdkRepo userRepository;
    private final UserTesserishdkRepo userTesserisRepository;
    private final BusinessManhdkRepo businessManRepository;
    private final BusinessGradehdkRepo businessGradeRepository;
    private final BusinessAreahdkRepo businessAreaRepository;
    private final GeocodingService geocodingService;
    private final S3FileUploadService s3FileUploadService;
    private final AlarmSvc alarmSvc;
    
    /**
     * 가맹점 신청 등록
     * @param storeData 가맹점 신청 데이터 (JSON 문자열)
     * @param storeBusinessLicensePhoto 사업자등록증 사진
     * @param storeSignPhoto 간판 사진
     * @param storeFrontPhoto 외관 사진
     * @return 등록 결과
     */
    @Transactional
    public Map<String, Object> registerStore(String storeData, MultipartFile storeBusinessLicensePhoto, 
                                           MultipartFile storeSignPhoto, MultipartFile storeFrontPhoto) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("가맹점 신청 등록 처리 시작");
            
            // ObjectMapper 설정 - 알 수 없는 필드 무시
            ObjectMapper customObjectMapper = new ObjectMapper();
            customObjectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            
            // JSON 문자열을 DTO로 변환
            StoreRegisterRequestDto storeRegisterDto = customObjectMapper.readValue(storeData, StoreRegisterRequestDto.class);
            
            // 기본 정보 설정
            storeRegisterDto.setStatus("PENDING"); // 대기 상태로 설정
            storeRegisterDto.setCreatedAt(LocalDateTime.now());
            
            log.info("변환된 가맹점 신청 데이터: {}", storeRegisterDto);
            
            // 1. Store 테이블에 저장
            Store store = createStoreEntity(storeRegisterDto, storeBusinessLicensePhoto, storeSignPhoto, storeFrontPhoto);
            Store savedStore = storeRepository.save(store);
            log.info("Store 테이블 저장 완료: {}", savedStore.getStoreIndex());
            
            // 2. TemporaryStoreMaster 테이블에 저장
            TemporaryStoreMaster temporaryStoreMaster = createTemporaryStoreMasterEntity(savedStore);
            TemporaryStoreMaster savedTemporaryStoreMaster = temporaryStoreMasterRepository.save(temporaryStoreMaster);
            log.info("TemporaryStoreMaster 테이블 저장 완료: {}", savedTemporaryStoreMaster.getTemporaryStoreMasterIndex());
            
            // 3. TemporaryStoreDetail 테이블에 저장 (복잡한 로직)
            createTemporaryStoreDetailEntities(savedStore, savedTemporaryStoreMaster);
            log.info("TemporaryStoreDetail 테이블 저장 완료");
            
            // 4. UserTesseris 테이블의 user_role_index 변경 (1 -> 3)
            // Store에서 UserTesseris 정보를 활용하여 역할 변경
            updateUserRoleByUserTesseris(savedStore.getUserIndex());
            log.info("UserTesseris user_role_index 변경 완료");
            
            // 파일 S3 업로드 처리 (DB에는 파일명만 저장, S3에는 실제 파일 저장)
            String storeId = savedStore.getStoreIndex().toString();
            
            if (storeBusinessLicensePhoto != null && !storeBusinessLicensePhoto.isEmpty()) {
                log.info("사업자등록증 사진 S3 업로드 시작: {}, 크기: {} bytes", storeBusinessLicensePhoto.getOriginalFilename(), storeBusinessLicensePhoto.getSize());
                String businessLicenseUrl = s3FileUploadService.uploadStoreFile(storeBusinessLicensePhoto, "business_license", storeId);
                if (businessLicenseUrl != null) {
                    log.info("사업자등록증 사진 S3 업로드 성공: {} (DB에는 파일명 저장: {})", businessLicenseUrl, savedStore.getStoreBusinessLicensePhoto());
                }
            }
            if (storeSignPhoto != null && !storeSignPhoto.isEmpty()) {
                log.info("간판 사진 S3 업로드 시작: {}, 크기: {} bytes", storeSignPhoto.getOriginalFilename(), storeSignPhoto.getSize());
                String signPhotoUrl = s3FileUploadService.uploadStoreFile(storeSignPhoto, "sign_photo", storeId);
                if (signPhotoUrl != null) {
                    log.info("간판 사진 S3 업로드 성공: {} (DB에는 파일명 저장: {})", signPhotoUrl, savedStore.getStoreSignPhoto());
                }
            }
            if (storeFrontPhoto != null && !storeFrontPhoto.isEmpty()) {
                log.info("외관 사진 S3 업로드 시작: {}, 크기: {} bytes", storeFrontPhoto.getOriginalFilename(), storeFrontPhoto.getSize());
                String frontPhotoUrl = s3FileUploadService.uploadStoreFile(storeFrontPhoto, "front_photo", storeId);
                if (frontPhotoUrl != null) {
                    log.info("외관 사진 S3 업로드 성공: {} (DB에는 파일명 저장: {})", frontPhotoUrl, savedStore.getStoreProntPhoto());
                }
            }
            
            response.put("success", true);
            response.put("message", "가맹점 신청이 성공적으로 등록되었습니다");
            response.put("storeId", savedStore.getStoreIndex());
            response.put("status", storeRegisterDto.getStatus());
            
            log.info("가맹점 신청 등록 완료");

            // 가맹점 등록 알림 서비스 (user->admin)
            try {
                alarmSvc.sendNewStoreRegisterAlarm(savedStore.getUserIndex().getUserIndex());
                log.info("가맹점 신청 등록 알림 전송 완료");
            } catch (Exception e) {
                log.error("가맹점 신청 등록 알림 전송 실패: {}", e.getMessage());
            }

            return response;
            
        } catch (Exception e) {
            log.error("가맹점 신청 등록 실패", e); // ← 이걸로 바꾸면 콘솔에 정확한 원인 출력됨
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            response.put("success", false);
            response.put("message", "가맹점 신청 등록에 실패했습니다: " + e.getMessage());
            return response;
        }
    }
    
    /**
     * Store 엔티티 생성
     */
    private Store createStoreEntity(StoreRegisterRequestDto dto, MultipartFile storeBusinessLicensePhoto, 
                                   MultipartFile storeSignPhoto, MultipartFile storeFrontPhoto) {
        // user_index 조회
        UserTesseris userTesseris = null;
        
        // 1. userIndex가 있는 경우 직접 조회
        if (dto.getUserIndex() != null) {
            userTesseris = userTesserisRepository.findById(dto.getUserIndex())
                .orElseThrow(() -> new RuntimeException("UserTesseris를 찾을 수 없습니다: " + dto.getUserIndex()));
        } 
        // 2. userIndex가 없는 경우 userInfo.name으로 조회
        else if (dto.getUserInfo() != null && dto.getUserInfo().getName() != null) {
            String userName = dto.getUserInfo().getName();
            
            // userName으로 UserEntity 조회
            UserEntity userEntity = userRepository.findByName(userName)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userName));
            
            // UserEntity로 UserTesseris 조회
            List<UserTesseris> tesserisList = userTesserisRepository.findByUsersId(userEntity);
            if (tesserisList.isEmpty()) {
                throw new RuntimeException("UserTesseris를 찾을 수 없습니다: " + userName);
            }
            userTesseris = tesserisList.get(0);
        } else {
            throw new RuntimeException("사용자 정보가 부족합니다. userIndex 또는 userInfo.name이 필요합니다.");
        }
        
        Store store = new Store();
        
        // UserTesseris 설정
        store.setUserIndex(userTesseris);
        
        // 새로운 데이터 구조에서 정보 추출
        String storeRegistrationNum = dto.getBusinessInfo() != null ? dto.getBusinessInfo().getStoreRegistrationNum() : dto.getStoreRegistrationNum();
        String storeCorporateName = dto.getBusinessInfo() != null ? dto.getBusinessInfo().getStoreCorporateName() : dto.getStoreCorporateName();
        String storeBossName = dto.getBusinessInfo() != null ? dto.getBusinessInfo().getStoreBossName() : dto.getStoreBossName();
        String storeTypeTaxation = dto.getBusinessInfo() != null ? dto.getBusinessInfo().getStoreTypeTaxation() : dto.getStoreTypeTaxation();
        
        String storeName = dto.getStoreInfo() != null ? dto.getStoreInfo().getStore_name() : dto.getStoreName();
        String storePhone = dto.getStoreInfo() != null ? dto.getStoreInfo().getStore_phone() : dto.getStorePhone();
        String storePostcode = dto.getStoreInfo() != null ? dto.getStoreInfo().getStore_postcode() : dto.getStorePostcode();
        String storeAddress = dto.getStoreInfo() != null ? dto.getStoreInfo().getStore_address() : dto.getStoreAddress();
        String storeDetailAddress = dto.getStoreInfo() != null ? dto.getStoreInfo().getStore_detail_address() : dto.getStoreDetailAddress();
        String storeSite = dto.getStoreInfo() != null ? dto.getStoreInfo().getStoreSite() : dto.getStoreSite();
        String hasManagerStr = dto.getStoreInfo() != null ? dto.getStoreInfo().getHasManager() : (dto.getHasManager() != null ? dto.getHasManager().toString() : null);
        Boolean hasManager = "YES".equals(hasManagerStr);
        String managerId = dto.getStoreInfo() != null ? dto.getStoreInfo().getManagerId() : dto.getManagerId();
        
        // 사업자 등록 정보
        store.setStoreRegistrationNum(storeRegistrationNum);
        store.setStoreCorporateName(storeCorporateName);
        store.setStoreBossName(storeBossName);
        store.setStoreTypeTaxation(storeTypeTaxation);
        
        // 사업자등록증 파일명 저장
        if (storeBusinessLicensePhoto != null && !storeBusinessLicensePhoto.isEmpty()) {
            store.setStoreBusinessLicensePhoto(storeBusinessLicensePhoto.getOriginalFilename());
        } else {
            // Object 타입 처리
            Object businessLicensePhotoObj = dto.getBusinessInfo() != null ? dto.getBusinessInfo().getStoreBusinessLicensePhoto() : dto.getStoreBusinessLicensePhoto();
            String businessLicensePhotoStr = businessLicensePhotoObj != null ? businessLicensePhotoObj.toString() : "";
            store.setStoreBusinessLicensePhoto(businessLicensePhotoStr);
        }
        
        // 가맹점 등록 정보
        store.setStoreName(storeName);
        store.setStorePhone(storePhone);
        store.setStoreZoneCode(storePostcode); // storePostcode -> storeZoneCode
        store.setStoreAddress(storeAddress);
        store.setStoreDetailAddress(storeDetailAddress);
        store.setStoreSite(storeSite);
        
        // 1. 주소로 위도/경도 구하기
        if (storeAddress != null && !storeAddress.trim().isEmpty()) {
            String fullAddress = storeAddress;
            if (storeDetailAddress != null && !storeDetailAddress.trim().isEmpty()) {
                fullAddress = storeAddress + " " + storeDetailAddress;
            }
            
            try {
                String[] latLng = geocodingService.getLatLngAsString(fullAddress);
                if (latLng != null && !latLng[0].isEmpty() && !latLng[1].isEmpty()) {
                    store.setStorePos1(latLng[0]); // 위도
                    store.setStorePos2(latLng[1]); // 경도
                    log.info("주소 '{}' 의 좌표 설정 성공: 위도={}, 경도={}", fullAddress, latLng[0], latLng[1]);
                } else {
                    store.setStorePos1(""); // 빈 값 설정
                    store.setStorePos2(""); // 빈 값 설정
                    log.warn("주소 '{}' 의 지오코딩 실패 - 빈 값으로 설정", fullAddress);
                }
            } catch (Exception e) {
                store.setStorePos1(""); // 빈 값 설정
                store.setStorePos2(""); // 빈 값 설정
                log.warn("주소 '{}' 의 지오코딩 처리 중 오류 발생 - 빈 값으로 설정: {}", fullAddress, e.getMessage());
            }
        } else {
            store.setStorePos1(""); // 주소가 없으면 빈 값
            store.setStorePos2(""); // 주소가 없으면 빈 값
        }
        
        // 간판 사진 파일명 저장
        if (storeSignPhoto != null && !storeSignPhoto.isEmpty()) {
            store.setStoreSignPhoto(storeSignPhoto.getOriginalFilename());
        } else {
            // Object 타입 처리
            Object signPhotoObj = dto.getStoreInfo() != null ? dto.getStoreInfo().getStoreSignPhoto() : dto.getStoreSignPhoto();
            String signPhotoStr = signPhotoObj != null ? signPhotoObj.toString() : "";
            store.setStoreSignPhoto(signPhotoStr);
        }
        
        // 외관 사진 파일명 저장
        if (storeFrontPhoto != null && !storeFrontPhoto.isEmpty()) {
            store.setStoreProntPhoto(storeFrontPhoto.getOriginalFilename()); // storeFrontPhoto -> storeProntPhoto
        } else {
            // Object 타입 처리
            Object frontPhotoObj = dto.getStoreInfo() != null ? dto.getStoreInfo().getStoreFrontPhoto() : dto.getStoreFrontPhoto();
            String frontPhotoStr = frontPhotoObj != null ? frontPhotoObj.toString() : "";
            store.setStoreProntPhoto(frontPhotoStr);
        }
        
        // 매니저 정보 처리
        if (hasManager != null && hasManager && managerId != null) {
            // managerId로 user_tesseris 테이블에서 user_index 찾기
            Integer managerUserIndex = findManagerUserIndex(managerId);
            store.setBusinessManUserIndex(managerUserIndex);
        }
        
        // 약관 동의 정보 (새로운 구조에서 추출)
        Boolean agreementOptional1 = dto.getAgreements() != null ? dto.getAgreements().getOptional1() : dto.getAgreementOptional1();
        Boolean agreementOptional2 = dto.getAgreements() != null ? dto.getAgreements().getOptional2() : dto.getAgreementOptional2();
        Boolean agreementOptional3 = dto.getAgreements() != null ? dto.getAgreements().getOptional3() : dto.getAgreementOptional3();
        
        store.setStoreMarketingAgree(agreementOptional1 != null ? agreementOptional1.toString() : "false");
        store.setStoreAedAgree(agreementOptional2 != null ? agreementOptional2.toString() : "false");
        store.setStoreLowAgree(agreementOptional3 != null ? agreementOptional3.toString() : "false");
        
        // 기본값 설정
        store.setStoreTransactionStatus(false); // 거래 상태: 정지
        store.setStoreRequestStatusIndex(1); // 신청 상태: 대기
        store.setStoreRegistrationDate(LocalDateTime.now());
        store.setStoreCreateDate(LocalDateTime.now());
        
        return store;
    }
    
    /**
     * TemporaryStoreMaster 엔티티 생성
     */
    private TemporaryStoreMaster createTemporaryStoreMasterEntity(Store store) {
        LocalDateTime now = LocalDateTime.now();
        
        return TemporaryStoreMaster.builder()
            .storeUserIndex(store)
            .temporaryStoreMasterTransactionName("가맹신청비")
            .temporaryStoreMasterChargeTime(now)
            .temporaryStoreMasterDistributionTime(now)
            .temporaryStoreCmValue(500000)
            .temporaryStoreCashValue(500000)
            .temporaryStoreMasterDistributionStatus("y")
            .build();
    }

    /**
     * TemporaryStoreDetail 엔티티들 생성 (복잡한 로직)
     * 3. temporary_store_detail에 insert하기. (business_man_user_index 하나당 1개 칼럼 insert)
     */
    private void createTemporaryStoreDetailEntities(Store store, TemporaryStoreMaster temporaryStoreMaster) {
        Integer businessManUserIndex = store.getBusinessManUserIndex();
        
        if (businessManUserIndex != null) {
            processBusinessManHierarchy(businessManUserIndex, temporaryStoreMaster);
        } else {
            // business_man_user_index가 없는 경우 기본 레코드 생성
            createDefaultTemporaryStoreDetail(store, temporaryStoreMaster);
        }
    }
    
    /**
     * business_man 계층 구조를 처리하여 temporary_store_detail 레코드들 생성
     */
    private void processBusinessManHierarchy(Integer businessManUserIndex, TemporaryStoreMaster temporaryStoreMaster) {
        Integer currentBusinessManUserIndex = businessManUserIndex;
        
        while (currentBusinessManUserIndex != null) {
            // UserTesseris 먼저 찾기
            UserTesseris currentUserTesseris = userTesserisRepository.findById(currentBusinessManUserIndex).orElse(null);
            if (currentUserTesseris == null) {
                log.warn("UserTesseris를 찾을 수 없습니다: user_index={}", currentBusinessManUserIndex);
                break;
            }
            
            // business_man 테이블에서 UserTesseris로 찾기
            BusinessMan businessMan = businessManRepository.findByUserIndex(currentUserTesseris).orElse(null);
            
            if (businessMan == null) {
                log.warn("BusinessMan을 찾을 수 없습니다: user_index={}", currentBusinessManUserIndex);
                break;
            }
            
            // business_grade와 business_area 정보 가져오기
            BusinessGrade businessGrade = businessMan.getBusinessGrade();
            BusinessArea businessArea = businessMan.getBusinessArea();
            
            if (businessGrade == null) {
                log.warn("BusinessGrade가 없습니다: business_man_index={}", businessMan.getBusinessManIndex());
                break;
            }
            
            // temporary_store_value 계산: business_grade_rate * 20000 (소숫점 제거)
            Double temporaryStoreValueRaw = (businessGrade.getBusinessGradeRate() != null) 
                ? businessGrade.getBusinessGradeRate() * 20000.0 
                : 0.0;
            Double temporaryStoreValue = Math.floor(temporaryStoreValueRaw); // 소숫점 이하 버림
            
            // TemporaryStoreDetail 생성 및 저장
            TemporaryStoreDetail temporaryStoreDetail = TemporaryStoreDetail.builder()
                .userIndex(currentUserTesseris)
                .temporaryStoreMasterIndex(temporaryStoreMaster)
                .businessGradeName(businessGrade.getBusinessGradeName())
                .businessAreaName(businessArea != null ? businessArea.getBusinessAreaName() : "전국")
                .temporaryStoreCmValue(temporaryStoreValue)
                .temporaryStoreCashValue(temporaryStoreValue)
                .build();
            
            TemporaryStoreDetail saved = temporaryStoreDetailRepository.save(temporaryStoreDetail);
            log.info("TemporaryStoreDetail 저장: user_index={}, business_grade={}, temporary_store_value={}, detail_index={}", 
                    currentBusinessManUserIndex, businessGrade.getBusinessGradeName(), temporaryStoreValue, saved.getTemporaryStoreDetailIndex());
            
            // 다음 상위 business_man 찾기 (boss_user_index)
            currentBusinessManUserIndex = businessMan.getBossUserIndex();
        }
    }
    
    /**
     * 기본 TemporaryStoreDetail 생성 (business_man_user_index가 없는 경우)
     */
    private void createDefaultTemporaryStoreDetail(Store store, TemporaryStoreMaster temporaryStoreMaster) {
        UserTesseris userTesseris = store.getUserIndex();
        
        TemporaryStoreDetail temporaryStoreDetail = TemporaryStoreDetail.builder()
            .userIndex(userTesseris)
            .temporaryStoreMasterIndex(temporaryStoreMaster)
            .businessGradeName("일반") // 기본값
            .businessAreaName("전국") // 기본값
            .temporaryStoreCmValue(500000.0)
            .temporaryStoreCashValue(500000.0)
            .build();
        
        TemporaryStoreDetail saved = temporaryStoreDetailRepository.save(temporaryStoreDetail);
        log.info("기본 TemporaryStoreDetail 저장: detail_index={}", saved.getTemporaryStoreDetailIndex());
    }
    
    /**
     * UserTesseris의 user_role_index 변경 (1 -> 3) - UserTesseris 객체 직접 사용
     */
    private void updateUserRoleByUserTesseris(UserTesseris userTesseris) {
        log.info("사용자 역할 변경 시작: userIndex={}", userTesseris.getUserIndex());
        
        try {
            Integer oldRoleIndex = userTesseris.getUserRoleIndex();
            
            userTesseris.setUserRoleIndex(3); // 1(일반) -> 3(가맹점)
            UserTesseris savedUserTesseris = userTesserisRepository.save(userTesseris);
            
            log.info("사용자 역할 변경 성공: userIndex={}, 이전역할={}, 새역할={}", 
                    savedUserTesseris.getUserIndex(), oldRoleIndex, savedUserTesseris.getUserRoleIndex());
            
        } catch (Exception e) {
            log.error("사용자 역할 변경 실패: userIndex={}", userTesseris.getUserIndex(), e);
            throw e;
        }
    }

    /**
     * UserTesseris의 user_role_index 변경 (1 -> 3) - userId 문자열 사용
     */
    private void updateUserRole(String userId) {
        log.info("사용자 역할 변경 시작: userId={}", userId);
        
        // null 체크
        if (userId == null || userId.trim().isEmpty()) {
            log.error("userId가 null이거나 빈 문자열입니다");
            throw new RuntimeException("유효하지 않은 userId입니다: " + userId);
        }
        
        try {
            // userId로 UserEntity 조회
            UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("UserEntity를 찾을 수 없습니다: " + userId));
            
            log.info("UserEntity 조회 성공: userId={}, name={}", userId, userEntity.getName());
            
            // UserTesseris 조회
            List<UserTesseris> userTesserisList = userTesserisRepository.findByUsersId(userEntity);
            if (userTesserisList.isEmpty()) {
                log.error("UserTesseris 목록이 비어있습니다: userId={}", userId);
                throw new RuntimeException("UserTesseris를 찾을 수 없습니다: " + userId);
            }
            
            log.info("UserTesseris 조회 성공: userId={}, 목록 크기={}", userId, userTesserisList.size());
            
            // 첫 번째 UserTesseris 사용하여 역할 변경
            updateUserRoleByUserTesseris(userTesserisList.get(0));
            
        } catch (Exception e) {
            log.error("사용자 역할 변경 실패: userId={}", userId, e);
            throw e;
        }
    }

    /**
     * managerId로 user_tesseris 테이블에서 user_index 찾기 (business_man 테이블에 존재하는지 검증)
     */
    private Integer findManagerUserIndex(String managerId) {
        try {
            // managerId로 UserEntity 조회
            UserEntity managerUser = userRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("매니저 UserEntity를 찾을 수 없습니다: " + managerId));
            
            // UserEntity로 UserTesseris 조회
            List<UserTesseris> managerTesserisList = userTesserisRepository.findByUsersId(managerUser);
            if (managerTesserisList.isEmpty()) {
                throw new RuntimeException("매니저 UserTesseris를 찾을 수 없습니다: " + managerId);
            }
            
            UserTesseris managerUserTesseris = managerTesserisList.get(0);
            Integer userIndex = managerUserTesseris.getUserIndex();
            
            // 해당 user_index가 business_man 테이블에 존재하는지 검증
            BusinessMan businessMan = businessManRepository.findByUserIndex(managerUserTesseris).orElse(null);
            
            if (businessMan == null) {
                log.warn("매니저 {}(user_index={})는 business_man 테이블에 존재하지 않습니다. business_man_user_index를 null로 설정합니다.", managerId, userIndex);
                return null; // business_man 테이블에 없으면 null 반환
            }
            
            log.info("매니저 user_index 조회 성공: managerId={}, user_index={}", managerId, userIndex);
            return userIndex; // UserTesseris의 user_index 반환
            
        } catch (Exception e) {
            log.error("매니저 user_index 조회 실패: managerId={}", managerId, e);
            return null; // 에러 발생시 null 반환하여 외래키 제약조건 회피
        }
    }
} 