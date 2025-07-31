package com.jakdang.labs.api.deokkyu.storeRegister.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StoreRegisterRequestDto {
    
    // 사용자 정보
    private String userId;              // 사용자 ID
    private Integer userIndex;          // 로컬스토리지의 user_index
    private String userName;            // 신청자 이름
    private String userPhone;           // 신청자 전화번호
    
    // 사업자 등록 정보
    private String storeRegistrationNum;    // 사업자등록번호
    private String storeCorporateName;      // 법인명
    private String storeBossName;           // 대표자명
    private String storeTypeTaxation;       // 과세유형
    private String storeBusinessLicensePhoto; // 사업자등록증 사진 (파일명)
    
    // 가맹점 등록 정보
    private String storeName;               // 가게명
    private String storePhone;              // 가게 전화번호
    private String storePostcode;           // 우편번호
    private String storeAddress;            // 주소
    private String storeDetailAddress;      // 상세주소
    private String storeSite;               // 가게 사이트
    private String storeSignPhoto;          // 간판 사진 (파일명)
    private String storeFrontPhoto;         // 외관 사진 (파일명)
    private Boolean hasManager;             // 매니저 유무
    private String managerId;               // 매니저 ID
    
    // 약관 동의 정보
    private Boolean agreementRequired1;     // 필수약관1 동의
    private Boolean agreementRequired2;     // 필수약관2 동의
    private Boolean agreementOptional1;     // 선택약관1 동의
    private Boolean agreementOptional2;     // 선택약관2 동의
    private Boolean agreementOptional3;     // 선택약관3 동의
    
    // 기존 필드들 (하위 호환성 유지)
    private String businessNumber;      // 사업자등록번호 (기존)
    private String ownerName;           // 대표자명 (기존)
    private String ownerPhone;          // 대표자 전화번호 (기존)
    private String ownerEmail;          // 대표자 이메일 (기존)
    private String zipCode;             // 우편번호 (기존)
    private String businessType;        // 업종 (기존)
    private String operatingHours;      // 운영시간 (기존)
    private String description;         // 가맹점 설명 (기존)
    private String contractType;        // 계약 유형 (기존)
    private Double commissionRate;      // 수수료율 (기존)
    private String status;              // 신청 상태 (PENDING, APPROVED, REJECTED)
    private LocalDateTime createdAt;    // 신청일시
    private String memo;                // 메모
    
    // 새로운 데이터 구조를 위한 중첩 객체들
    private UserInfo userInfo;
    private BusinessInfo businessInfo;
    private StoreInfo storeInfo;
    private Agreements agreements;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserInfo {
        private Integer user_index;  // ✅ user_index 필드 추가
        private String name;
        private String phone;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BusinessInfo {
        private String storeRegistrationNum;
        private String storeCorporateName;
        private String storeBossName;
        private String storeTypeTaxation;
        private Object storeBusinessLicensePhoto; // Object로 변경하여 빈 객체도 받을 수 있도록
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StoreInfo {
        private String store_name;  // 프론트엔드와 일치
        private String store_phone; // 프론트엔드와 일치
        private String store_postcode; // 프론트엔드와 일치
        private String store_address; // 프론트엔드와 일치
        private String store_detail_address; // 프론트엔드와 일치
        private String storeSite;
        private Object storeSignPhoto; // Object로 변경하여 빈 객체도 받을 수 있도록
        private Object storeFrontPhoto; // Object로 변경하여 빈 객체도 받을 수 있도록
        private String hasManager; // 프론트엔드에서는 "YES"/"NO" 문자열
        private String managerId;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Agreements {
        private Boolean all; // 프론트엔드에서 추가로 보내는 필드
        private Boolean required1;
        private Boolean required2;
        private Boolean optional1;
        private Boolean optional2;
        private Boolean optional3;
    }
} 