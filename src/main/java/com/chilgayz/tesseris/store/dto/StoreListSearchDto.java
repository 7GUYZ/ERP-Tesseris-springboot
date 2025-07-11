package com.chilgayz.tesseris.store.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
