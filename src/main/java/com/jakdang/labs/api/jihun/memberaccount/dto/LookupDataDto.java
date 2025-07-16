package com.jakdang.labs.api.jihun.memberaccount.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 룩업 데이터용 공통 DTO
 * 
 * 드롭다운, 선택 목록 등에 사용되는 키-값 쌍 데이터
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LookupDataDto {
    
    /**
     * 인덱스/ID
     */
    private Long index;
    
    /**
     * 표시명
     */
    private String name;
    
    /**
     * 추가 코드 (선택사항)
     */
    private String code;
} 