package com.chilgayz.tesseris.store.dto;

import lombok.*;

import java.time.LocalDateTime;

import com.chilgayz.tesseris.store.entity.Store;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreDto {
    // User 쪽
    private String userId;        // 아이디
    private String userName;      // 이름
    private String userPhone;     // 핸드폰 번호

    // Store쪽
    private String storeCorporateName; // 가맹점 대표자 명
    private String storeName;     // 가맹점 명
    private String storeTransactionStatus;  // 거래 상태 - 1=정상 , 0=정지
    private String storeRegistrationNum;    // 사업자등록번호
    private String storePhone;    // 가게 대표번호
    private String storeTypeTaxation; // 가맹점 유형
    
    // Store_Category 쪽
    private String storeCategoryName;       // 카테고리

    //  Store_Request_Status 쪽
    private String storeRequestStatusName;  // 승인 여부


 }
