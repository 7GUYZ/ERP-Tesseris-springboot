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
import com.jakdang.labs.entity.StoreImage;
import com.jakdang.labs.api.deokkyu.storeRegister.service.S3FileUploadService;
import com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.repository.StoreImageJdbRepo;

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
    private final StoreImageJdbRepo storeImageRepository;
    
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
            log.info("=== 🚀 가맹점 신청 등록 처리 시작 ===");
            

            
            // === 1. 파라미터 확인 ===
            log.info("📊 입력 파라미터 확인:");
            log.info("   - storeData 길이: {}", storeData != null ? storeData.length() : "null");
            if (storeData != null && storeData.length() > 0) {
                log.info("   - storeData 미리보기: {}", storeData.substring(0, Math.min(200, storeData.length())) + "...");
            }
            
            log.info("📁 파일 파라미터 상세 확인:");
            log.info("   - storeBusinessLicensePhoto: {}", storeBusinessLicensePhoto != null ? 
                    String.format("파일명=%s, 크기=%d bytes, Content-Type=%s, 비어있는가=%s", 
                            storeBusinessLicensePhoto.getOriginalFilename(), 
                            storeBusinessLicensePhoto.getSize(), 
                            storeBusinessLicensePhoto.getContentType(),
                            storeBusinessLicensePhoto.isEmpty()) : "null");
            log.info("   - storeSignPhoto: {}", storeSignPhoto != null ? 
                    String.format("파일명=%s, 크기=%d bytes, Content-Type=%s, 비어있는가=%s", 
                            storeSignPhoto.getOriginalFilename(), 
                            storeSignPhoto.getSize(), 
                            storeSignPhoto.getContentType(),
                            storeSignPhoto.isEmpty()) : "null");
            log.info("   - storeFrontPhoto: {}", storeFrontPhoto != null ? 
                    String.format("파일명=%s, 크기=%d bytes, Content-Type=%s, 비어있는가=%s", 
                            storeFrontPhoto.getOriginalFilename(), 
                            storeFrontPhoto.getSize(), 
                            storeFrontPhoto.getContentType(),
                            storeFrontPhoto.isEmpty()) : "null");
            
            // S3 설정 확인
            log.info("⚙️ S3 설정 확인:");
            log.info("   - s3FileUploadService 인스턴스: {}", s3FileUploadService != null ? "존재함" : "NULL!");
            if (s3FileUploadService != null) {
                log.info("   - s3FileUploadService 클래스: {}", s3FileUploadService.getClass().getName());
            }
            
            // ObjectMapper 설정 - 알 수 없는 필드 무시
            ObjectMapper customObjectMapper = new ObjectMapper();
            customObjectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            
            // JSON 문자열을 DTO로 변환
            log.info("🔄 JSON 문자열을 DTO로 변환 시작");
            StoreRegisterRequestDto storeRegisterDto = customObjectMapper.readValue(storeData, StoreRegisterRequestDto.class);
            log.info("✅ JSON 변환 완료");
            
            // === 0. userIndex 정보 확인 ===
            log.info("🔍 === USER INDEX 정보 확인 ===");
            log.info("   - dto.getUserIndex(): {}", storeRegisterDto.getUserIndex());
            if (storeRegisterDto.getUserInfo() != null) {
                log.info("   - dto.getUserInfo().getUser_index(): {}", storeRegisterDto.getUserInfo().getUser_index());
                log.info("   - dto.getUserInfo().getName(): '{}'", storeRegisterDto.getUserInfo().getName());
                log.info("   - dto.getUserInfo().getPhone(): '{}'", storeRegisterDto.getUserInfo().getPhone());
            } else {
                log.info("   - dto.getUserInfo(): null");
            }
            log.info("🔍 === USER INDEX 정보 확인 완료 ===");
            
            // 기본 정보 설정
            storeRegisterDto.setStatus("PENDING"); // 대기 상태로 설정
            storeRegisterDto.setCreatedAt(LocalDateTime.now());
            
            log.info("📋 변환된 가맹점 신청 데이터: {}", storeRegisterDto);
            
            // === 2. 주소 정보 확인 ===
            log.info("🏠 주소 정보 추출:");
            String storeAddress = storeRegisterDto.getStoreInfo() != null ? storeRegisterDto.getStoreInfo().getStore_address() : storeRegisterDto.getStoreAddress();
            String storeDetailAddress = storeRegisterDto.getStoreInfo() != null ? storeRegisterDto.getStoreInfo().getStore_detail_address() : storeRegisterDto.getStoreDetailAddress();
            log.info("   - 기본주소: '{}'", storeAddress);
            log.info("   - 상세주소: '{}'", storeDetailAddress);
            
            String fullAddress = storeAddress;
            if (storeDetailAddress != null && !storeDetailAddress.trim().isEmpty()) {
                fullAddress = storeAddress + " " + storeDetailAddress;
            }
            log.info("   - 전체주소: '{}'", fullAddress);
            
            // === 3. Store 엔티티 생성 ===
            log.info("🏢 Store 엔티티 생성 시작");
            Store store = createStoreEntity(storeRegisterDto, storeBusinessLicensePhoto, storeSignPhoto, storeFrontPhoto);
            log.info("✅ Store 엔티티 생성 완료");
            
            // === 4. Store 엔티티 정보 확인 ===
            log.info("📋 생성된 Store 엔티티 정보:");
            log.info("   - 사업자등록증 파일명: '{}'", store.getStoreBusinessLicensePhoto());
            log.info("   - 간판 사진 파일명: '{}'", store.getStoreSignPhoto());
            log.info("   - 외관 사진 파일명: '{}'", store.getStoreProntPhoto());
            log.info("   - 주소: '{}'", store.getStoreAddress());
            log.info("   - 상세주소: '{}'", store.getStoreDetailAddress());
            log.info("   - 위도(storePos1): '{}'", store.getStorePos1());
            log.info("   - 경도(storePos2): '{}'", store.getStorePos2());
            log.info("   - business_man_user_index: {}", store.getBusinessManUserIndex());
            
            // === 5. Store DB 저장 ===
            log.info("💾 Store 테이블 저장 시작");
            Store savedStore = storeRepository.save(store);
            log.info("✅ Store 테이블 저장 완료: store_index={}", savedStore.getStoreIndex());
            
            // === 6. 저장된 Store 정보 재확인 ===
            log.info("🔍 DB에 저장된 Store 정보 재확인:");
            log.info("   - store_index: {}", savedStore.getStoreIndex());
            log.info("   - 사업자등록증 파일명: '{}'", savedStore.getStoreBusinessLicensePhoto());
            log.info("   - 간판 사진 파일명: '{}'", savedStore.getStoreSignPhoto());
            log.info("   - 외관 사진 파일명: '{}'", savedStore.getStoreProntPhoto());
            log.info("   - 위도(storePos1): '{}'", savedStore.getStorePos1());
            log.info("   - 경도(storePos2): '{}'", savedStore.getStorePos2());
            
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
            
            // === 7. S3 파일 업로드 처리 ===
            String storeId = savedStore.getStoreIndex().toString();
            log.info("=== 📁 S3 파일 업로드 처리 시작 ===");
            log.info("📁 storeId: {}", storeId);
            log.info("📁 s3FileUploadService 인스턴스: {}", s3FileUploadService != null ? "존재함" : "NULL!");
            
            // === 7-1. 사업자등록증 사진 S3 업로드 ===
            log.info("📄 사업자등록증 사진 S3 업로드 처리");
            if (storeBusinessLicensePhoto != null && !storeBusinessLicensePhoto.isEmpty()) {
                try {
                    log.info("📄 사업자등록증 사진 S3 업로드 시작: {}, 크기: {} bytes, 타입: {}", 
                            storeBusinessLicensePhoto.getOriginalFilename(), 
                            storeBusinessLicensePhoto.getSize(),
                            storeBusinessLicensePhoto.getContentType());
                    
                    log.info("📄 S3 업로드 시작: business_license 타입");
                    
                    String s3Url = s3FileUploadService.uploadStoreFile(storeBusinessLicensePhoto, "business_license", storeId);
                    
                    if (s3Url != null && !s3Url.isEmpty()) {
                        log.info("✅ 사업자등록증 사진 S3 업로드 성공!");
                        log.info("   - S3 URL: {}", s3Url);
                        log.info("   - DB 파일명: {}", savedStore.getStoreBusinessLicensePhoto());
                        log.info("   - 업로드 파일명: {}", storeBusinessLicensePhoto.getOriginalFilename());
                        
                        // 🔄 Store 테이블에 S3 URL 업데이트
                        log.info("🔄 Store 테이블 사업자등록증 URL 업데이트 시작");
                        savedStore.setStoreBusinessLicensePhoto(s3Url);
                        Store updatedStore = storeRepository.save(savedStore);
                        log.info("✅ Store 테이블 사업자등록증 URL 업데이트 완료: {}", s3Url);
                        
                        // 📋 StoreImage 테이블에도 저장
                        saveStoreImage(updatedStore, s3Url, "business_license");
                    } else {
                        log.error("❌ 사업자등록증 사진 S3 업로드 실패 - S3 URL이 null 또는 빈값");
                        log.error("   - 원본 파일명: {}", storeBusinessLicensePhoto.getOriginalFilename());
                        log.error("   - 파일 크기: {} bytes", storeBusinessLicensePhoto.getSize());
                        log.error("   - Content-Type: {}", storeBusinessLicensePhoto.getContentType());
                    }
                } catch (Exception e) {
                    log.error("❌ 사업자등록증 사진 S3 업로드 중 오류 발생");
                    log.error("   - 파일명: {}", storeBusinessLicensePhoto.getOriginalFilename());
                    log.error("   - 에러 타입: {}", e.getClass().getSimpleName());
                    log.error("   - 에러 메시지: {}", e.getMessage());
                    log.error("   - 에러 상세: ", e);
                }
            } else {
                log.info("📄 사업자등록증 사진 파일이 없음 - S3 업로드 생략");
                log.info("   - storeBusinessLicensePhoto null 여부: {}", storeBusinessLicensePhoto == null);
                if (storeBusinessLicensePhoto != null) {
                    log.info("   - 파일 isEmpty: {}", storeBusinessLicensePhoto.isEmpty());
                }
            }
            
            // === 7-2. 간판 사진 S3 업로드 ===
            log.info("🪧 간판 사진 S3 업로드 처리");
            if (storeSignPhoto != null && !storeSignPhoto.isEmpty()) {
                try {
                    log.info("🪧 간판 사진 S3 업로드 시작: {}, 크기: {} bytes, 타입: {}", 
                            storeSignPhoto.getOriginalFilename(), 
                            storeSignPhoto.getSize(),
                            storeSignPhoto.getContentType());
                    
                    log.info("🪧 S3 업로드 시작: sign_photo 타입");
                    
                    String s3Url = s3FileUploadService.uploadStoreFile(storeSignPhoto, "sign_photo", storeId);
                    
                    if (s3Url != null && !s3Url.isEmpty()) {
                        log.info("✅ 간판 사진 S3 업로드 성공!");
                        log.info("   - S3 URL: {}", s3Url);
                        log.info("   - DB 파일명: {}", savedStore.getStoreSignPhoto());
                        log.info("   - 업로드 파일명: {}", storeSignPhoto.getOriginalFilename());
                        
                        // 🔄 Store 테이블에 S3 URL 업데이트
                        log.info("🔄 Store 테이블 간판사진 URL 업데이트 시작");
                        savedStore.setStoreSignPhoto(s3Url);
                        Store updatedStore = storeRepository.save(savedStore);
                        log.info("✅ Store 테이블 간판사진 URL 업데이트 완료: {}", s3Url);
                        
                        // 📋 StoreImage 테이블에도 저장
                        saveStoreImage(updatedStore, s3Url, "sign_photo");
                    } else {
                        log.error("❌ 간판 사진 S3 업로드 실패 - S3 URL이 null 또는 빈값");
                        log.error("   - 원본 파일명: {}", storeSignPhoto.getOriginalFilename());
                        log.error("   - 파일 크기: {} bytes", storeSignPhoto.getSize());
                        log.error("   - Content-Type: {}", storeSignPhoto.getContentType());
                    }
                } catch (Exception e) {
                    log.error("❌ 간판 사진 S3 업로드 중 오류 발생");
                    log.error("   - 파일명: {}", storeSignPhoto.getOriginalFilename());
                    log.error("   - 에러 타입: {}", e.getClass().getSimpleName());
                    log.error("   - 에러 메시지: {}", e.getMessage());
                    log.error("   - 에러 상세: ", e);
                }
            } else {
                log.info("🪧 간판 사진 파일이 없음 - S3 업로드 생략");
                log.info("   - storeSignPhoto null 여부: {}", storeSignPhoto == null);
                if (storeSignPhoto != null) {
                    log.info("   - 파일 isEmpty: {}", storeSignPhoto.isEmpty());
                }
            }
            
            // === 7-3. 외관 사진 S3 업로드 ===
            log.info("🏪 외관 사진 S3 업로드 처리");
            if (storeFrontPhoto != null && !storeFrontPhoto.isEmpty()) {
                try {
                    log.info("🏪 외관 사진 S3 업로드 시작: {}, 크기: {} bytes, 타입: {}", 
                            storeFrontPhoto.getOriginalFilename(), 
                            storeFrontPhoto.getSize(),
                            storeFrontPhoto.getContentType());
                    
                    log.info("🏪 S3 업로드 시작: front_photo 타입");
                    
                    String s3Url = s3FileUploadService.uploadStoreFile(storeFrontPhoto, "front_photo", storeId);
                    
                    if (s3Url != null && !s3Url.isEmpty()) {
                        log.info("✅ 외관 사진 S3 업로드 성공!");
                        log.info("   - S3 URL: {}", s3Url);
                        log.info("   - DB 파일명: {}", savedStore.getStoreProntPhoto());
                        log.info("   - 업로드 파일명: {}", storeFrontPhoto.getOriginalFilename());
                        
                        // 🔄 Store 테이블에 S3 URL 업데이트
                        log.info("🔄 Store 테이블 외관사진 URL 업데이트 시작");
                        savedStore.setStoreProntPhoto(s3Url);
                        Store updatedStore = storeRepository.save(savedStore);
                        log.info("✅ Store 테이블 외관사진 URL 업데이트 완료: {}", s3Url);
                        
                        // 📋 StoreImage 테이블에도 저장
                        saveStoreImage(updatedStore, s3Url, "front_photo");
                    } else {
                        log.error("❌ 외관 사진 S3 업로드 실패 - S3 URL이 null 또는 빈값");
                        log.error("   - 원본 파일명: {}", storeFrontPhoto.getOriginalFilename());
                        log.error("   - 파일 크기: {} bytes", storeFrontPhoto.getSize());
                        log.error("   - Content-Type: {}", storeFrontPhoto.getContentType());
                    }
                } catch (Exception e) {
                    log.error("❌ 외관 사진 S3 업로드 중 오류 발생");
                    log.error("   - 파일명: {}", storeFrontPhoto.getOriginalFilename());
                    log.error("   - 에러 타입: {}", e.getClass().getSimpleName());
                    log.error("   - 에러 메시지: {}", e.getMessage());
                    log.error("   - 에러 상세: ", e);
                }
            } else {
                log.info("🏪 외관 사진 파일이 없음 - S3 업로드 생략");
                log.info("   - storeFrontPhoto null 여부: {}", storeFrontPhoto == null);
                if (storeFrontPhoto != null) {
                    log.info("   - 파일 isEmpty: {}", storeFrontPhoto.isEmpty());
                }
            }
            
            log.info("=== 📁 S3 파일 업로드 처리 완료 ===");
            
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
        
        // 1. 최상위 userIndex가 있는 경우 직접 조회
        if (dto.getUserIndex() != null) {
            log.info("🔍 최상위 userIndex 발견: {}", dto.getUserIndex());
            userTesseris = userTesserisRepository.findById(dto.getUserIndex())
                .orElseThrow(() -> new RuntimeException("UserTesseris를 찾을 수 없습니다: " + dto.getUserIndex()));
        }
        // 2. userInfo.user_index가 있는 경우 직접 조회 (✅ 추가된 로직)
        else if (dto.getUserInfo() != null && dto.getUserInfo().getUser_index() != null) {
            Integer userIndexFromUserInfo = dto.getUserInfo().getUser_index();
            log.info("🔍 userInfo.user_index 발견: {}", userIndexFromUserInfo);
            userTesseris = userTesserisRepository.findById(userIndexFromUserInfo)
                .orElseThrow(() -> new RuntimeException("UserTesseris를 찾을 수 없습니다: " + userIndexFromUserInfo));
        }
        // 3. userIndex가 없는 경우에만 userInfo.name으로 조회 (마지막 수단)
        else if (dto.getUserInfo() != null && dto.getUserInfo().getName() != null) {
            String userName = dto.getUserInfo().getName();
            
            log.info("🔍 사용자 이름으로 UserEntity 조회 시작: userName='{}'", userName);
            
            try {
                // userName으로 UserEntity 조회 - 안전한 메서드 사용 (중복 방지)
                log.info("📋 findFirstByName() 호출 전 - 중복 방지");
                UserEntity userEntity = userRepository.findFirstByName(userName)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userName));
                log.info("✅ findFirstByName() 성공: userId={}", userEntity.getId());
                
                // UserEntity로 UserTesseris 조회
                List<UserTesseris> tesserisList = userTesserisRepository.findByUsersId(userEntity);
                if (tesserisList.isEmpty()) {
                    throw new RuntimeException("UserTesseris를 찾을 수 없습니다: " + userName);
                }
                userTesseris = tesserisList.get(0);
                
            } catch (Exception e) {
                log.error("❌ 사용자 조회 중 오류 발생: userName='{}', 오류: {}", userName, e.getMessage());
                log.error("❌ 오류 상세: ", e);
                throw e;
            }
        } else {
            throw new RuntimeException("사용자 정보가 부족합니다. userIndex 또는 userInfo.name이 필요합니다.");
        }
        
        // === ✅ 최종 선택된 UserTesseris 정보 확인 ===
        log.info("✅ === 최종 선택된 사용자 정보 ===");
        log.info("   - UserTesseris.userIndex: {}", userTesseris.getUserIndex());
        log.info("   - UserTesseris.usersId: {}", userTesseris.getUsersId() != null ? userTesseris.getUsersId().getId() : "null");
        log.info("   - UserTesseris.userRoleIndex: {}", userTesseris.getUserRoleIndex());
        log.info("✅ === 최종 선택된 사용자 정보 완료 ===");
        
        Store store = new Store();
        
        // UserTesseris 설정
        store.setUserIndex(userTesseris);
        
        // === ✅ Store에 설정된 UserTesseris 확인 ===
        log.info("🏢 === Store 엔티티에 설정된 사용자 정보 ===");
        log.info("   - store.getUserIndex().getUserIndex(): {}", store.getUserIndex().getUserIndex());
        log.info("   - store.getUserIndex().getUsersId(): {}", store.getUserIndex().getUsersId().getId());
        log.info("🏢 === Store 엔티티 사용자 정보 설정 완료 ===");
        
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
        
        // === 📸 사업자등록증 파일명 저장 ===
        log.info("📸 사업자등록증 파일 처리 시작");
        log.info("   - MultipartFile 존재: {}", storeBusinessLicensePhoto != null);
        if (storeBusinessLicensePhoto != null) {
            log.info("   - MultipartFile 비어있는가: {}", storeBusinessLicensePhoto.isEmpty());
            log.info("   - MultipartFile 원본파일명: '{}'", storeBusinessLicensePhoto.getOriginalFilename());
            log.info("   - MultipartFile 크기: {} bytes", storeBusinessLicensePhoto.getSize());
        }
        
        if (storeBusinessLicensePhoto != null && !storeBusinessLicensePhoto.isEmpty()) {
            String originalFilename = storeBusinessLicensePhoto.getOriginalFilename();
            store.setStoreBusinessLicensePhoto(originalFilename);
            log.info("✅ 사업자등록증 파일명 설정 (MultipartFile): '{}'", originalFilename);
        } else {
            log.info("❌ MultipartFile이 null이거나 비어있음 - DTO에서 파일명 확인");
            // Object 타입 처리 (빈 객체 {} 처리 포함)
            Object businessLicensePhotoObj = dto.getBusinessInfo() != null ? dto.getBusinessInfo().getStoreBusinessLicensePhoto() : dto.getStoreBusinessLicensePhoto();
            log.info("   - DTO에서 가져온 값: {}", businessLicensePhotoObj);
            String businessLicensePhotoStr = "";
            if (businessLicensePhotoObj != null) {
                String objStr = businessLicensePhotoObj.toString();
                log.info("   - 문자열로 변환: '{}'", objStr);
                // 빈 객체 "{}" 나 "null"이 아닌 경우만 저장
                if (!objStr.equals("{}") && !objStr.equals("null") && !objStr.trim().isEmpty()) {
                    businessLicensePhotoStr = objStr;
                    log.info("   - 유효한 파일명으로 판단: '{}'", businessLicensePhotoStr);
                } else {
                    log.info("   - 무효한 값으로 판단 (빈 문자열 설정)");
                }
            }
            store.setStoreBusinessLicensePhoto(businessLicensePhotoStr);
            log.info("✅ 사업자등록증 파일명 설정 (DTO): '{}'", businessLicensePhotoStr);
        }
        
        // 가맹점 등록 정보
        store.setStoreName(storeName);
        store.setStorePhone(storePhone);
        store.setStoreZoneCode(storePostcode); // storePostcode -> storeZoneCode
        store.setStoreAddress(storeAddress);
        store.setStoreDetailAddress(storeDetailAddress);
        store.setStoreSite(storeSite);
        
        // === 📍 주소로 위도/경도 구하기 (지오코딩) ===
        log.info("=== 📍 지오코딩 처리 시작 ===");
        log.info("📍 현재 메소드: createStoreEntity()");
        log.info("📍 geocodingService 인스턴스: {}", geocodingService != null ? "존재함" : "NULL!");
        
        // 주소 정보 다시 추출
        log.info("📍 주소 정보 재추출:");
        log.info("   - 기본주소 (storeAddress): '{}'", storeAddress);
        log.info("   - 상세주소 (storeDetailAddress): '{}'", storeDetailAddress);
        
        if (storeAddress != null && !storeAddress.trim().isEmpty()) {
            String fullAddress = storeAddress;
            if (storeDetailAddress != null && !storeDetailAddress.trim().isEmpty()) {
                fullAddress = storeAddress + " " + storeDetailAddress;
            }
            
            log.info("🌐 카카오 지오코딩 API 요청 준비:");
            log.info("   - 전체주소: '{}'", fullAddress);
            log.info("   - 주소 길이: {} 글자", fullAddress.length());
            log.info("   - 주소가 비어있는가: {}", fullAddress.trim().isEmpty());
            
            try {
                log.info("🔄 geocodingService.getLatLngAsString() 호출 시작");
                String[] latLng = geocodingService.getLatLngAsString(fullAddress);
                log.info("🔄 geocodingService.getLatLngAsString() 호출 완료");
                log.info("🌐 카카오 지오코딩 API 응답: {}", latLng != null ? java.util.Arrays.toString(latLng) : "null");
                
                if (latLng != null && latLng.length >= 2 && !latLng[0].isEmpty() && !latLng[1].isEmpty()) {
                    store.setStorePos1(latLng[0]); // 위도
                    store.setStorePos2(latLng[1]); // 경도
                    log.info("✅ 지오코딩 성공 - 주소: '{}' → 위도: {}, 경도: {}", fullAddress, latLng[0], latLng[1]);
                    log.info("✅ Store 엔티티에 좌표 저장 완료: storePos1='{}', storePos2='{}'", store.getStorePos1(), store.getStorePos2());
                } else {
                    store.setStorePos1(""); // 빈 값 설정
                    store.setStorePos2(""); // 빈 값 설정
                    log.warn("⚠️ 지오코딩 실패 - 주소: '{}', 응답 데이터가 유효하지 않음", fullAddress);
                    log.warn("⚠️ 응답 배열 길이: {}", latLng != null ? latLng.length : "null");
                    if (latLng != null && latLng.length >= 2) {
                        log.warn("⚠️ 위도 값: '{}', 경도 값: '{}'", latLng[0], latLng[1]);
                    }
                    log.warn("⚠️ 좌표를 빈 값으로 설정: storePos1='', storePos2=''");
                }
            } catch (Exception e) {
                store.setStorePos1(""); // 빈 값 설정
                store.setStorePos2(""); // 빈 값 설정
                log.error("❌ 지오코딩 API 호출 중 오류 발생");
                log.error("❌ 요청 주소: '{}'", fullAddress);
                log.error("❌ 오류 타입: {}", e.getClass().getSimpleName());
                log.error("❌ 오류 메시지: {}", e.getMessage());
                log.error("❌ 오류 상세: ", e);
                log.error("❌ 좌표를 빈 값으로 설정: storePos1='', storePos2=''");
            }
        } else {
            store.setStorePos1(""); // 주소가 없으면 빈 값
            store.setStorePos2(""); // 주소가 없으면 빈 값
            log.warn("⚠️ 주소 정보가 없거나 비어있음:");
            log.warn("   - storeAddress: '{}'", storeAddress);
            log.warn("   - storeAddress가 null인가: {}", storeAddress == null);
            log.warn("   - storeAddress가 비어있는가: {}", storeAddress != null && storeAddress.trim().isEmpty());
            log.warn("⚠️ 좌표를 빈 값으로 설정");
        }
        
        log.info("=== 📍 지오코딩 처리 완료 ===");
        log.info("📍 최종 설정된 좌표: storePos1='{}', storePos2='{}'", store.getStorePos1(), store.getStorePos2());
        
        // === 🪧 간판 사진 파일명 저장 ===
        log.info("🪧 간판 사진 파일 처리 시작");
        if (storeSignPhoto != null && !storeSignPhoto.isEmpty()) {
            String originalFilename = storeSignPhoto.getOriginalFilename();
            store.setStoreSignPhoto(originalFilename);
            log.info("✅ 간판 사진 파일명 설정 (MultipartFile): '{}'", originalFilename);
        } else {
            log.info("❌ 간판 사진 MultipartFile이 null이거나 비어있음 - DTO에서 파일명 확인");
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
            log.info("✅ 간판 사진 파일명 설정 (DTO): '{}'", signPhotoStr);
        }
        
        // === 🏪 외관 사진 파일명 저장 ===
        log.info("🏪 외관 사진 파일 처리 시작");
        if (storeFrontPhoto != null && !storeFrontPhoto.isEmpty()) {
            String originalFilename = storeFrontPhoto.getOriginalFilename();
            store.setStoreProntPhoto(originalFilename); // storeFrontPhoto -> storeProntPhoto
            log.info("✅ 외관 사진 파일명 설정 (MultipartFile): '{}'", originalFilename);
        } else {
            log.info("❌ 외관 사진 MultipartFile이 null이거나 비어있음 - DTO에서 파일명 확인");
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
            log.info("✅ 외관 사진 파일명 설정 (DTO): '{}'", frontPhotoStr);
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
        
        // ✅ Store의 business_man_user_index 확인 (이제 business_man_index 값임)
        Integer businessManIndex = store.getBusinessManUserIndex();
        log.info("🔍 Store에서 가져온 business_man_user_index (실제로는 business_man_index): {}", businessManIndex);
        
        if (businessManIndex == null) {
            log.warn("⚠️ Store의 business_man_user_index가 null입니다. StoreSubscriptionFee 저장을 건너뜁니다.");
            return;
        }
        
        try {
            // ✅ business_man_index로 BusinessMan 직접 조회
            log.info("🔍 BusinessMan 조회 시작: business_man_index={}", businessManIndex);
            BusinessMan businessMan = businessManRepository.findById(businessManIndex)
                .orElseThrow(() -> new RuntimeException("BusinessMan을 찾을 수 없습니다. business_man_index: " + businessManIndex));
            log.info("✅ BusinessMan 조회 성공: business_man_index={}", businessMan.getBusinessManIndex());
            
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
     * 3. temporary_store_detail에 insert하기. (business_man_index 하나당 1개 칼럼 insert)
     */
    private void createTemporaryStoreDetailEntities(Store store, TemporaryStoreMaster temporaryStoreMaster) {
        Integer businessManIndex = store.getBusinessManUserIndex(); // 실제로는 business_man_index 값
        
        if (businessManIndex != null) {
            processBusinessManHierarchy(businessManIndex, temporaryStoreMaster);
        } else {
            // business_man_index가 없는 경우 기본 레코드 생성
            createDefaultTemporaryStoreDetail(store, temporaryStoreMaster);
        }
    }
    
    /**
     * business_man 계층 구조를 처리하여 temporary_store_detail 레코드들 생성
     */
    private void processBusinessManHierarchy(Integer businessManIndex, TemporaryStoreMaster temporaryStoreMaster) {
        Integer currentBusinessManIndex = businessManIndex;
        
        while (currentBusinessManIndex != null) {
            // ✅ business_man_index로 BusinessMan 직접 찾기
            BusinessMan businessMan = businessManRepository.findById(currentBusinessManIndex).orElse(null);
            
            if (businessMan == null) {
                log.warn("BusinessMan을 찾을 수 없습니다: business_man_index={}", currentBusinessManIndex);
                break;
            }
            
            // ✅ BusinessMan에서 UserTesseris 가져오기
            UserTesseris currentUserTesseris = businessMan.getUserIndex();
            if (currentUserTesseris == null) {
                log.warn("BusinessMan의 UserTesseris가 null입니다: business_man_index={}", currentBusinessManIndex);
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
            log.info("TemporaryStoreDetail 저장: business_man_index={}, user_index={}, business_grade={}, temporary_store_value={}, detail_index={}", 
                    currentBusinessManIndex, currentUserTesseris.getUserIndex(), businessGrade.getBusinessGradeName(), temporaryStoreValue, saved.getTemporaryStoreDetailIndex());
            
            // user_cm_log 테이블에 INSERT (1:1 대응)
            createUserCmLogEntity(temporaryStoreMaster, saved, temporaryStoreValue.intValue());
            
            // user_cm 테이블 UPDATE (user_cm_deposit += user_cm_log_value)
            updateUserCmDeposit(currentUserTesseris.getUserIndex(), temporaryStoreValue.intValue());
            
            // ✅ 다음 상위 business_man 찾기 (boss_user_index → business_man_index 변환)
            Integer bossUserIndex = businessMan.getBossUserIndex();
            if (bossUserIndex != null) {
                log.info("🔍 상위 BusinessMan 조회: boss_user_index={}", bossUserIndex);
                // boss_user_index(user_index)로 UserTesseris 찾기
                UserTesseris bossUserTesseris = userTesserisRepository.findById(bossUserIndex).orElse(null);
                if (bossUserTesseris != null) {
                    // UserTesseris로 BusinessMan 찾기
                    BusinessMan bossBusinessMan = businessManRepository.findByUserIndex(bossUserTesseris).orElse(null);
                    if (bossBusinessMan != null) {
                        currentBusinessManIndex = bossBusinessMan.getBusinessManIndex();
                        log.info("✅ 상위 BusinessMan 발견: boss_user_index={} → business_man_index={}", bossUserIndex, currentBusinessManIndex);
                    } else {
                        log.warn("⚠️ boss_user_index={}에 해당하는 BusinessMan이 없습니다", bossUserIndex);
                        currentBusinessManIndex = null;
                    }
                } else {
                    log.warn("⚠️ boss_user_index={}에 해당하는 UserTesseris가 없습니다", bossUserIndex);
                    currentBusinessManIndex = null;
                }
            } else {
                log.info("ℹ️ boss_user_index가 null - 최상위 BusinessMan입니다");
                currentBusinessManIndex = null;
            }
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
            .userCmLogReason("가맹신청비 : 중개수수료 지급") 
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
     * StoreImage 테이블에 사진 정보 저장
     * @param store 저장된 Store 엔티티
     * @param imageUrl S3 업로드된 이미지 URL
     * @param imageType 이미지 타입 ("business_license", "sign_photo", "front_photo")
     */
    private void saveStoreImage(Store store, String imageUrl, String imageType) {
        try {
            log.info("🖼️ StoreImage 테이블 저장 시작: type={}, url={}", imageType, imageUrl);
            
            StoreImage storeImage = new StoreImage();
            storeImage.setStoreUserIndex(store);
            storeImage.setStoreImage(imageUrl);
            storeImage.setStoreMainImageStatus(imageType);
            
            StoreImage savedStoreImage = storeImageRepository.save(storeImage);
            log.info("✅ StoreImage 테이블 저장 완료: imageIndex={}, type={}", 
                    savedStoreImage.getStoreImageIndex(), imageType);
            
        } catch (Exception e) {
            log.error("❌ StoreImage 테이블 저장 실패: type={}, url={}", imageType, imageUrl);
            log.error("❌ 오류 상세: ", e);
            // StoreImage 저장 실패는 전체 프로세스를 중단시키지 않음 (로그만 남김)
        }
    }

    /**
     * managerEmail로 user_tesseris 테이블에서 user_index 찾기 (business_man 테이블에 존재하는지 검증)
     */
    private Integer findManagerUserIndex(String managerEmail) {
        try {
            log.info("=== 🔍 매니저 조회 프로세스 시작 ===");
            log.info("🔍 1단계: 매니저 이메일로 UserEntity 조회 - email: {}", managerEmail);
            
            try {
                // 1. managerEmail로 UserEntity 조회 - email은 unique하므로 안전
                log.info("📋 findByEmail() 호출 전: email='{}' - email은 unique", managerEmail);
                UserEntity managerUserByEmail = userRepository.findByEmail(managerEmail)
                    .orElseThrow(() -> new RuntimeException("매니저 UserEntity를 이메일로 찾을 수 없습니다: " + managerEmail));
                log.info("✅ 1단계 성공: 이메일로 UserEntity 조회 완료: userId={}", managerUserByEmail.getId());
                
                // 2. UserEntity의 ID 얻기
                String managerId = managerUserByEmail.getId();
                log.info("🔍 2단계: UserEntity ID 추출 - userId: {}", managerId);
                
                // 3. 기존 로직대로 ID로 UserEntity 조회 (기존 방식 유지)
                log.info("🔍 3단계: ID로 UserEntity 재조회");
                UserEntity managerUser = userRepository.findById(managerId)
                    .orElseThrow(() -> new RuntimeException("매니저 UserEntity를 ID로 찾을 수 없습니다: " + managerId));
                log.info("✅ 3단계 성공: ID로 UserEntity 재조회 완료");
                
                // Userntity로 UserTesseris 조회
                log.info("🔍 4단계: UserEntity로 UserTesseris 조회");
                List<UserTesseris> managerTesserisList = userTesserisRepository.findByUsersId(managerUser);
                if (managerTesserisList.isEmpty()) {
                    throw new RuntimeException("매니저 UserTesseris를 찾을 수 없습니다: " + managerEmail);
                }
                log.info("✅ 4단계 성공: UserTesseris 조회 완료 - 목록 크기: {}", managerTesserisList.size());
                
                UserTesseris managerUserTesseris = managerTesserisList.get(0);
                Integer userIndex = managerUserTesseris.getUserIndex();
                log.info("🔍 5단계: UserTesseris에서 user_index 추출 - user_index: {}", userIndex);
                
                // ✅ user_index로 BusinessMan 테이블에서 business_man_index 찾기
                log.info("🔍 6단계: BusinessMan 테이블에서 business_man_index 조회");
                BusinessMan businessMan = businessManRepository.findByUserIndex(managerUserTesseris).orElse(null);
                
                if (businessMan == null) {
                    log.warn("⚠️ 6단계 실패: 매니저 {}(user_index={})는 business_man 테이블에 존재하지 않습니다.", managerEmail, userIndex);
                    log.warn("⚠️ business_man_user_index를 null로 설정합니다.");
                    return null; // business_man 테이블에 없으면 null 반환
                }
                
                // ✅ business_man_index를 반환 (Store.business_man_user_index에 저장될 값)
                Integer businessManIndex = businessMan.getBusinessManIndex();
                log.info("✅ 6단계 성공: BusinessMan 조회 완료");
                log.info("   - user_index: {}", userIndex);
                log.info("   - business_man_index: {}", businessManIndex);
                log.info("🎯 최종 결과: business_man_index={} 반환 (Store.business_man_user_index에 저장)", businessManIndex);
                log.info("=== 🔍 매니저 조회 프로세스 완료 ===");
                
                return businessManIndex; // ✅ BusinessMan.business_man_index 반환
                
            } catch (Exception innerE) {
                log.error("❌ 매니저 조회 내부 오류: email='{}', 오류: {}", managerEmail, innerE.getMessage());
                log.error("❌ 내부 오류 상세: ", innerE);
                throw innerE;
            }
            
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