package com.jakdang.labs.api.taekjun.checkpermission.dto;

import lombok.Data;

@Data
public class PermissionCheckDTO {
    private Integer adminTypeIndex;    // 관리자 타입 인덱스
    private Integer programIndex;      // 프로그램 인덱스
    private Integer hasInsertAuthority; // 추가 권한 여부 (1: 있음, 0: 없음)
    private Integer hasDeleteAuthority; // 삭제 권한 여부 (1: 있음, 0: 없음)
    private Integer hasUpdateAuthority; // 수정 권한 여부 (1: 있음, 0: 없음)
} 