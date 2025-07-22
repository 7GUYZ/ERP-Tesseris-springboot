package com.jakdang.labs.api.jihun.memberaccount.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 회원 자산 내역 검색 요청 DTO
 * 
 * 동적 LIKE 검색을 지원하며, 프론트엔드에서 전달받는 검색 조건들을 매핑
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCmLogSearchRequestDto {
    
    /**
     * 트리거 사용자 ID (FROM_ID) - 기존 인덱스 방식
     */
    private String userIndexEventTrigger;
    
    /**
     * 상대방 사용자 ID (TO_ID) - 기존 인덱스 방식
     */
    private String userIndexEventParty;
    
    /**
     * 🆕 이벤트 트리거 사용자 이메일 (LIKE 검색 지원)
     * 프론트엔드에서 eventTriggerUserEmail로 전달
     */
    @JsonProperty("eventTriggerUserEmail")
    private String eventTriggerUserEmail;
    
    /**
     * 🆕 이벤트 상대방 사용자 이메일 (LIKE 검색 지원)
     * 프론트엔드에서 eventPartyUserEmail로 전달
     */
    @JsonProperty("eventPartyUserEmail")
    private String eventPartyUserEmail;
    
    /**
     * 🆕 이벤트 상대방 사용자 이름 (LIKE 검색 지원)
     * 프론트엔드에서 eventPartyUserName로 전달
     */
    @JsonProperty("eventPartyUserName")
    private String eventPartyUserName;
    
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
     * 페이지 크기 (기본값: 25)
     */
    private int size = 25;
    
    /**
     * 🆕 검색 타입 (LIKE, EXACT 등)
     */
    private String searchType = "LIKE";
} 