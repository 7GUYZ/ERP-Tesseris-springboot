package com.jakdang.labs.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 유저 정기 테이블
 * 
 * user_regular_index -- 기본키
 * user_role_index -- 유저 역할 인덱스 (외래키)
 * cm_rate -- CM 비율
 * cash_begin -- 현금 시작 금액
 * cash_end -- 현금 종료 금액
 */
@Entity
@Table(name = "user_regular")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegular {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_regular_index")
    private Integer userRegularIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_role_index")
    private UserRole userRoleIndex;

    @Column(name = "cm_rate")
    private Integer cmRate;

    @Column(name = "cash_begin")
    private Integer cashBegin;

    @Column(name = "cash_end")
    private Integer cashEnd;
} 