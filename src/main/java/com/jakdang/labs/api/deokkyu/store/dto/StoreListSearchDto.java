package com.jakdang.labs.api.deokkyu.store.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreListSearchDto {
    private String userId;
    private String userName;
    private String userPhone;
    private String storeBossName;
    private String storeRequestStatusName;
    private String storeTransactionStatus;
    private String storeCorporateName;
    private String storeName;
    private String businessUserName;
    private String storeCreateDateStart;
    private String storeCreateDateEnd;
}
