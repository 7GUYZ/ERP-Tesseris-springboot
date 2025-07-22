package com.jakdang.labs.api.jungeun.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrentCmDTO {
    private Integer userIndex;
    private Integer currentCM; // 현재 CM 보유량 (user_cm_deposit + user_cm_withdrawal)
}
