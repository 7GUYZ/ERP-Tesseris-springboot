package com.jakdang.labs.api.taekjun.storelist.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreListDTO {
    private Integer storeIndex;
    private String storeName;
    private String storePhone;
    private String storeAddress;
    private String storeCategoryName;
    private Integer userCmUse; // CM 금액 (잔액 표시)
    private String storeImage; // 이미지 url
    private Integer storeBusinessState; 
    // storeBusinessState
    // -> 0이면 영업종료, 1이면 영업중, 2면 영업요일 아님, 3면 브레이크타임, 4면 영업일 미지정
    private String storeBusinessDate; // 영업일
    private String storeBusinessHour; // 영업시간
    private String storeRestHour; // 휴게시간
} 