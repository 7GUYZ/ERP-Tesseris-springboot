package com.jakdang.labs.api.auth.entity;

import com.jakdang.labs.api.auth.dto.RoleType;
import com.jakdang.labs.api.auth.dto.SignUpDTO;
import com.jakdang.labs.api.auth.dto.UserUpdateDTO;
import com.jakdang.labs.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 사용자 엔티티 클래스
 * 데이터베이스의 users 테이블과 매핑되는 사용자 정보 엔티티
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@ToString(exclude = "members")
public class UserEntity extends BaseEntity {

    /** 사용자 고유 ID (UUID) */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "char(36)")
    private String id;

    /** 사용자 비밀번호 (암호화된 값) */
    @Column(name = "password", length = 255)
    private String password;

    /** 사용자 이메일 (로그인 ID로 사용) */
    @Column(name = "email", length = 255)
    private String email;

    /** 사용자 전화번호 */
    @Column(name = "phone", length = 255)
    private String phone;

    /** 계정 활성화 여부 (0: 비활성, 1: 활성) */
    @Column(name = "activated", columnDefinition = "tinyint(1) default 1")
    private Boolean activated;

    /** 사용자 역할 (ADMIN, USER, TEACHER 등) */
    @Column(name = "role", length = 255)
    @Enumerated(EnumType.STRING)
    private RoleType role;

    /** 사용자 이름 */
    @Column(name = "name", length = 255)
    private String name;

    /** 로그인 제공자 (local, google, apple 등) */
    @Column(name = "provider", length = 255)
    private String provider;

    /** 디바이스 토큰 (푸시 알림용) */
    @Column(name = "device_token", columnDefinition = "longtext")
    private String deviceToken;

    /** 토큰 타입 */
    @Column(name = "token_type", columnDefinition = "int(11)")
    private Integer tokenType;

    /** 광고 수신 동의 여부 */
    @Column(name = "advertise", columnDefinition = "tinyint(1)")
    private Boolean advertise;

    /** 마지막 로그인 시간 */
    @Column(name = "last_login")
    @Temporal(TemporalType.DATE)
    private Date lastLogin;

    /** 통화 가능 여부 */
    @Column(name = "is_callable", length = 255)
    private String isCallable;

    /** 리프레시 토큰 */
    @Column(name = "refresh_token", columnDefinition = "longtext")
    private String refreshToken;

    /** Google 계정 연동 정보 */
    @Column(name = "google", length = 255)
    private String google;

    /** Kakao 계정 연동 정보 */
    @Column(name = "kakao", length = 255)
    private String kakao;

    /** Apple 계정 연동 정보 */
    @Column(name = "apple", length = 255)
    private String apple;

    /** Facebook 계정 연동 정보 */
    @Column(name = "facebook", length = 255)
    private String facebook;

    /** Naver 계정 연동 정보 */
    @Column(name = "naver", length = 255)
    private String naver;

    /** 추천 코드 */
    @Column(name = "referral_code", length = 50)
    private String referralCode;

    /** 사용자 닉네임 */
    @Column(name = "nickname", length = 30)
    private String nickname;

    /** 사용자 프로필 이미지 URL */
    @Column(name = "image", columnDefinition = "TEXT")
    private String image;

    /** 사용자 자기소개 */
    @Column(name = "bio", length = 100)
    private String bio;

    /**
     * SignUpDTO로부터 UserEntity 생성
     * 회원가입 시 DTO 정보를 엔티티로 변환
     * 
     * @param signUpDTO 회원가입 정보 DTO
     * @return UserEntity 객체
     */
    public static UserEntity fromDto(SignUpDTO signUpDTO) {
        return UserEntity.builder()
                .email(signUpDTO.getEmail())
                .password(signUpDTO.getPassword())
                .name(signUpDTO.getName())
                .phone(signUpDTO.getPhone())
                .provider(signUpDTO.getProvider())
                .build();
    }

    /**
     * 사용자 정보 업데이트
     * UserUpdateDTO의 정보로 사용자 정보를 업데이트
     * 
     * @param dto 업데이트할 사용자 정보 DTO
     */
    public void update(UserUpdateDTO dto) {
        setName(dto.getName());
        //setEmail(dto.getEmail()); // 위험 - 이메일 변경은 별도 처리 필요
        setNickname(dto.getNickname());
        setImage(dto.getImage());
        setBio(dto.getBio());
    }

}