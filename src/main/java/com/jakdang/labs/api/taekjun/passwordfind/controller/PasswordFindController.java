package com.jakdang.labs.api.taekjun.passwordfind.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jakdang.labs.api.taekjun.passwordfind.service.PasswordFindService;
import com.jakdang.labs.api.taekjun.passwordfind.dto.PasswordFindRequestDTO;
import com.jakdang.labs.api.taekjun.passwordfind.dto.PasswordFindResponseDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/passwordfind")
@RequiredArgsConstructor
public class PasswordFindController {
    
    private final PasswordFindService passwordFindService;
    
    /**
     * 패스워드 찾기 - 이메일 인증 메일 발송
     */
    @PostMapping("/send-auth-email")
    public ResponseEntity<Map<String, Object>> sendPasswordFindAuthEmail(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String name = request.get("name");
            
            if (email == null || name == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "이메일과 이름을 입력해주세요."
                ));
            }
            
            // 이메일 형식 검증
            if (!isValidEmailFormat(email)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "올바른 이메일 형식이 아닙니다."
                ));
            }
            
            // 인증 메일 발송
            String authToken = passwordFindService.sendPasswordFindAuthEmail(email, name);
            
            if (authToken != null) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "authToken", authToken,
                    "message", "패스워드 찾기 인증 메일이 발송되었습니다. 이메일을 확인해주세요."
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "등록되지 않은 이메일이거나 이름이 일치하지 않습니다."
                ));
            }
        } catch (Exception e) {
            log.error("패스워드 찾기 인증 메일 발송 오류: ", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "서버 오류가 발생했습니다."
            ));
        }
    }
    
    /**
     * 패스워드 찾기 - 이메일 인증 코드 검증
     */
    @PostMapping("/verify-auth-email")
    public ResponseEntity<Map<String, Object>> verifyPasswordFindAuthEmail(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String name = request.get("name");
            String authCode = request.get("authCode");
            String authToken = request.get("authToken");
            
            if (email == null || name == null || authCode == null || authToken == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "모든 필수 정보를 입력해주세요."
                ));
            }
            
            // DTO 생성
            PasswordFindRequestDTO requestDTO = new PasswordFindRequestDTO();
            requestDTO.setEmail(email);
            requestDTO.setName(name);
            requestDTO.setAuthCode(authCode);
            requestDTO.setAuthToken(authToken);
            
            boolean isValid = passwordFindService.validatePasswordFindAuth(requestDTO);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "isValid", isValid,
                "message", isValid ? "이메일 인증이 성공했습니다." : "인증 코드가 올바르지 않습니다."
            ));
        } catch (Exception e) {
            log.error("패스워드 찾기 이메일 인증 검증 오류: ", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "서버 오류가 발생했습니다."
            ));
        }
    }
    
    /**
     * 패스워드 찾기 - 새 패스워드 설정
     */
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String name = request.get("name");
            String authCode = request.get("authCode");
            String authToken = request.get("authToken");
            String newPassword = request.get("newPassword");
            
            if (email == null || name == null || authCode == null || authToken == null || newPassword == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "모든 필수 정보를 입력해주세요."
                ));
            }
            
            // 새 비밀번호 유효성 검사
            if (newPassword.length() < 8) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "비밀번호는 8자 이상이어야 합니다."
                ));
            }
            
            // DTO 생성
            PasswordFindRequestDTO requestDTO = new PasswordFindRequestDTO();
            requestDTO.setEmail(email);
            requestDTO.setName(name);
            requestDTO.setAuthCode(authCode);
            requestDTO.setAuthToken(authToken);
            requestDTO.setNewPassword(newPassword);
            
            // 인증 검증
            boolean isValid = passwordFindService.validatePasswordFindAuth(requestDTO);
            if (!isValid) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "인증 코드가 올바르지 않습니다."
                ));
            }
            
            // 패스워드 변경
            boolean changeSuccess = passwordFindService.changePassword(requestDTO);
            
            if (changeSuccess) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "비밀번호가 성공적으로 변경되었습니다. 로그인 페이지로 이동합니다."
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "패스워드 변경에 실패했습니다."
                ));
            }
        } catch (Exception e) {
            log.error("패스워드 변경 오류: ", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "서버 오류가 발생했습니다."
            ));
        }
    }
    
    /**
     * 이메일 형식 검증
     */
    private boolean isValidEmailFormat(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        // 기본적인 이메일 형식 검증 (정규식)
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }
} 