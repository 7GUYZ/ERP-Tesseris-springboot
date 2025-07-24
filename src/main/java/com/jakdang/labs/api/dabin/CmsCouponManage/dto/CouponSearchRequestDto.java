package com.jakdang.labs.api.dabin.CmsCouponManage.dto;


import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponSearchRequestDto {
    private LocalDateTime issuanceStart;
    private LocalDateTime issuanceEnd;
    private LocalDateTime providedStart;
    private LocalDateTime providedEnd;
    private LocalDateTime limitStart;
    private LocalDateTime limitEnd;
    private String issuanceUserId;
    private String providedUserId;
    private Integer issuanceStatusIndex;
    private Integer providedStatusIndex;
    private String couponName;
    private Integer couponPrice;

} 