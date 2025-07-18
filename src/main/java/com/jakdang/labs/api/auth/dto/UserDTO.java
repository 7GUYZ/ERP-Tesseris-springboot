package com.jakdang.labs.api.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.ZonedDateTime;

/**
 * 사용자 정보 DTO
 * 사용자의 모든 정보를 담는 클래스로, API 응답에서 사용자 정보를 전달할 때 사용
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    /** 사용자 고유 ID */
    private String id;
    
    /** 사용자 비밀번호 */
    private String password;
    
    /** 사용자 이메일 */
    private String email;
    
    /** 사용자 전화번호 */
    private String phone;
    
    /** 계정 활성화 여부 */
    private Boolean activated;
    
    /** 사용자 이름 */
    private String name;
    
    /** 로그인 제공자 (local, google, apple 등) */
    private String provider;
    
    /** 계정 생성 시간 */
    private String createdAt;
    
    /** 계정 수정 시간 */
    private String updatedAt;
    
    /** 디바이스 토큰 (푸시 알림용) */
    private String deviceToken;
    
    /** 토큰 타입 */
    private String tokenType;
    
    /** 광고 수신 동의 여부 */
    private Boolean advertise;
    
    /** 마지막 로그인 시간 */
    private ZonedDateTime lastLogin;
    
    /** 통화 가능 여부 */
    private String isCallable;
    
    /** Google 계정 연동 여부 */
    private String google;
    
    /** Facebook 계정 연동 여부 */
    private String facebook;
    
    /** Naver 계정 연동 여부 */
    private String naver;
    
    /** Kakao 계정 연동 여부 */
    private String kakao;
    
    /** Apple 계정 연동 여부 */
    private String apple;
    
    /** 사용자 닉네임 */
    private String nickname;
    
    /** 사용자 프로필 이미지 */
    private String image;
    
    /** 사용자 자기소개 */
    private String bio;
    
    /** 카운트 정보 */
    private int count;
    
    /** 사용자 등급 */
    private String grade;
    
    /** 사용자 에이전트 정보 */
    private String userAgent;
    
    /** 사용자 이름 관련 필드들 */
    private String uName, sName, cName, memberNickname;
    
    /** 사용자 역할 */
    private String role;
    
    /** 생성 시간 (JSON 포맷) */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("tCreatedAt")
    private ZonedDateTime tCreatedAt;
    
    /** 추가 사용자 정보 필드들 */
    private String mName, mId;
}
