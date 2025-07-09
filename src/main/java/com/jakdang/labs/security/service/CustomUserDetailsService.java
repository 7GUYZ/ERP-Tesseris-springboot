package com.jakdang.labs.security.service;

import com.jakdang.labs.api.auth.dto.CustomUserDetails;
import com.jakdang.labs.api.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Spring Security 사용자 상세 정보 서비스
 * UserDetailsService를 구현하여 Spring Security에서 사용자 인증 정보를 로드하는 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthRepository userRepository;

    /**
     * 사용자명(이메일)으로 사용자 상세 정보 로드
     * Spring Security에서 인증 시 호출되는 메서드
     * 
     * @param email 사용자 이메일 (사용자명으로 사용)
     * @return UserDetails 객체
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
     */
    @Override
    public UserDetails loadUserByUsername(String email) {
        log.info(email);
        return userRepository.findByEmail(email)
                .map(CustomUserDetails::new)
                .orElseThrow( () -> new UsernameNotFoundException("UserDetails is null"));
    }
}
