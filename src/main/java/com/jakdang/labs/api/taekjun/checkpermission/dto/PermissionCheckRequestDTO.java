package com.jakdang.labs.api.taekjun.checkpermission.dto;

import lombok.Data;

@Data
public class PermissionCheckRequestDTO {
    private Integer adminTypeIndex;    // 관리자 타입 인덱스
    private Integer programIndex;      // 프로그램 인덱스
} 