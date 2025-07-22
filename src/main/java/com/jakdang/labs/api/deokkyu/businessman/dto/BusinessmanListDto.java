package com.jakdang.labs.api.deokkyu.businessman.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessmanListDto {
    private String businessUserId; // 사업자 ID
    private String businessUserName; // 사업자 이름
    private String businessGradeName; // 사업자 등급
    private String businessUserPhone; // 핸드폰 번호 (businessman 테이블)
    private String businessAreaName; // 담당 구역 (businessman 테이블)
    
    private String storeName; // 가맹점 명
    private String storeUserId; // 가맹점 ID
    private String storeUserName; // 회원 이름
    
    private String temporaryStoreMasterDistributionTime; // 분배시간 (temporary_store_master 테이블)
    private Integer temporaryStoreCmValue; // 중개수수료 CM (temporary_store_master 테이블)
    private Integer temporaryStoreCashValue; // 중개수수료 Cash (temporary_store_master 테이블)
    private Integer temporaryStoreTotalValue; // 중개수수료 합계 (temporary_store_master 테이블)
} 