package com.jakdang.labs.api.taekjun.businessmanlist.dto;

import lombok.Data;

@Data
public class BusinessmanUpdateRequestDTO {
    private Integer userIndex;  // 수정할 사용자 인덱스
    private String email;
    private String userName;
    private String userPw;      // 비밀번호 (선택사항)
    private String userPhone;
    private String userBirthday;
    private Integer userGenderIndex;
    private String userZoneCode;
    private String userAddress;         // 전체 주소 (address_name)
    private String userDetailAddress;   // 상세주소 (직접입력)
    private String region1DepthName;    // 시/도
    private String region2DepthName;    // 구/군
    private String region3DepthName;    // 동/읍/면
    private Integer userBankIndex;
    private String userBankHolder;
    private String userBankNumber;
    private String bossEmail;
    private Integer businessGradeIndex;
    private Integer businessAreaIndex;
    private String businessManDistributionFlag;
    
    // 조직도 수정 관련 필드
    private String newBossEmail;        // 새로운 상사 이메일
    private Boolean changeOrganization; // 조직도 변경 여부
} 