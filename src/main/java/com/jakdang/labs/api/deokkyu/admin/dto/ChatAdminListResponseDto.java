package com.jakdang.labs.api.deokkyu.admin.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class ChatAdminListResponseDto {
    
    // adminName: user_index로 user_tesseris의 user_index와 같은 칼럼의 users_id를 가지고 
    // users 테이블로 가서 id와 같은 칼럼의 name 얻기
    private String adminName;
    
    // adminUserIndex: admin 테이블의 user_index
    private Integer adminUserIndex;
    
    // adminTypeName: admin_type_index로 admin_type 테이블의 admin_type_name
    private String adminTypeName;
    
    // adminRankName: admin 테이블의 admin_rank_name
    private String adminRankName;
    
    /**
     * JPQL Constructor Expression용 생성자
     */
    public ChatAdminListResponseDto(String adminName, Integer adminUserIndex, 
                                   String adminTypeName, String adminRankName) {
        this.adminName = adminName;
        this.adminUserIndex = adminUserIndex;
        this.adminTypeName = adminTypeName;
        this.adminRankName = adminRankName;
    }
} 