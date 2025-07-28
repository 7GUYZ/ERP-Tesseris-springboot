package com.jakdang.labs.api.deokkyu.admin.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class AdminListResponseDto {
    
    // 1. 사업자 ID (users.email)
    private String adminUserEmail;
    
    // 2. 사업자 이름 (users.name)
    private String adminUserName;
    
    // 3. 핸드폰 번호 (users.phone)
    private String adminUserPhone;
    
    // 4. 관리자 타입 이름 (admin_type.admin_type_name)
    private String adminTypeName;
    
    // 5. 관리자 등급 (admin.admin_rank_name)
    private String adminRankName;
    
    // 6. 등록시간 (admin.admin_registration_date)
    private LocalDateTime adminRegistrationDate;
    
    /**
     * JPQL Constructor Expression용 생성자
     */
    public AdminListResponseDto(String adminUserEmail, String adminUserName, String adminUserPhone, 
                               String adminTypeName, String adminRankName, LocalDateTime adminRegistrationDate) {
        this.adminUserEmail = adminUserEmail;
        this.adminUserName = adminUserName;
        this.adminUserPhone = adminUserPhone;
        this.adminTypeName = adminTypeName;
        this.adminRankName = adminRankName;
        this.adminRegistrationDate = adminRegistrationDate;
    }
} 