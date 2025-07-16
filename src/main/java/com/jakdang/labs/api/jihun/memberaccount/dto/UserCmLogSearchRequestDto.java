package com.jakdang.labs.api.jihun.memberaccount.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 회원 자산 내역 검색 요청 DTO
 * 
 * PHP에서 전달받는 검색 조건들을 매핑
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCmLogSearchRequestDto {
    
    /**
     * 트리거 사용자 ID (FROM_ID)
     */
    private String userIndexEventTrigger;
    
    /**
     * 상대방 사용자 ID (TO_ID)
     */
    private String userIndexEventParty;
    
    /**
     * FROM 사용자 역할 인덱스
     */
    private Long userRoleIndex;
    
    /**
     * TO 사용자 역할 인덱스
     */
    private Long userRoleIndex2;
    
    /**
     * 거래 가치 유형 인덱스
     */
    private Long userCmLogValueTypeIndex;
    
    /**
     * 검색 시작 일자
     */
    private String userCmLogCreateTimeStart;
    
    /**
     * 검색 종료 일자
     */
    private String userCmLogCreateTimeEnd;
    
    /**
     * 결제 방법 인덱스
     */
    private Long userCmLogPaymentIndex;
    
    /**
     * 거래 유형 인덱스
     */
    private Long userCmLogTransactionTypeIndex;
    
    /**
     * 페이지 번호 (기본값: 0)
     */
    private int page = 0;
    
    /**
     * 페이지 크기 (기본값: 100)
     */
    private int size = 100;
} 