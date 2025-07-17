package com.jakdang.labs.api.taekjun.Permissionsettings.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityUpdateDTO {
    private Integer adminTypeIndex;
    private Integer programIndex;
    private Integer insertAuthority;
    private Integer deleteAuthority;
    private Integer updateAuthority;
    private Integer userIndex;
    private String password;
} 