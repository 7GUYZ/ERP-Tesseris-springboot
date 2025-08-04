package com.jakdang.labs.api.taekjun.Permissionsettings.dto;

import java.util.List;

import lombok.Data;

@Data
public class BulkAuthorityDTO {
    private List<AuthorityUpdateDTO> authorities;
    private Integer userIndex;
    private String password;
} 