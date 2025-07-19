package com.jakdang.labs.api.jungeun.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBusinessFilteredListDTO {
    private String userEmail; // 우리는 ID 가입이 아니라 이메일 가입임.
    private String userName;
    private String gradeName;
    private String bossEmail; // 마찬가지로 상급자 ID가 아닌 이메일을 보여주기.
    private Long totalCm;
    private Long storeCount;
}
