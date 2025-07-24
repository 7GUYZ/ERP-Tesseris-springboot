package com.jakdang.labs.api.deokkyu.storeRegister.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreRegisterRequestDto {
    
    // 기본 가맹점 정보
    private String storeName;           // 가맹점명
    private String businessNumber;      // 사업자등록번호
    private String ownerName;           // 대표자명
    private String ownerPhone;          // 대표자 전화번호
    private String ownerEmail;          // 대표자 이메일
    
    // 가맹점 주소 정보
    private String storeAddress;        // 가맹점 주소
    private String storeDetailAddress;  // 가맹점 상세주소
    private String zipCode;             // 우편번호
    
    // 가맹점 운영 정보
    private String businessType;        // 업종
    private String operatingHours;      // 운영시간
    private String description;         // 가맹점 설명
    
    // 계약 정보
    private String contractType;        // 계약 유형
    private Double commissionRate;      // 수수료율
    
    // 신청 상태
    private String status;              // 신청 상태 (PENDING, APPROVED, REJECTED)
    private LocalDateTime createdAt;    // 신청일시
    private String memo;                // 메모
} 