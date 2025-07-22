package com.jakdang.labs.api.deokkyu.businessman.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessmanSearchDto {
    private String businessUserId; // 사업자 ID
    private String businessUserName; // 사업자 이름
    private String businessGradeName; // 사업자 등급
    private String businessUserPhone; // 사업자 핸드폰 번호
    private String businessAreaIndex; // 담당 구역
    private String storeName; // 가맹점 명
    private String userId; // 가맹점 ID
    private String userName; // 회원 이름
    private String temporaryStoreMasterChargeTimeStart; // 분배시간 시작
    private String temporaryStoreMasterChargeTimeEnd; // 분배시간 종료
} 