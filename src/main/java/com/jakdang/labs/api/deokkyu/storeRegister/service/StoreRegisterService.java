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
import com.jakdang.labs.api.deokkyu.store.repository.StoreSubscriptionFeehdkRepo;
import com.jakdang.labs.api.deokkyu.modal_admin.repository.UserCmLoghdkRepo;
import com.jakdang.labs.api.deokkyu.store.repository.UserCmhdkRepo;
import com.jakdang.labs.entity.BusinessMan;
import com.jakdang.labs.entity.BusinessGrade;
import com.jakdang.labs.entity.BusinessArea;
import com.jakdang.labs.entity.StoreSubscriptionFee;
import com.jakdang.labs.entity.UserCmLog;
import com.jakdang.labs.entity.UserCm;

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
    private final StoreSubscriptionFeehdkRepo storeSubscriptionFeeRepository;
    private final UserCmLoghdkRepo userCmLogRepository;
    private final UserCmhdkRepo userCmRepository;
    private final GeocodingService geocodingService;
    private final S3FileUploadService s3FileUploadService;
    
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
            
            // 2-1. StoreSubscriptionFee 테이블에 저장
            createStoreSubscriptionFeeEntity(savedStore);
            log.info("StoreSubscriptionFee 테이블 저장 완료");
            
            // 3. TemporaryStoreDetail 테이블에 저장 (복잡한 로직)
            createTemporaryStoreDetailEntities(savedStore, savedTemporaryStoreMaster);
            log.info("TemporaryStoreDetail 테이블 저장 완료");
            
            // 4. UserTesseris 테이블의 user_role_index 변경 (1 -> 3)
            // Store에서 UserTesseris 정보를 활용하여 역할 변경
            updateUserRoleByUserTesseris(savedStore.getUserIndex());
            log.info("UserTesseris user_role_index 변경 완료");
            
            // 파일 S3 업로드 처리 (DB에는 파일명만 저장, S3에는 실제 파일 저장)
            String storeId = savedStore.getStoreIndex().toString();
            log.info("📁 S3 파일 업로드 처리 시작 - storeId: {}", storeId);
            
            // 사업자등록증 사진 S3 업로드
            if (storeBusinessLicensePhoto != null && !storeBusinessLicensePhoto.isEmpty()) {
                try {
                    log.info("📄 사업자등록증 사진 S3 업로드 시작: {}, 크기: {} bytes", 
                            storeBusinessLicensePhoto.getOriginalFilename(), storeBusinessLicensePhoto.getSize());
                    String businessLicenseUrl = s3FileUploadService.uploadStoreFile(storeBusinessLicensePhoto, "business_license", storeId);
                    if (businessLicenseUrl != null) {
                        log.info("✅ 사업자등록증 사진 S3 업로드 성공: {} (DB 파일명: {})", 
                                businessLicenseUrl, savedStore.getStoreBusinessLicensePhoto());
                    } else {
                        log.error("❌ 사업자등록증 사진 S3 업로드 실패 - URL이 null");
                    }
                } catch (Exception e) {
                    log.error("❌ 사업자등록증 사진 S3 업로드 중 오류 발생", e);
                }
            } else {
                log.info("📄 사업자등록증 사진 파일이 없음 - S3 업로드 생략");
            }
            
            // 간판 사진 S3 업로드  
            if (storeSignPhoto != null && !storeSignPhoto.isEmpty()) {
                try {
                    log.info("🪧 간판 사진 S3 업로드 시작: {}, 크기: {} bytes", 
                            storeSignPhoto.getOriginalFilename(), storeSignPhoto.getSize());
                    String signPhotoUrl = s3FileUploadService.uploadStoreFile(storeSignPhoto, "sign_photo", storeId);
                    if (signPhotoUrl != null) {
                        log.info("✅ 간판 사진 S3 업로드 성공: {} (DB 파일명: {})", 
                                signPhotoUrl, savedStore.getStoreSignPhoto());
                    } else {
                        log.error("❌ 간판 사진 S3 업로드 실패 - URL이 null");
                    }
                } catch (Exception e) {
                    log.error("❌ 간판 사진 S3 업로드 중 오류 발생", e);
                }
            } else {
                log.info("🪧 간판 사진 파일이 없음 - S3 업로드 생략");
            }
            
            // 외관 사진 S3 업로드
            if (storeFrontPhoto != null && !storeFrontPhoto.isEmpty()) {
                try {
                    log.info("🏪 외관 사진 S3 업로드 시작: {}, 크기: {} bytes", 
                            storeFrontPhoto.getOriginalFilename(), storeFrontPhoto.getSize());
                    String frontPhotoUrl = s3FileUploadService.uploadStoreFile(storeFrontPhoto, "front_photo", storeId);
                    if (frontPhotoUrl != null) {
                        log.info("✅ 외관 사진 S3 업로드 성공: {} (DB 파일명: {})", 
                                frontPhotoUrl, savedStore.getStoreProntPhoto());
                    } else {
                        log.error("❌ 외관 사진 S3 업로드 실패 - URL이 null");
                    }
                } catch (Exception e) {
                    log.error("❌ 외관 사진 S3 업로드 중 오류 발생", e);
                }
            } else {
                log.info("🏪 외관 사진 파일이 없음 - S3 업로드 생략");
            }
            
            response.put("success", true);
            response.put("message", "가맹점 신청이 성공적으로 등록되었습니다");
            response.put("storeId", savedStore.getStoreIndex());
            response.put("status", storeRegisterDto.getStatus());
            
            log.info("가맹점 신청 등록 완료");
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
            String originalFilename = storeBusinessLicensePhoto.getOriginalFilename();
            store.setStoreBusinessLicensePhoto(originalFilename);
            log.info("사업자등록증 파일명 설정: {}", originalFilename);
        } else {
            // Object 타입 처리 (빈 객체 {} 처리 포함)
            Object businessLicensePhotoObj = dto.getBusinessInfo() != null ? dto.getBusinessInfo().getStoreBusinessLicensePhoto() : dto.getStoreBusinessLicensePhoto();
            String businessLicensePhotoStr = "";
            if (businessLicensePhotoObj != null) {
                String objStr = businessLicensePhotoObj.toString();
                // 빈 객체 "{}" 나 "null"이 아닌 경우만 저장
                if (!objStr.equals("{}") && !objStr.equals("null") && !objStr.trim().isEmpty()) {
                    businessLicensePhotoStr = objStr;
                }
            }
            store.setStoreBusinessLicensePhoto(businessLicensePhotoStr);
            log.info("사업자등록증 파일명 설정 (DTO에서): {}", businessLicensePhotoStr);
        }
        
        // 가맹점 등록 정보
        store.setStoreName(storeName);
        store.setStorePhone(storePhone);
        store.setStoreZoneCode(storePostcode); // storePostcode -> storeZoneCode
        store.setStoreAddress(storeAddress);
        store.setStoreDetailAddress(storeDetailAddress);
        store.setStoreSite(storeSite);
        
        // 1. 주소로 위도/경도 구하기 (지오코딩 실패시에도 계속 진행)
        log.info("=== 📍 지오코딩 처리 시작 ===");
        log.info("입력 주소: '{}'", storeAddress);
        log.info("입력 상세주소: '{}'", storeDetailAddress);
        
        if (storeAddress != null && !storeAddress.trim().isEmpty()) {
            String fullAddress = storeAddress;
            if (storeDetailAddress != null && !storeDetailAddress.trim().isEmpty()) {
                fullAddress = storeAddress + " " + storeDetailAddress;
            }
            
            log.info("🌐 카카오 지오코딩 API 요청 - 전체주소: '{}'", fullAddress);
            
            try {
                String[] latLng = geocodingService.getLatLngAsString(fullAddress);
                log.info("🌐 카카오 지오코딩 API 응답: {}", latLng != null ? java.util.Arrays.toString(latLng) : "null");
                
                if (latLng != null && latLng.length >= 2 && !latLng[0].isEmpty() && !latLng[1].isEmpty()) {
                    store.setStorePos1(latLng[0]); // 위도
                    store.setStorePos2(latLng[1]); // 경도
                    log.info("✅ 지오코딩 성공 - 주소: '{}' → 위도: {}, 경도: {}", fullAddress, latLng[0], latLng[1]);
                    log.info("✅ Store 엔티티에 좌표 저장 완료: storePos1={}, storePos2={}", store.getStorePos1(), store.getStorePos2());
                } else {
                    store.setStorePos1(""); // 빈 값 설정
                    store.setStorePos2(""); // 빈 값 설정
                    log.warn("⚠️ 지오코딩 실패 - 주소: '{}', 응답 데이터가 유효하지 않음", fullAddress);
                    log.warn("⚠️ 좌표를 빈 값으로 설정: storePos1='', storePos2=''");
                }
            } catch (Exception e) {
                store.setStorePos1(""); // 빈 값 설정
                store.setStorePos2(""); // 빈 값 설정
                log.error("❌ 지오코딩 API 호출 중 오류 발생 - 주소: '{}'", fullAddress);
                log.error("❌ 오류 내용: {}", e.getMessage());
                log.error("❌ 좌표를 빈 값으로 설정: storePos1='', storePos2=''");
            }
        } else {
            store.setStorePos1(""); // 주소가 없으면 빈 값
            store.setStorePos2(""); // 주소가 없으면 빈 값
            log.warn("⚠️ 주소 정보가 없음 - 좌표를 빈 값으로 설정");
        }
        
        log.info("=== 📍 지오코딩 처리 완료 ===");
        
        // 간판 사진 파일명 저장
        if (storeSignPhoto != null && !storeSignPhoto.isEmpty()) {
            String originalFilename = storeSignPhoto.getOriginalFilename();
            store.setStoreSignPhoto(originalFilename);
            log.info("간판 사진 파일명 설정: {}", originalFilename);
        } else {
            // Object 타입 처리 (빈 객체 {} 처리 포함)
            Object signPhotoObj = dto.getStoreInfo() != null ? dto.getStoreInfo().getStoreSignPhoto() : dto.getStoreSignPhoto();
            String signPhotoStr = "";
            if (signPhotoObj != null) {
                String objStr = signPhotoObj.toString();
                // 빈 객체 "{}" 나 "null"이 아닌 경우만 저장
                if (!objStr.equals("{}") && !objStr.equals("null") && !objStr.trim().isEmpty()) {
                    signPhotoStr = objStr;
                }
            }
            store.setStoreSignPhoto(signPhotoStr);
            log.info("간판 사진 파일명 설정 (DTO에서): {}", signPhotoStr);
        }
        
        // 외관 사진 파일명 저장
        if (storeFrontPhoto != null && !storeFrontPhoto.isEmpty()) {
            String originalFilename = storeFrontPhoto.getOriginalFilename();
            store.setStoreProntPhoto(originalFilename); // storeFrontPhoto -> storeProntPhoto
            log.info("외관 사진 파일명 설정: {}", originalFilename);
        } else {
            // Object 타입 처리 (빈 객체 {} 처리 포함)
            Object frontPhotoObj = dto.getStoreInfo() != null ? dto.getStoreInfo().getStoreFrontPhoto() : dto.getStoreFrontPhoto();
            String frontPhotoStr = "";
            if (frontPhotoObj != null) {
                String objStr = frontPhotoObj.toString();
                // 빈 객체 "{}" 나 "null"이 아닌 경우만 저장
                if (!objStr.equals("{}") && !objStr.equals("null") && !objStr.trim().isEmpty()) {
                    frontPhotoStr = objStr;
                }
            }
            store.setStoreProntPhoto(frontPhotoStr);
            log.info("외관 사진 파일명 설정 (DTO에서): {}", frontPhotoStr);
        }
        
        // 매니저 정보 처리
        log.info("=== 👥 매니저 정보 처리 시작 ===");
        log.info("hasManager: {}, managerId(email): {}", hasManager, managerId);
        
        if (hasManager != null && hasManager && managerId != null) {
            // managerEmail로 user_tesseris 테이블에서 user_index 찾기
            log.info("🔍 매니저 정보 조회 시작: email={}", managerId);
            Integer managerUserIndex = findManagerUserIndex(managerId);
            store.setBusinessManUserIndex(managerUserIndex);
            log.info("✅ 매니저 정보 설정 완료: businessManUserIndex={}", managerUserIndex);
            
            // 중요: Store에 설정된 값 재확인
            log.info("🔍 Store에 설정된 businessManUserIndex 재확인: {}", store.getBusinessManUserIndex());
        } else {
            log.info("ℹ️ 매니저 정보 없음 - businessManUserIndex는 null로 설정");
            store.setBusinessManUserIndex(null);
        }
        
        log.info("=== 👥 매니저 정보 처리 완료 ===");
        
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
            .temporaryStoreCmValue(200000)
            .temporaryStoreCashValue(1)
            .temporaryStoreMasterDistributionStatus("y")
            .build();
    }
    
    /**
     * StoreSubscriptionFee 엔티티 생성 및 저장
     */
    private void createStoreSubscriptionFeeEntity(Store store) {
        LocalDateTime now = LocalDateTime.now();
        
        log.info("=== 💰 StoreSubscriptionFee 저장 시작 ===");
        
        // Store의 business_man_user_index 확인
        Integer businessManUserIndex = store.getBusinessManUserIndex();
        log.info("🔍 Store에서 가져온 business_man_user_index: {}", businessManUserIndex);
        
        if (businessManUserIndex == null) {
            log.warn("⚠️ Store의 business_man_user_index가 null입니다. StoreSubscriptionFee 저장을 건너뜁니다.");
            return;
        }
        
        try {
            // UserTesseris로 BusinessMan 조회 (올바른 방식)
            log.info("🔍 UserTesseris 조회 시작: user_index={}", businessManUserIndex);
            UserTesseris userTesseris = userTesserisRepository.findById(businessManUserIndex)
                .orElseThrow(() -> new RuntimeException("UserTesseris를 찾을 수 없습니다: " + businessManUserIndex));
            log.info("✅ UserTesseris 조회 성공: user_index={}", userTesseris.getUserIndex());
            
            log.info("🔍 BusinessMan 조회 시작: UserTesseris.user_index={}", userTesseris.getUserIndex());
            BusinessMan businessMan = businessManRepository.findByUserIndex(userTesseris)
                .orElseThrow(() -> new RuntimeException("BusinessMan을 찾을 수 없습니다. user_index: " + businessManUserIndex));
            log.info("✅ BusinessMan 조회 성공: user_index={}, business_man_index={}", 
                    businessManUserIndex, businessMan.getBusinessManIndex());
            
            // StoreSubscriptionFee 엔티티 생성
            log.info("🔨 StoreSubscriptionFee 엔티티 생성 시작");
            StoreSubscriptionFee storeSubscriptionFee = new StoreSubscriptionFee();
            storeSubscriptionFee.setStoreSubscriptionFeeTransactionName("가맹비 납부");
            storeSubscriptionFee.setBusinessManUserIndex(businessMan); // BusinessMan 엔티티 설정
            storeSubscriptionFee.setStoreSubscriptionFeeTime(now);
            storeSubscriptionFee.setStoreUserIndex(store); // Store 엔티티 설정
            storeSubscriptionFee.setStoreSubscriptionFeePaymentMethod("카드");
            storeSubscriptionFee.setStoreSubscriptionFeeCommissionCheck("y");
            storeSubscriptionFee.setStoreSubscriptionFeeValue(200000);
            
            // 엔티티 설정 후 필드 검증
            log.info("🔍 StoreSubscriptionFee 필드 검증:");
            log.info("   - businessManUserIndex(엔티티): {}", storeSubscriptionFee.getBusinessManUserIndex() != null ? storeSubscriptionFee.getBusinessManUserIndex().getBusinessManIndex() : "null");
            log.info("   - storeUserIndex(엔티티): {}", storeSubscriptionFee.getStoreUserIndex() != null ? storeSubscriptionFee.getStoreUserIndex().getStoreIndex() : "null");
            log.info("   - transactionName: {}", storeSubscriptionFee.getStoreSubscriptionFeeTransactionName());
            
            // DB 저장 시도
            log.info("💾 StoreSubscriptionFee DB 저장 시도");
            StoreSubscriptionFee saved = storeSubscriptionFeeRepository.save(storeSubscriptionFee);
            log.info("✅ StoreSubscriptionFee 저장 성공!");
            log.info("   - store_subscription_fee_index: {}", saved.getStoreSubscriptionFeeIndex());
            log.info("   - business_man_user_index: {}", businessMan.getBusinessManIndex());
            log.info("   - store_user_index: {}", store.getStoreIndex());
            log.info("   - transaction_name: {}", saved.getStoreSubscriptionFeeTransactionName());
            log.info("   - value: {}", saved.getStoreSubscriptionFeeValue());
            
        } catch (Exception e) {
            log.error("❌ StoreSubscriptionFee 처리 중 오류 발생");
            log.error("❌ 오류 타입: {}", e.getClass().getSimpleName());
            log.error("❌ 오류 메시지: {}", e.getMessage());
            log.error("❌ 오류 상세: ", e);
            throw e;
        }
        
        log.info("=== 💰 StoreSubscriptionFee 저장 완료 ===");
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
            
            // user_cm_log 테이블에 INSERT (1:1 대응)
            createUserCmLogEntity(temporaryStoreMaster, saved, temporaryStoreValue.intValue());
            
            // user_cm 테이블 UPDATE (user_cm_deposit += user_cm_log_value)
            updateUserCmDeposit(currentUserTesseris.getUserIndex(), temporaryStoreValue.intValue());
            
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
        
        // user_cm_log 테이블에 INSERT (1:1 대응)
        createUserCmLogEntity(temporaryStoreMaster, saved, 500000);
        
        // user_cm 테이블 UPDATE (user_cm_deposit += user_cm_log_value)
        updateUserCmDeposit(userTesseris.getUserIndex(), 500000);
    }
    
    /**
     * UserCmLog 엔티티 생성 및 저장
     */
    private void createUserCmLogEntity(TemporaryStoreMaster temporaryStoreMaster, TemporaryStoreDetail temporaryStoreDetail, int logValue) {
        LocalDateTime now = LocalDateTime.now();
        
        UserCmLog userCmLog = UserCmLog.builder()
            .userCmLogPaymentIndex(1) // 입금
            .userCmpLogPaymentIndex(null) // null
            .userCmLogTransactionTypeIndex(1) // 중개수수료
            .userCmLogValueTypeIndex(2) // CM
            .userIndexEventTrigger(temporaryStoreMaster.getStoreUserIndex().getUserIndex()) // temporaryStoreMaster의 store_user_index
            .userIndexEventParty(temporaryStoreDetail.getUserIndex()) // temporaryStoreDetail의 user_index
            .userCmLogValue(logValue) // temporaryStoreValue
            .userCmLogReason(null) // null
            .userCmLogCreateTime(now) // 현재 시간
            .userCmLogTransactionCancel(null) // null (판매 취소용)
            .userCouponValue(0) // 0
            .build();
            
        UserCmLog saved = userCmLogRepository.save(userCmLog);
        log.info("UserCmLog 저장: log_index={}, trigger={}, party={}, value={}", 
                saved.getUserCmLogIndex(), 
                temporaryStoreMaster.getStoreUserIndex().getUserIndex().getUserIndex(),
                temporaryStoreDetail.getUserIndex().getUserIndex(),
                logValue);
    }
    
    /**
     * UserCm 테이블의 user_cm_deposit 업데이트
     */
    private void updateUserCmDeposit(Integer userIndex, int addValue) {
        // UserCm 조회 (user_cm_index = userIndex)
        UserCm userCm = userCmRepository.findById(userIndex).orElse(null);
        
        if (userCm == null) {
            // UserCm이 존재하지 않으면 새로 생성
            userCm = UserCm.builder()
                .userCmIndex(userIndex)
                .userCmDeposit(addValue)
                .userCmWithdrawal(0)
                .userCashDeposit(0)
                .userCashWithdrawal(0)
                .userCmpDeposit(0)
                .userCmpWithdrawal(0)
                .userCmpInit(0)
                .userCmPincode(null)
                .build();
            log.info("새 UserCm 생성: user_index={}, deposit={}", userIndex, addValue);
        } else {
            // 기존 UserCm의 user_cm_deposit 업데이트
            Integer currentDeposit = userCm.getUserCmDeposit() != null ? userCm.getUserCmDeposit() : 0;
            userCm.setUserCmDeposit(currentDeposit + addValue);
            log.info("UserCm 업데이트: user_index={}, 기존deposit={}, 추가값={}, 새deposit={}", 
                    userIndex, currentDeposit, addValue, currentDeposit + addValue);
        }
        
        userCmRepository.save(userCm);
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
     * managerEmail로 user_tesseris 테이블에서 user_index 찾기 (business_man 테이블에 존재하는지 검증)
     */
    private Integer findManagerUserIndex(String managerEmail) {
        try {
            log.info("=== 🔍 매니저 조회 프로세스 시작 ===");
            log.info("🔍 1단계: 매니저 이메일로 UserEntity 조회 - email: {}", managerEmail);
            
            // 1. managerEmail로 UserEntity 조회
            UserEntity managerUserByEmail = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new RuntimeException("매니저 UserEntity를 이메일로 찾을 수 없습니다: " + managerEmail));
            log.info("✅ 1단계 성공: 이메일로 UserEntity 조회 완료");
            
            // 2. UserEntity의 ID 얻기
            String managerId = managerUserByEmail.getId();
            log.info("🔍 2단계: UserEntity ID 추출 - userId: {}", managerId);
            
            // 3. 기존 로직대로 ID로 UserEntity 조회 (기존 방식 유지)
            log.info("🔍 3단계: ID로 UserEntity 재조회");
            UserEntity managerUser = userRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("매니저 UserEntity를 ID로 찾을 수 없습니다: " + managerId));
            log.info("✅ 3단계 성공: ID로 UserEntity 재조회 완료");
            
            // UserEntity로 UserTesseris 조회
            log.info("🔍 4단계: UserEntity로 UserTesseris 조회");
            List<UserTesseris> managerTesserisList = userTesserisRepository.findByUsersId(managerUser);
            if (managerTesserisList.isEmpty()) {
                throw new RuntimeException("매니저 UserTesseris를 찾을 수 없습니다: " + managerEmail);
            }
            log.info("✅ 4단계 성공: UserTesseris 조회 완료 - 목록 크기: {}", managerTesserisList.size());
            
            UserTesseris managerUserTesseris = managerTesserisList.get(0);
            Integer userIndex = managerUserTesseris.getUserIndex();
            log.info("🔍 5단계: UserTesseris에서 user_index 추출 - user_index: {}", userIndex);
            
            // 해당 user_index가 business_man 테이블에 존재하는지 검증
            log.info("🔍 6단계: BusinessMan 테이블에서 존재 여부 검증");
            BusinessMan businessMan = businessManRepository.findByUserIndex(managerUserTesseris).orElse(null);
            
            if (businessMan == null) {
                log.warn("⚠️ 6단계 실패: 매니저 {}(user_index={})는 business_man 테이블에 존재하지 않습니다.", managerEmail, userIndex);
                log.warn("⚠️ business_man_user_index를 null로 설정합니다.");
                return null; // business_man 테이블에 없으면 null 반환
            }
            
            log.info("✅ 6단계 성공: BusinessMan 조회 완료 - business_man_index: {}", businessMan.getBusinessManIndex());
            log.info("🎯 최종 결과: 매니저 user_index={} 반환", userIndex);
            log.info("=== 🔍 매니저 조회 프로세스 완료 ===");
            
            return userIndex; // UserTesseris의 user_index 반환
            
        } catch (Exception e) {
            log.error("❌ 매니저 조회 프로세스 실패");
            log.error("❌ 매니저 이메일: {}", managerEmail);
            log.error("❌ 오류 타입: {}", e.getClass().getSimpleName());
            log.error("❌ 오류 메시지: {}", e.getMessage());
            log.error("❌ 오류 상세: ", e);
            log.warn("⚠️ null 반환하여 외래키 제약조건 회피");
            return null; // 에러 발생시 null 반환하여 외래키 제약조건 회피
        }
    }
} 