package com.jakdang.labs.api.deokkyu.store.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreRegisterdListDto {
    private String businessUserId; // 사업자 ID
    private String businessUserName; // 사업자 이름
    private String businessGradeName; // 사업자 등급
    private String userId; // 가맹점 ID
    private String userName; // 이름
    private String userPhone; // 핸드폰 번호
    private String storeBossName; // 대표자 이름
    private String storeCorporateName; // 상호명
    private String storeName; // 가맹점 명
    private String storeRequestStatusName; // 승인 여부
    private String storeTransactionStatus; // 거래 상태
    private Integer userCmpInit; // 초기지급 CMP
    private Integer totalCM; // 보유 CM
    private String storeCreateDate; // 신청일
    private Integer storeSubscriptionFeeValue; // 충전금액 (store_subscription_fee 테이블)
    private Integer franchiseFee; // 가맹비 (store 테이블)
    private String storeSubscriptionFeeCommissionCheck; // 분배여부 (store_subscription_fee 테이블)
}
