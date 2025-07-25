package com.jakdang.labs.api.taekjun.businessmanlist.dto;

import lombok.Data;

@Data
public class BusinessmanDeleteRequestDTO {
    private Integer userIndex;  // 삭제할 사용자 인덱스
    private String reason;      // 삭제 사유 (선택사항)
} 