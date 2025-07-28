package com.jakdang.labs.api.deokkyu.modal_admin.dto;

import lombok.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class StoreDetailDto {
    
    // 프론트엔드에서 보내는 추가 필드들
    private Integer id;                                 // 프론트엔드 ID
    private String userId;                              // 사용자 ID
    private String userName;                            // 사용자 이름
    private String userPhone;                           // 사용자 전화번호
    private String storeRegistrationNum;                // 사업자등록번호
    private String storeTypeTaxation;                   // 과세 유형
    private String storeCreateDate;                     // 가맹점 생성일
    private String storeCategoryName;                   // 가맹점 카테고리명
    private String storeRequestStatusName;              // 가맹점 신청 상태명
    private String businessGradeName;                   // 사업자 등급명
    private Integer totalCM;                            // 총 CM
    private Integer userCmpInit;                        // 초기 CMP
    private String businessUserId;                      // 사업자 사용자 ID
    private String businessUserName;                    // 사업자 사용자명
    
    // 1. 사용자 기본 정보 (users 테이블)
    private String userPassword;                        // 사용자 비밀번호
    
    // 2, 3. 사용자 상세 정보 (user_tesseris 테이블)
    private String userBirthday;                        // 사용자 생년월일
    private Integer userGenderIndex;                    // 사용자 성별 인덱스
    
    // 4, 5, 6. 가맹점 기본 정보 (store 테이블)
    private String storeName;                           // 가맹점 이름
    private String storePhone;                          // 가맹점 전화번호
    private String storeBossName;                       // 가맹점 대표자명
    private String storeCorporateName;                  // 가맹점 법인명
    private String storeAddress;                        // 가맹점 주소
    private String storeDetailAddress;                  // 가맹점 상세주소
    
    // 7. 가맹점 신청 상태
    private Integer storeRequestStatusIndex;            // 가맹점 신청 상태 인덱스
    
    // 8. 가맹점 거래 상태
    private Object storeTransactionStatus;              // 가맹점 거래 상태 (Boolean 또는 String)
    private String storeTransactionStatusString;        // 가맹점 거래 상태 (문자열 - "정상", "정지")
    
    // 10, 11, 12. 가맹점 사진 정보 (store 테이블)
    private String storeProntPhoto;                     // 가맹점 정면 사진
    private String storeBusinessLicensePhoto;           // 사업자등록증 사진
    private String storeSignPhoto;                      // 간판 사진
} 