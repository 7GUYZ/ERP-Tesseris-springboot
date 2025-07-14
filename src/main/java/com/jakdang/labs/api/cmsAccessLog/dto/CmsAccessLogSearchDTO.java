package com.jakdang.labs.api.cmsAccessLog.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CmsAccessLogSearchDTO {
    private String userId;
    private String userName;
    private String cmsAccessUserIp;
    private String adminTypeIndex;
    private String cmsAccessUserTimeStart;
    private String cmsAccessUserTimeEnd;
} 