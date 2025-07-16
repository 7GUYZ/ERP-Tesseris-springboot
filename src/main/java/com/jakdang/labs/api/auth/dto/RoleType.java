package com.jakdang.labs.api.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 역할 타입 열거형
 * Spring Security에서 사용하는 사용자 권한을 정의
 */
@Getter
@RequiredArgsConstructor
public enum RoleType {
    /** 관리자 권한 */
    ROLE_ADMIN("ROLE_ADMIN"),
    
    /** 조직 관리자 권한 */
    ROLE_ORGANIZATION("ROLE_ORGANIZATION"),
    
    /** 일반 사용자 권한 */
    ROLE_USER("ROLE_USER"),
    
    /** 교사 권한 */
    ROLE_TEACHER("ROLE_TEACHER");

    /** 역할 문자열 값 */
    private final String role;

}
