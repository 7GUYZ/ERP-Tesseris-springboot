package com.jakdang.labs.api.deokkyu.store.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreCustomerDto {
    private String userId; // 고객 아이디
    private String userName; // 고객 이름
    private String storeCustomerStatus; // 고객 등급
} 