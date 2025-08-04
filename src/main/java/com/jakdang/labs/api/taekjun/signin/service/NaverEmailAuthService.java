package com.jakdang.labs.api.taekjun.signin.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverEmailAuthService {
    
    private final JavaMailSender mailSender;
    
    // 인증 코드 저장소 (실제로는 Redis나 DB 사용 권장)
    private final Map<String, EmailAuthInfo> authCodeStore = new ConcurrentHashMap<>();
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    /**
     * 네이버 인증메일 발송 (회원가입용)
     */
    public String sendAuthEmail(String email, String name) {
        try {
            // 디버깅 로그 추가
            log.info("이메일 인증 시작 - email: {}, name: {}, fromEmail: {}", email, name, fromEmail);
            
            // fromEmail 검증
            if (fromEmail == null || fromEmail.trim().isEmpty()) {
                log.error("fromEmail이 설정되지 않았습니다. fromEmail: {}", fromEmail);
                return null;
            }
            
            // 인증 코드 생성 (6자리 숫자)
            String authCode = generateAuthCode();
            
            // 인증 정보 저장 (5분 유효)
            EmailAuthInfo authInfo = new EmailAuthInfo(email, name, authCode, System.currentTimeMillis() + 300000);
            String authToken = UUID.randomUUID().toString();
            authCodeStore.put(authToken, authInfo);
            
            // 이메일 발송
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("[Tesseris] 이메일 인증 코드");
            message.setText(
                "안녕하세요, " + name + "님!\n\n" +
                "Tesseris 회원가입을 위한 이메일 인증 코드입니다.\n\n" +
                "code: " + authCode + "\n\n" +
                "이 코드는 5분간 유효합니다.\n" +
                "본인이 요청하지 않은 경우 이 메일을 무시하세요.\n\n" +
                "감사합니다.\n" +
                "Tesseris 팀"
            );
            
            log.info("메일 발송 시도 - from: {}, to: {}", fromEmail, email);
            mailSender.send(message);
            
            log.info("회원가입 인증메일 발송 완료: email={}, authToken={}", email, authToken);
            return authToken;
            
        } catch (Exception e) {
            log.error("회원가입 인증메일 발송 실패: email={}, fromEmail={}", email, fromEmail, e);
            return null;
        }
    }
    
    /**
     * 패스워드 찾기용 인증메일 발송
     */
    public String sendPasswordFindAuthEmail(String email, String name) {
        try {
            // 인증 코드 생성 (6자리 숫자)
            String authCode = generateAuthCode();
            
            // 인증 정보 저장 (5분 유효)
            EmailAuthInfo authInfo = new EmailAuthInfo(email, name, authCode, System.currentTimeMillis() + 300000);
            String authToken = UUID.randomUUID().toString();
            authCodeStore.put(authToken, authInfo);
            
            // 이메일 발송
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("[Tesseris] 비밀번호 찾기 인증 코드");
            message.setText(
                "안녕하세요, " + name + "님!\n\n" +
                "Tesseris 비밀번호 찾기 및 변경을 위한 이메일 인증 코드입니다.\n\n" +
                "code: " + authCode + "\n\n" +
                "이 코드는 5분간 유효합니다.\n" +
                "본인이 요청하지 않은 경우 이 메일을 무시하세요.\n\n" +
                "감사합니다.\n" +
                "Tesseris 팀"
            );
            
            mailSender.send(message);
            
            log.info("패스워드 찾기 인증메일 발송 완료: email={}, authToken={}", email, authToken);
            return authToken;
            
        } catch (Exception e) {
            log.error("패스워드 찾기 인증메일 발송 실패: email={}", email, e);
            return null;
        }
    }
    
    /**
     * 인증 코드 검증 (회원가입용)
     */
    public boolean verifyAuthCode(String authToken, String authCode) {
        try {
            EmailAuthInfo authInfo = authCodeStore.get(authToken);
            
            if (authInfo == null) {
                log.warn("인증 토큰을 찾을 수 없음: authToken={}", authToken);
                return false;
            }
            
            // 만료 시간 체크
            if (System.currentTimeMillis() > authInfo.getExpireTime()) {
                log.warn("인증 코드 만료: authToken={}", authToken);
                authCodeStore.remove(authToken);
                return false;
            }
            
            // 인증 코드 일치 여부 체크
            if (!authCode.equals(authInfo.getAuthCode())) {
                log.warn("인증 코드 불일치: authToken={}, inputCode={}, expectedCode={}", 
                    authToken, authCode, authInfo.getAuthCode());
                return false;
            }
            
            // 인증 성공 시 저장소에서 제거
            authCodeStore.remove(authToken);
            log.info("회원가입 인증 성공: email={}", authInfo.getEmail());
            return true;
            
        } catch (Exception e) {
            log.error("인증 코드 검증 실패: authToken={}, authCode={}", authToken, authCode, e);
            return false;
        }
    }
    
    /**
     * 패스워드 찾기용 인증 코드 검증 (토큰 유지)
     */
    public boolean verifyPasswordFindAuthCode(String authToken, String authCode) {
        try {
            EmailAuthInfo authInfo = authCodeStore.get(authToken);
            
            if (authInfo == null) {
                log.warn("패스워드 찾기 인증 토큰을 찾을 수 없음: authToken={}", authToken);
                return false;
            }
            
            // 만료 시간 체크
            if (System.currentTimeMillis() > authInfo.getExpireTime()) {
                log.warn("패스워드 찾기 인증 코드 만료: authToken={}", authToken);
                authCodeStore.remove(authToken);
                return false;
            }
            
            // 인증 코드 일치 여부 체크
            if (!authCode.equals(authInfo.getAuthCode())) {
                log.warn("패스워드 찾기 인증 코드 불일치: authToken={}, inputCode={}, expectedCode={}", 
                    authToken, authCode, authInfo.getAuthCode());
                return false;
            }
            
            // 인증 성공 시에도 토큰 유지 (패스워드 변경 단계에서 사용)
            log.info("패스워드 찾기 인증 성공: email={}", authInfo.getEmail());
            return true;
            
        } catch (Exception e) {
            log.error("패스워드 찾기 인증 코드 검증 실패: authToken={}, authCode={}", authToken, authCode, e);
            return false;
        }
    }
    
    /**
     * 패스워드 찾기용 토큰 제거
     */
    public void removePasswordFindAuthToken(String authToken) {
        try {
            EmailAuthInfo authInfo = authCodeStore.remove(authToken);
            if (authInfo != null) {
                log.info("패스워드 찾기 토큰 제거 완료: email={}", authInfo.getEmail());
            }
        } catch (Exception e) {
            log.error("패스워드 찾기 토큰 제거 실패: authToken={}", authToken, e);
        }
    }
    
    /**
     * 인증 상태 조회
     */
    public Map<String, Object> getAuthStatus(String authToken) {
        try {
            EmailAuthInfo authInfo = authCodeStore.get(authToken);
            
            if (authInfo == null) {
                return Map.of(
                    "valid", false,
                    "message", "인증 토큰을 찾을 수 없습니다."
                );
            }
            
            if (System.currentTimeMillis() > authInfo.getExpireTime()) {
                authCodeStore.remove(authToken);
                return Map.of(
                    "valid", false,
                    "message", "인증 코드가 만료되었습니다."
                );
            }
            
            return Map.of(
                "valid", true,
                "email", authInfo.getEmail(),
                "name", authInfo.getName(),
                "expireTime", authInfo.getExpireTime()
            );
            
        } catch (Exception e) {
            log.error("인증 상태 조회 실패: authToken={}", authToken, e);
            return Map.of(
                "valid", false,
                "message", "인증 상태 조회 중 오류가 발생했습니다."
            );
        }
    }
    
    /**
     * 6자리 인증 코드 생성
     */
    private String generateAuthCode() {
        return String.format("%06d", (int)(Math.random() * 1000000));
    }
    
    /**
     * 인증 정보를 담는 내부 클래스
     */
    private static class EmailAuthInfo {
        private final String email;
        private final String name;
        private final String authCode;
        private final long expireTime;
        
        public EmailAuthInfo(String email, String name, String authCode, long expireTime) {
            this.email = email;
            this.name = name;
            this.authCode = authCode;
            this.expireTime = expireTime;
        }
        
        public String getEmail() { return email; }
        public String getName() { return name; }
        public String getAuthCode() { return authCode; }
        public long getExpireTime() { return expireTime; }
    }
} 