package com.jakdang.labs.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 유저 CM 로그 값 타입 테이블
 * 
 * user_cm_log_value_type_index -- 기본키
 * user_cm_log_value_type_name -- 값 타입 이름
 * 
 * 값 타입들:
 * - CMP (캐시 머니 포인트)
 * - CM (캐시 머니)
 * - Cash (현금)
 */
@Entity
@Table(name = "user_cm_log_value_type")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCmLogValueType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_cm_log_value_type_index")
    private Integer userCmLogValueTypeIndex;

    @Column(name = "user_cm_log_value_type_name", length = 30)
    private String userCmLogValueTypeName;
} 