package com.jakdang.labs.api.taekjun.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreDTO {
    private Integer userIndex;
    private String storeName;
} 