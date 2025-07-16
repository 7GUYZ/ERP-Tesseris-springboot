package com.jakdang.labs.api.auth.dto;

import com.jakdang.labs.api.auth.entity.UserEntity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Spring Security 사용자 상세 정보 클래스
 * UserDetails 인터페이스를 구현하여 Spring Security에서 사용자 인증 정보를 관리
 */
@Getter
@RequiredArgsConstructor
@ToString
public class CustomUserDetails implements UserDetails {

    /** 사용자 엔티티 */
    private final UserEntity userEntity;


    /**
     * 사용자의 권한 정보를 반환
     * Spring Security에서 권한 기반 접근 제어에 사용
     * 
     * @return 사용자 권한 목록
     */
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

        authorities.add((GrantedAuthority) () -> userEntity.getRole().getRole());

        return authorities;
    }

    /**
     * 사용자 ID 반환
     * 
     * @return 사용자 ID
     */
    public String getUserId() {
        return userEntity.getId();
    }

    /**
     * 사용자 비밀번호 반환
     * 
     * @return 사용자 비밀번호
     */
    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    /**
     * 사용자명 반환 (이름 사용)
     * 
     * @return 사용자명
     */
    @Override
    public String getUsername() {
        return userEntity.getName();
    }

    /**
     * 사용자 역할 반환
     * 
     * @return 사용자 역할
     */
    public RoleType getRole() {
        return userEntity.getRole();
    }

    /**
     * 사용자 이메일 반환
     * 
     * @return 사용자 이메일
     */
    public String getEmail() {
        return userEntity.getEmail();
    }

    // 정은 추가 0710
    public String getCreatedAt() {
        return userEntity.getCreatedAt().toString();
    }

    // 정은 추가 0710
    public String getPhone() {
        return userEntity.getPhone();
    }


    /**
     * 계정 만료 여부 확인
     * 
     * @return 계정 만료 여부
     */
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    /**
     * 계정 잠금 여부 확인
     * 
     * @return 계정 잠금 여부
     */
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    /**
     * 자격 증명 만료 여부 확인
     * 
     * @return 자격 증명 만료 여부
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

//    /**
//     * 계정 활성화 여부 확인 (주석 처리됨)
//     * 
//     * @return 계정 활성화 여부
//     */
//    @Override
//    public boolean isEnabled() {
//        return UserDetails.super.isEnabled();
//    }

    /**
     * 계정 활성화 여부 확인
     * 사용자 엔티티의 activated 필드를 기반으로 판단
     * 
     * @return 계정 활성화 여부
     */
    @Override
    public boolean isEnabled() {
        return userEntity.getActivated();
    }

}
