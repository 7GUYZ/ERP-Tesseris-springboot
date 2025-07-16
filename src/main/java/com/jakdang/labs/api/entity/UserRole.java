package com.jakdang.labs.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 유저 역할 테이블
 * 
 * user_role_index -- 기본키
 * user_role_eng_nm -- 역할 영문명
 * user_role_kor_nm -- 역할 한글명
 * userRoleKorNm -- 역할 한글명 (중복 컬럼)
 * 
 * 역할들:
 * - general (일반)
 * - business (사업자)
 * - store (가맹점)
 * - admin (관리자)
 * - agent (특판부)
 * - store_sub (가맹점 서브)
 */
@Entity
@Table(name = "user_role")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_role_index")
    private Integer userRoleIndex;

    @Column(name = "user_role_eng_nm", length = 60)
    private String userRoleEngNm;

    @Column(name = "user_role_kor_nm", length = 255)
    private String userRoleKorNm;

    @Column(name = "userRoleKorNm", length = 255)
    private String userRoleKorNmDuplicate;
} 