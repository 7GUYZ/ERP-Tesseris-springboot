package com.jakdang.labs.api.taekjun.customermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerListResponseDTO {
    private Integer storeCustomerIndex;
    private String maskedName;
    private String maskedId;
    private String fullPhone;
    private String storeCustomerStatus;
    private String fullName;
    private String fullEmail;
    private String phoneLast4;
} 