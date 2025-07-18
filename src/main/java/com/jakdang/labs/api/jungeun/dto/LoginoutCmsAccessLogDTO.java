package com.jakdang.labs.api.jungeun.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginoutCmsAccessLogDTO {
    private Integer cmsAccessLogUserIndex;
    private String cmsAccessUserValue;
    private String cmsAccessUserIp;
}
