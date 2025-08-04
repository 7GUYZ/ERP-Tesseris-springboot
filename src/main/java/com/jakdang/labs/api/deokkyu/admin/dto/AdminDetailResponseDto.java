package com.jakdang.labs.api.deokkyu.admin.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDetailResponseDto {
    
    // 기본 정보 (UserEntity에서)
    private String adminUserEmail;      // 이메일
    private String adminUserName;       // 이름
    private String adminUserPhone;      // 핸드폰 번호
    
    // 상세 정보 (UserTesseris에서)
    private LocalDate adminUserBirthday;   // 생년월일
    private String adminUserGender;     // 성별 (UserGender 테이블에서 조회)
    private String adminAddress;        // 주소
    private String adminDetailAddress;  // 상세주소
    
    // 관리자 정보 (Admin에서)
    private String adminTypeName;       // 관리자 타입명
    private LocalDateTime adminRegistrationDate; // 등록일
    private Integer adminUserIndex;     // user_index (식별용)
}