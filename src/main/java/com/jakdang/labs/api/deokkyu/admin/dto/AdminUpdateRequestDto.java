package com.jakdang.labs.api.deokkyu.admin.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUpdateRequestDto {
    
    // 수정 가능한 기본 정보 (UserEntity)
    private String adminUserName;       // 이름
    private String adminUserPhone;      // 핸드폰 번호
    
    // 수정 가능한 상세 정보 (UserTesseris)
    private String adminUserBirthday;   // 생년월일 (String으로 받아서 파싱)
    private String adminUserGender;     // 성별 (성별명으로 받아서 인덱스로 변환)
    private String adminAddress;        // 주소
    private String adminDetailAddress;  // 상세주소
}