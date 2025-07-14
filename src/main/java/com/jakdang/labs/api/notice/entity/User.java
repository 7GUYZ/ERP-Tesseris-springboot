package com.jakdang.labs.api.notice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import com.google.auto.value.AutoValue.Builder;

import com.jakdang.labs.api.updateLog.entity.UserRole;

@Entity
@Table(name = "`User`")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_index")
    private Integer userIndex;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_role_index")
    private Integer userRoleIndex;

    @Column(name = "user_pw")
    private String userPw;

    @Column(name = "user_name")
    private String userName;

    // 연관관계 추가
    @ManyToOne
    @JoinColumn(name = "user_role_index", insertable = false, updatable = false)
    private UserRole userRole;

    // ... 기타 필드 생략
}