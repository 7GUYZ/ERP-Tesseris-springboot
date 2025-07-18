package com.jakdang.labs.api.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 사용자 토큰 엔티티 클래스
 * 사용자의 리프레시 토큰 정보를 관리하는 엔티티
 */
@Entity
@Table(name = "user_tokens")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserToken {

    /** 토큰 고유 ID (UUID) */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "char(36)", nullable = false)
    private String id;

    /** 사용자 ID */
    @Column(name = "user_id", columnDefinition = "char(36)", nullable = false)
    private String userId;

    /** 토큰 생성 시간 */
    @CreationTimestamp
    @Column(name = "created_at", columnDefinition = "timestamp default current_timestamp()")
    private LocalDateTime createdAt;

    /** 토큰 만료 시간 (Unix timestamp) */
    @Column(name = "expires_at", columnDefinition = "bigint(20)")
    private Long expiresAt;

    /** 사용자 에이전트 정보 */
    @Column(name = "user_agent", columnDefinition = "longtext")
    private String userAgent;

    /** 리프레시 토큰 값 */
    @Column(name = "refresh_token", columnDefinition = "longtext")
    private String refreshToken;

    /** 토큰 폐기 여부 (0: 유효, 1: 폐기) */
    @Column(name = "is_revoked", columnDefinition = "tinyint(1) default 0")
    private Boolean isRevoked;

    /** 마지막 사용 시간 */
    @UpdateTimestamp
    @Column(name = "last_used_at", columnDefinition = "timestamp default current_timestamp() on update current_timestamp()")
    private LocalDateTime lastUsedAt;
}