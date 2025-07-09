//package com.jakdang.labs.security.oauth.dto;
//
//import com.jakdang.labs.api.auth.entity.UserEntity;
//import lombok.Getter;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.Map;
//
///**
// * OAuth2 사용자 상세 정보 클래스 (주석 처리됨)
// * UserDetails와 OAuth2User 인터페이스를 모두 구현하여
// * 일반 로그인과 OAuth2 로그인을 모두 지원하는 클래스
// * 현재 주석 처리되어 사용되지 않음
// */
//@Getter
//public class PrincipalDetails implements UserDetails, OAuth2User {
//
//    /** 사용자 엔티티 */
//    private UserEntity user;
//    
//    /** OAuth2 사용자 속성 */
//    private Map<String, Object> attributes;
//
//    /**
//     * 일반 로그인 생성자
//     * 
//     * @param user 사용자 엔티티
//     */
//    public PrincipalDetails(UserEntity user) {
//        this.user = user;
//    }
//
//    /**
//     * OAuth2 로그인 생성자
//     * 
//     * @param user 사용자 엔티티
//     * @param attributes OAuth2 사용자 속성
//     */
//    public PrincipalDetails(UserEntity user, Map<String, Object> attributes) {
//        this.user = user;
//        this.attributes = attributes;
//    }
//
//    /**
//     * OAuth2User 인터페이스 메소드
//     * OAuth2 사용자 속성 반환
//     * 
//     * @return OAuth2 사용자 속성
//     */
//    @Override
//    public Map<String, Object> getAttributes() {
//        return attributes;
//    }
//
//    /**
//     * OAuth2User 인터페이스 메소드
//     * 사용자명 반환
//     * 
//     * @return 사용자명
//     */
//    @Override
//    public String getName() {
//        return user.getName();
//    }
//
//    /**
//     * 사용자 ID 반환
//     * 
//     * @return 사용자 ID
//     */
//    public String getId() {
//        return user.getId();
//    }
//
//    /**
//     * 사용자 역할 반환
//     * 
//     * @return 사용자 역할
//     */
//    public String getRole(){
//        return user.getRole().toString();
//    }
//
//    /**
//     * UserDetails 인터페이스 메소드
//     * 사용자 권한 반환
//     * 
//     * @return 사용자 권한 목록
//     */
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        List<GrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));
//        return authorities;
//    }
//
//    /**
//     * UserDetails 인터페이스 메소드
//     * 사용자 비밀번호 반환
//     * 
//     * @return 사용자 비밀번호
//     */
//    @Override
//    public String getPassword() {
//        return user.getPassword();
//    }
//
//    /**
//     * UserDetails 인터페이스 메소드
//     * 사용자명 반환 (이메일 사용)
//     * 
//     * @return 사용자 이메일
//     */
//    @Override
//    public String getUsername() {
//        return user.getEmail();
//    }
//
//    /**
//     * UserDetails 인터페이스 메소드
//     * 계정 만료 여부 확인
//     * 
//     * @return 계정 만료 여부 (항상 true)
//     */
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    /**
//     * UserDetails 인터페이스 메소드
//     * 계정 잠금 여부 확인
//     * 
//     * @return 계정 잠금 여부 (항상 true)
//     */
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    /**
//     * UserDetails 인터페이스 메소드
//     * 자격 증명 만료 여부 확인
//     * 
//     * @return 자격 증명 만료 여부 (항상 true)
//     */
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    /**
//     * UserDetails 인터페이스 메소드
//     * 계정 활성화 여부 확인
//     * 
//     * @return 계정 활성화 여부 (항상 true)
//     */
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//}