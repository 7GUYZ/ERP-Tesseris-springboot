package com.jakdang.labs.api.jungeun.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStoreDetailDTO {
    private Integer storeIndex;
    private String storeName;
    private String storePhone;
    private String storeAddress;
    private String storeDetailAddress;
    private String storeSite; // 홈페이지가 있다면, url
    private String storeMemo; // 가맹점 소개글 또는 공지글
    private String storeCategoryName;
    private Integer userCmUse; // CM 금액 (잔액 표시)
    private List<String> storeImages; // 이미지 url 배열
    private Integer storeBusinessState; // 영업 상태 (-> 0이면 영업종료, 1이면 영업중, 2면 영업요일 아님, 3면 브레이크타임, 4면 영업일 미지정)
    private String storeBusinessDate; // 장사요일들
    private String storeBusinessHour; // 장사시간 (store_start_business_hour ~ store_end_business_hour)
    private String storeRestHour; // 휴게시간 (store_rest_start_hour ~ store_rest_end_hour)
}
