package com.jakdang.labs.api.jiyun.cmsAccessLog.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CmsAccessLogDTO {
    private String userId;
    private String userName;
    private String cmsAccessUserValue;
    private String adminTypeName;
    private String cmsAccessUserIp;
    private String cmsAccessUserTime;
} 