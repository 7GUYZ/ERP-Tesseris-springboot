package com.jakdang.labs.api.deokkyu.storeRegister.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakdang.labs.api.deokkyu.storeRegister.dto.StoreRegisterRequestDto;
import com.jakdang.labs.entity.Store;
import com.jakdang.labs.entity.TemporaryStoreMaster;
import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.api.deokkyu.store.repository.StorehdkRepo;
import com.jakdang.labs.api.deokkyu.businessman.repository.TemporaryStoreMasterhdkRepo;
import com.jakdang.labs.api.deokkyu.store.repository.UserhdkRepo;
import com.jakdang.labs.api.deokkyu.store.repository.UserTesserishdkRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreRegisterService {
    
    private final ObjectMapper objectMapper;
    private final StorehdkRepo storeRepository;
    private final TemporaryStoreMasterhdkRepo temporaryStoreMasterRepository;
    private final UserhdkRepo userRepository;
    private final UserTesserishdkRepo userTesserisRepository;
    
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
            
            // 3. UserTesseris 테이블의 user_role_index 변경 (1 -> 3)
            updateUserRole(storeRegisterDto.getUserId());
            log.info("UserTesseris user_role_index 변경 완료");
            
            // 파일 처리 로그
            if (storeBusinessLicensePhoto != null && !storeBusinessLicensePhoto.isEmpty()) {
                log.info("사업자등록증 사진: {}, 크기: {} bytes", storeBusinessLicensePhoto.getOriginalFilename(), storeBusinessLicensePhoto.getSize());
                // TODO: 파일 저장 로직 구현 필요
            }
            if (storeSignPhoto != null && !storeSignPhoto.isEmpty()) {
                log.info("간판 사진: {}, 크기: {} bytes", storeSignPhoto.getOriginalFilename(), storeSignPhoto.getSize());
                // TODO: 파일 저장 로직 구현 필요
            }
            if (storeFrontPhoto != null && !storeFrontPhoto.isEmpty()) {
                log.info("외관 사진: {}, 크기: {} bytes", storeFrontPhoto.getOriginalFilename(), storeFrontPhoto.getSize());
                // TODO: 파일 저장 로직 구현 필요
            }
            
            response.put("success", true);
            response.put("message", "가맹점 신청이 성공적으로 등록되었습니다");
            response.put("storeId", savedStore.getStoreIndex());
            response.put("status", storeRegisterDto.getStatus());
            
            log.info("가맹점 신청 등록 완료");
            return response;
            
        } catch (Exception e) {
            log.error("가맹점 신청 등록 실패: {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }
    }
    
    /**
     * Store 엔티티 생성
     */
    private Store createStoreEntity(StoreRegisterRequestDto dto, MultipartFile storeBusinessLicensePhoto, 
                                   MultipartFile storeSignPhoto, MultipartFile storeFrontPhoto) {
        // user_index 조회 (로컬스토리지의 user-info에서 가져온 user_index 사용)
        Integer userIndex = dto.getUserIndex(); // 이미 Integer 타입
        
        // UserTesseris 조회
        UserTesseris userTesseris = userTesserisRepository.findById(userIndex)
            .orElseThrow(() -> new RuntimeException("UserTesseris를 찾을 수 없습니다: " + userIndex));
        
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
        
        // 파일명 저장 (실제 파일이 있는 경우 파일명, 없는 경우 기존 값 사용)
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
        
        // 파일명 저장
        if (storeSignPhoto != null && !storeSignPhoto.isEmpty()) {
            store.setStoreSignPhoto(storeSignPhoto.getOriginalFilename());
        } else {
            // Object 타입 처리
            Object signPhotoObj = dto.getStoreInfo() != null ? dto.getStoreInfo().getStoreSignPhoto() : dto.getStoreSignPhoto();
            String signPhotoStr = signPhotoObj != null ? signPhotoObj.toString() : "";
            store.setStoreSignPhoto(signPhotoStr);
        }
        
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
     * UserTesseris의 user_role_index 변경 (1 -> 3)
     */
    private void updateUserRole(String userId) {
        // userId로 UserEntity 조회
        UserEntity userEntity = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("UserEntity를 찾을 수 없습니다: " + userId));
        
        // UserTesseris 조회
        List<UserTesseris> userTesserisList = userTesserisRepository.findByUsersId(userEntity);
        if (userTesserisList.isEmpty()) {
            throw new RuntimeException("UserTesseris를 찾을 수 없습니다: " + userId);
        }
        
        // 첫 번째 UserTesseris 사용
        UserTesseris userTesseris = userTesserisList.get(0);
        userTesseris.setUserRoleIndex(3); // 1(일반) -> 3(가맹점)
        userTesserisRepository.save(userTesseris);
    }

    /**
     * managerId로 user_tesseris 테이블에서 user_index 찾기
     */
    private Integer findManagerUserIndex(String managerId) {
        // managerId로 UserEntity 조회
        UserEntity managerUser = userRepository.findById(managerId)
            .orElseThrow(() -> new RuntimeException("매니저 UserEntity를 찾을 수 없습니다: " + managerId));
        
        // UserEntity로 UserTesseris 조회
        List<UserTesseris> managerTesserisList = userTesserisRepository.findByUsersId(managerUser);
        if (managerTesserisList.isEmpty()) {
            throw new RuntimeException("매니저 UserTesseris를 찾을 수 없습니다: " + managerId);
        }
        
        // 첫 번째 UserTesseris의 user_index 반환
        return managerTesserisList.get(0).getUserIndex();
    }
} 