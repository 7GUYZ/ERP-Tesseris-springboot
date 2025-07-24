package com.jakdang.labs.api.dabin.CmsCommissionManage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommissionPaymentUpdateRequest {
    private List<Integer> detailIndexes;
    private String paymentStatus; // "지급" 또는 "미지급"
    private String adminPassword;
} 