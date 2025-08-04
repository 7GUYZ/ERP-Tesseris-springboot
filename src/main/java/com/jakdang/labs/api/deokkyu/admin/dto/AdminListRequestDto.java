package com.jakdang.labs.api.deokkyu.admin.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminListRequestDto {
    

    // 1. 사업자 ID 필터 (users.email LIKE 검색)
    private String adminUserEmail;
    
    // 2. 사업자 이름 필터 (users.name LIKE 검색)
    private String adminUserName;
    
    // 3. 핸드폰 번호 필터 (users.phone LIKE 검색)
    private String adminUserPhone;
    
    // 4. 관리자 타입 이름 필터 (admin_type.admin_type_name LIKE 검색)
    private String adminTypeName;
    
    // 5. 관리자 등급 필터 (admin.admin_rank_name LIKE 검색)
    private String adminRankName;
    
    // 6. 등록시간 시작일 필터 (admin.admin_registration_date >= 조건)
    private LocalDate adminRegistrationDateStart;
    
    // 7. 등록시간 종료일 필터 (admin.admin_registration_date <= 조건)
    private LocalDate adminRegistrationDateEnd;
} 