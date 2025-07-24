package com.jakdang.labs.api.taekjun.checkpermission.dto;

import lombok.Data;

@Data
public class PermissionCheckDTO {
    private Integer adminTypeIndex;    // 관리자 타입 인덱스
    private Integer programIndex;      // 프로그램 인덱스
    private Boolean hasInsertAuthority; // 추가 권한 여부
    private Boolean hasDeleteAuthority; // 삭제 권한 여부
    private Boolean hasUpdateAuthority; // 수정 권한 여부
} 