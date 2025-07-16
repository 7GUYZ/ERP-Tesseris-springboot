package com.jakdang.labs.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 임시 통장 디테일 테이블
 * 
 * temporary_store_detail_index -- 기본키
 * user_index -- 지급 받을 관리자,사업자 유저 index (하지만 관리자는 분배 전에 이미 지급이 됨)
 * business_grade_name -- 해당 사업자의 등급 이름
 * business_area_name -- 해당 사업자의 지역 이름
 * temporary_store_cm_value -- 분배 받을 양 (CM)
 * temporary_store_cash_value -- 분배 받을 양 (CASH)
 * temporary_store_master_index -- 임시 통장 마스터 테이블 참조키값
 */
@Entity
@Table(name = "temporary_store_detail")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemporaryStoreDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "temporary_store_detail_index")
    private Integer temporaryStoreDetailIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_index", nullable = false)
    private UserTesseris userIndex;

    @Column(name = "business_grade_name", nullable = false, length = 100)
    private String businessGradeName;

    @Column(name = "business_area_name", nullable = false, length = 100)
    private String businessAreaName;

    @Column(name = "temporary_store_cm_value", nullable = false)
    private Double temporaryStoreCmValue;

    @Column(name = "temporary_store_cash_value", nullable = false)
    private Double temporaryStoreCashValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "temporary_store_master_index", nullable = false)
    private TemporaryStoreMaster temporaryStoreMasterIndex;
} 