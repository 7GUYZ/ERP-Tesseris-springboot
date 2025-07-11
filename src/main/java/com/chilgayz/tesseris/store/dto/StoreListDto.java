package com.chilgayz.tesseris.store.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreListDto {
    // User 쪽
    private String userId;        // 아이디.
    private String userName;      // 이름.
    private String userPhone;     // 핸드폰 번호.

    // Store쪽
    private String storeBossName; // 가맹점 대표자 명
    private String storeRegistrationNum;    // 사업자등록번호
    private String storeTypeTaxation; // 가맹점 유형 (부가가치세 일반과세자,..)
    private String storeCorporateName; // 상호명
    private String storeName;     // 가맹점 명
    private String storeTransactionStatus;  // 거래 상태 - 1=정상 , 0=정지.
    private String storePhone;    // 가게 대표번호.
    private LocalDateTime storeCreateDate; // 가맹점 생성 시간.
    
    // Store_Category 쪽
    private String storeCategoryName;       // 카테고리. 

    //  Store_Request_Status 쪽
    private String storeRequestStatusName;  // 승인 여부.

    // Bussiness_Grade 쪽
    private String businessGradeName;

    // User_Cm 쪽
    private Integer totalCM; // user_cm_deposit + user_cm_withdrawal  -- 총 보유CM  .
    private Integer userCmpInit ; // 초기 지급 CMP양.

    // 사업자 정보 가져오기 - user에서
    private String businessUserId;
    private String businessUserName;


 }
