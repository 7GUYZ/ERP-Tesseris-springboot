package com.jakdang.labs.api.taekjun.Permissionsettings.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityUpdateByIndexDTO {
    private Integer authorityTypeIndex;  // 직접 권한 인덱스 사용
    private Integer insertAuthority;
    private Integer deleteAuthority;
    private Integer updateAuthority;
    private Integer userIndex;          // 사용자 인증용 (선택사항)
    private String password;            // 사용자 인증용 (선택사항)
} 