package com.jakdang.labs.api.deokkyu.store.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CusStoreListDto {
     // User 쪽
    private String userId;        // 아이디.
    private String userName;      // 이름.
   
    private String storeCorporateName; // 상호명
    private String storeName;     // 가맹점 명
    private int customerCount; // 고객수
}
