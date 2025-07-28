package com.jakdang.labs.api.deokkyu.modal_admin.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessManTransactionHistoryDto { // 사업자 수당 받은 내역 보내기 (temporary_store_detail)
    
     // 1.
     private String temporaryStoreMasterIndexName;                
     // temporary_store_master_index로 temporary_store_master테이블에서 user_index 얻고, 
     // user_tesseris로 users_id 조회후에 users테이블에서 해당 users_id의 user_name 얻기
    
     
     // 3. 거래 발생 시간 (temporary_store_master 테이블)
     private LocalDateTime temporaryStoreMasterDistributionTime;       
     // temporary_store_master_index 가지고 temporary_store_master 테이블 가서
     // temporary_store_master_charge_time 얻기 (수당 발생 시간)
     
     // 4. 거래 금액 (temporary_store_detail 테이블)
     private Integer temporaryStoreCmValue;                     // 분배 받은 금액

 
}
