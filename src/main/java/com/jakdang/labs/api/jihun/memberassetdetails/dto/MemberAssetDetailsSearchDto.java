package com.jakdang.labs.api.jihun.memberassetdetails.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 회원 자산 현황 검색 DTO
 * 검색 조건과 페이징 정보를 분리하여 관리
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberAssetDetailsSearchDto {
    
    /**
     * 검색 조건 객체
     * 동적 검색을 위한 조건들을 묶어서 관리
     */
    private SearchCriteria searchCriteria;
    
    /**
     * 페이징 정보 객체
     * 페이지 번호와 페이지 크기를 관리
     */
    private PaginationInfo paginationInfo;
    
    /**
     * 검색 조건 내부 클래스
     * 회원 검색에 필요한 모든 조건을 포함
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchCriteria {
        /** 사용자 이메일 (아이디 검색용) */
        private String userEmail;
        
        /** 사용자 이름 */
        private String userName;
        
        /** 사용자 전화번호 */
        private String userPhone;
        
        /** 사용자 역할 인덱스 (등급) */
        private Integer userRoleIndex;
    }
    
    /**
     * 페이징 정보 내부 클래스
     * 페이지네이션 관련 정보를 관리
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationInfo {
        /** 페이지 번호 (0부터 시작) */
        private Integer page;
        
        /** 페이지 크기 */
        private Integer size;
    }
    
    /**
     * 기존 방식과의 호환성을 위한 getter 메서드들
     * 레거시 코드에서 사용할 수 있도록 유지
     */
    public String getUserEmail() {
        return searchCriteria != null ? searchCriteria.getUserEmail() : null;
    }
    
    public String getUserName() {
        return searchCriteria != null ? searchCriteria.getUserName() : null;
    }
    
    public String getUserPhone() {
        return searchCriteria != null ? searchCriteria.getUserPhone() : null;
    }
    
    public Integer getUserRoleIndex() {
        return searchCriteria != null ? searchCriteria.getUserRoleIndex() : null;
    }
    
    public Integer getPage() {
        return paginationInfo != null ? paginationInfo.getPage() : null;
    }
    
    public Integer getSize() {
        return paginationInfo != null ? paginationInfo.getSize() : null;
    }
} 