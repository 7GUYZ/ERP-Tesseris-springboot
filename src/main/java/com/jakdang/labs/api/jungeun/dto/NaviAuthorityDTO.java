package com.jakdang.labs.api.jungeun.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NaviAuthorityDTO {
    private Integer programIndex;
    private Integer adminTypeIndex;
    private Long menuIndex;
}
