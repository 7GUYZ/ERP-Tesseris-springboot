package com.jakdang.labs.api.taekjun.Permissionsettings.dto;

import java.util.List;

import lombok.Data;

@Data
public class BulkAuthorityUpdateDTO {
    private List<AuthorityUpdateByIndexDTO> authorities;
    private Integer userIndex;
    private String password;
} 