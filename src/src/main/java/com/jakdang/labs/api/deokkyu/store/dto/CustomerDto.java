package com.jakdang.labs.api.deokkyu.store.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDto { // /storecustomerlist front로 보낼 고객 정보 (가맹점 하나 클릭시)
    private String userId; // 고객 아이디
    private String userName; // 고객 이름
    private String storeCustomerStatus; // 고객 등급
} 