package com.jakdang.labs.api.dabin.FrontEventCouponRegistration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRegistrationRequest {
    private String eventName;
    private String eventCondition;
    private Integer eventDownLimit;
    private List<Long> couponIssuanceIndexList;
} 