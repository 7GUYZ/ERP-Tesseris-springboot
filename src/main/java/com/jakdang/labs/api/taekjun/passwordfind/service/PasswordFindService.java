package com.jakdang.labs.api.taekjun.passwordfind.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.api.auth.repository.AuthRepository;
import com.jakdang.labs.api.taekjun.signin.service.NaverEmailAuthService;
import com.jakdang.labs.api.taekjun.passwordfind.dto.PasswordFindRequestDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordFindService {
    
    private final AuthRepository authRepository;
    private final NaverEmailAuthService naverEmailAuthService;
    private final PasswordEncoder passwordEncoder;
    
    // 패스워드 찾기용 인증 토큰 저장소
    private final Map<String, PasswordFindAuthInfo> passwordFindAuthStore = new ConcurrentHashMap<>();
    
    /**
     * 이메일로 사용자 존재 여부 확인 및 인증 메일 발송
     */
    public String sendPasswordFindAuthEmail(String email, String name) {
        try {
            // 1. 사용자 존재 여부 확인
            Optional<UserEntity> userOpt = authRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                log.warn("존재하지 않는 이메일: {}", email);
                return null;
            }
            
            UserEntity user = userOpt.get();
            
            // 2. 이름 일치 여부 확인
            if (!name.equals(user.getName())) {
                log.warn("이름이 일치하지 않음: 입력={}, DB={}", name, user.getName());
                return null;
            }
            
            // 3. 패스워드 찾기용 인증 메일 발송
            String authToken = naverEmailAuthService.sendPasswordFindAuthEmail(email, name);
            
            if (authToken != null) {
                // 4. 패스워드 찾기용 인증 정보 저장 (5분 유효)
                PasswordFindAuthInfo authInfo = new PasswordFindAuthInfo(
                    email, name, authToken, "", System.currentTimeMillis() + 300000
                );
                passwordFindAuthStore.put(authToken, authInfo);
                
                log.info("패스워드 찾기 인증 메일 발송 완료: email={}, authToken={}", email, authToken);
            }
            
            return authToken;
            
        } catch (Exception e) {
            log.error("패스워드 찾기 인증 메일 발송 오류: ", e);
            return null;
        }
    }
    
    /**
     * 이메일 인증 코드 검증 (패스워드 찾기용)
     */
    public boolean validatePasswordFindAuth(PasswordFindRequestDTO requestDTO) {
        try {
            if (requestDTO.getEmail() == null || requestDTO.getAuthCode() == null || requestDTO.getAuthToken() == null) {
                return false;
            }
            
            // 패스워드 찾기용 인증 정보 조회
            PasswordFindAuthInfo authInfo = passwordFindAuthStore.get(requestDTO.getAuthToken());
            if (authInfo == null) {
                log.warn("패스워드 찾기 인증 토큰을 찾을 수 없음: authToken={}", requestDTO.getAuthToken());
                return false;
            }
            
            // 만료 시간 체크
            if (System.currentTimeMillis() > authInfo.getExpireTime()) {
                log.warn("패스워드 찾기 인증 코드 만료: authToken={}", requestDTO.getAuthToken());
                passwordFindAuthStore.remove(requestDTO.getAuthToken());
                return false;
            }
            
            // 패스워드 찾기용 이메일 인증으로 검증 (토큰 유지)
            boolean isValid = naverEmailAuthService.verifyPasswordFindAuthCode(requestDTO.getAuthToken(), requestDTO.getAuthCode());
            
            if (isValid) {
                log.info("패스워드 찾기 인증 성공: email={}", requestDTO.getEmail());
                // 인증 성공해도 토큰은 유지 (패스워드 변경 단계에서 사용)
            }
            
            return isValid;
            
        } catch (Exception e) {
            log.error("패스워드 찾기 인증 검증 오류: ", e);
            return false;
        }
    }
    
    /**
     * 패스워드 변경
     */
    @Transactional
    public boolean changePassword(PasswordFindRequestDTO requestDTO) {
        try {
            // 1. 인증 토큰 유효성 재확인
            PasswordFindAuthInfo authInfo = passwordFindAuthStore.get(requestDTO.getAuthToken());
            if (authInfo == null) {
                log.warn("패스워드 변경 시 인증 토큰을 찾을 수 없음: authToken={}", requestDTO.getAuthToken());
                return false;
            }
            
            // 2. 사용자 조회
            Optional<UserEntity> userOpt = authRepository.findByEmail(requestDTO.getEmail());
            if (userOpt.isEmpty()) {
                log.warn("존재하지 않는 사용자: {}", requestDTO.getEmail());
                return false;
            }
            
            UserEntity user = userOpt.get();
            
            // 3. 새 비밀번호 암호화
            String encodedPassword = passwordEncoder.encode(requestDTO.getNewPassword());
            
            // 4. 비밀번호 업데이트
            user.setPassword(encodedPassword);
            authRepository.save(user);
            
            // 5. 인증 토큰 제거 (사용 완료)
            passwordFindAuthStore.remove(requestDTO.getAuthToken());
            naverEmailAuthService.removePasswordFindAuthToken(requestDTO.getAuthToken());
            
            log.info("패스워드 변경 완료: {}", requestDTO.getEmail());
            return true;
            
        } catch (Exception e) {
            log.error("패스워드 변경 오류: ", e);
            return false;
        }
    }
    
    /**
     * 패스워드 찾기용 인증 정보를 담는 내부 클래스
     */
    private static class PasswordFindAuthInfo {
        private final String email;
        private final String name;
        private final String authToken;
        private final String authCode;
        private final long expireTime;
        
        public PasswordFindAuthInfo(String email, String name, String authToken, String authCode, long expireTime) {
            this.email = email;
            this.name = name;
            this.authToken = authToken;
            this.authCode = authCode;
            this.expireTime = expireTime;
        }
        
        public String getEmail() { return email; }
        public String getName() { return name; }
        public String getAuthToken() { return authToken; }
        public String getAuthCode() { return authCode; }
        public long getExpireTime() { return expireTime; }
    }
} 