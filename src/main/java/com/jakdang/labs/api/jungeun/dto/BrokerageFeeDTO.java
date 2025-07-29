package com.jakdang.labs.api.jungeun.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrokerageFeeDTO {
    private Integer cmValueTotalSum;      // 전체 CM 수수료
    private Integer cmValueChargeSum;     // 충전 중 CM 수수료
    private Integer cmValueWaitSum;       // 대기 중 CM 수수료
    private Integer cmValueYesSum;        // 완료된 CM 수수료
}
