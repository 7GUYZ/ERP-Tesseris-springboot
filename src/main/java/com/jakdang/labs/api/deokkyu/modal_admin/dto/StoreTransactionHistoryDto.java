package com.jakdang.labs.api.deokkyu.modal_admin.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreTransactionHistoryDto {
    
    // 1. 사용자 기본 정보 (users 테이블)
    private String userPassword;                        // 사용자 비밀번호
    
    // 2, 3. 사용자 상세 정보 (user_tesseris 테이블)
    private String userBirthday;                        // 사용자 생년월일
    private Integer userGenderIndex;                    // 사용자 성별 인덱스
    
    // 4. 거래 종류 이름 (user_cm_log_payment 테이블)
    private String userCmLogPaymentName;                // 거래 종류 이름 (입금,출금)
    
    // 5. 거래 타입 이름 (user_cm_log_transaction_type 테이블)
    private String userCmLogTransactionTypeName;        // 거래 타입 이름 (중개수수료,본사CM지급 등등)
    
    // 6. 거래 요청인 (user_cm_log 테이블)
    private Integer userIndexEventTrigger;              // 거래 요청인 인덱스
    private String userIndexEventTriggerId;             // 거래 요청인 사용자 ID (users 테이블의 id)
    
    // 7. 거래 발생 시간 (user_cm_log 테이블)
    private LocalDateTime userCmLogCreateTime;          // 거래 발생 시간
    
    // 8. 거래 메모 (user_cm_log 테이블)
    private String userCmLogReason;                     // 거래에 대한 메모
    
    // 9. 거래 금액 (user_cm_log 테이블)
    private Integer userCmLogValue;                     // 거래에 사용된 단위
    
    // 10, 11, 12. 가맹점 사진 정보 (store 테이블)
    private String storeProntPhoto;                     // 가맹점 정면 사진
    private String storeBusinessLicensePhoto;           // 사업자등록증 사진
    private String storeSignPhoto;                      // 간판 사진

} 