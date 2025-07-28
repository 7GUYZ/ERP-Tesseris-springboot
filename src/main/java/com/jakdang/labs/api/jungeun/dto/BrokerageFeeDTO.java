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
    private Integer storeCashValueTotalSum;
    private Integer storeCashValueChargeSum;
    private Integer storeCashValueWaitSum;
    private Integer storeCashValueYesSum;
}
