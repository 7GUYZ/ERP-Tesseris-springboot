package com.jakdang.labs.api.deokkyu.admin.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminCreateRequestDto {
    
           // 관리자 기본 정보
       private String adminUserEmail;      // 이메일
       private String adminUserName;       // 이름
       private String adminUserBirthday;   // 생년월일
       private String adminUserGender;     // 성별
       private String adminUserPhone;      // 핸드폰 번호
       private String adminPassword;       // 비밀번호
       private String adminPasswordConfirm; // 비밀번호 확인
       private String adminRegistrationDate; // 등록일
       
       // 관리자 권한 정보
       private Integer adminTypeIndex;     // 관리자 타입 인덱스
       
       // 관리자 주소 정보
       private String adminAddress;        // 주소
       private String adminDetailAddress;  // 상세주소
} 