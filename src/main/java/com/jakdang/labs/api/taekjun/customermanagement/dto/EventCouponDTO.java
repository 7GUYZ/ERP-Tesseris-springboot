package com.jakdang.labs.api.taekjun.customermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCouponDTO {
    private String eventName;           // 이벤트명
    private String couponName;          // 쿠폰명
    private Integer couponPrice;        // 쿠폰 금액
    private Integer couponLimit;        // 쿠폰 사용 기한 (일)
    private Integer couponCount;        // 쿠폰 발행 개수
    private String pinCode;             // 핀번호
    private String storeUserIndex;      // 가맹점 사용자 인덱스
} 