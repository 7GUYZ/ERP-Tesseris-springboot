package com.jakdang.labs.api.taekjun.Permissionsettings.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthorityProgramDTO {
    private Integer authorityTypeIndex;
    private Integer adminTypeIndex;
    private String adminTypeName;
    private Integer programIndex;  // programIndex 추가
    private String programName;
    private Integer insertAuthority;
    private Integer deleteAuthority;
    private Integer updateAuthority;
    
    // JPQL 쿼리와 정확히 일치하는 생성자 (7개 파라미터)
    public AuthorityProgramDTO(Integer authorityTypeIndex, Integer adminTypeIndex, 
                             Integer programIndex, String programName, 
                             Integer insertAuthority, Integer deleteAuthority, Integer updateAuthority) {
        this.authorityTypeIndex = authorityTypeIndex;
        this.adminTypeIndex = adminTypeIndex;
        this.programIndex = programIndex;
        this.programName = programName;
        this.insertAuthority = insertAuthority;
        this.deleteAuthority = deleteAuthority;
        this.updateAuthority = updateAuthority;
    }
} 