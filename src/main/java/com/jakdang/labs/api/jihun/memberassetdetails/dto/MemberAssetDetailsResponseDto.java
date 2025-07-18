package com.jakdang.labs.api.jihun.memberassetdetails.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberAssetDetailsResponseDto {
    private Integer userIndex;
    private String userId;
    private String userName;
    private String userPhone;
    private String userRoleKorNm;
    private String storeName;
    private String userCmCurrent;
    private String userCmpCurrent;
    private String userCashCurrent;
    private LocalDateTime userCreateTime;
    private String userBankName;
    private String userBankNumber;
    private String userBankHolder;
    private String userJumin;
    private String suggestionUserId;
    private String suggestionUserName;
    private String temporaryStoreCashValue;
    
    // 프론트엔드 호환성을 위한 별칭 필드들
    public String getId() { return userId; }
    public String getName() { return userName; }
    public String getPhone() { return userPhone; }
    public String getGrade() { return userRoleKorNm; }
    public String getFranchiseName() { return storeName; }
    public String getCmHeld() { return userCmCurrent; }
    public String getCmpHeld() { return userCmpCurrent; }
    public String getCashHeld() { return userCashCurrent; }
    public String getRegistrationDate() { 
        return userCreateTime != null ? userCreateTime.toString() : null; 
    }
} 