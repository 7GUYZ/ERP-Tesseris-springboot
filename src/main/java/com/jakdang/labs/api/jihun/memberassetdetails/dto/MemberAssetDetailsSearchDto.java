package com.jakdang.labs.api.jihun.memberassetdetails.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberAssetDetailsSearchDto {
    private String userId;
    private String userName;
    private String userPhone;
    private Integer userRoleIndex;
    private Integer page;
    private Integer size;
} 