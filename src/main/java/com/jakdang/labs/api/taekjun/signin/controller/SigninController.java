package com.jakdang.labs.api.taekjun.signin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jakdang.labs.api.taekjun.signin.service.ImportAuthService;
import com.jakdang.labs.api.taekjun.signin.service.StepwiseSignupService;
import com.jakdang.labs.api.taekjun.signin.service.ReferralService;
import com.jakdang.labs.api.taekjun.signin.dto.Step3UserInfoDTO;
import com.jakdang.labs.api.taekjun.signin.dto.ReferralRequestDTO;
import com.jakdang.labs.api.taekjun.signin.dto.UserSearchDTO;
import com.jakdang.labs.api.taekjun.signin.dto.UserSearchResultDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/signin")
@RequiredArgsConstructor
public class SigninController {
    
    private final ImportAuthService importAuthService;
    private final StepwiseSignupService stepwiseSignupService;
    private final ReferralService referralService;
    
    /**
     * 본인인증 토큰 생성
     */
    @PostMapping("/generate-auth-token")
    public ResponseEntity<Map<String, Object>> generateAuthToken(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String phone = request.get("phone");
            String birth = request.get("birth");
            String genderDigit = request.get("genderDigit");
            String carrier = request.get("carrier");
            
            String impUid = importAuthService.generateAuthToken(name, phone, birth, genderDigit, carrier);
            
            if (impUid != null) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "impUid", impUid,
                    "message", "본인인증 OTP가 요청되었습니다."
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "본인인증 OTP 요청에 실패했습니다."
                ));
            }
        } catch (Exception e) {
            log.error("본인인증 OTP 요청 오류: ", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "서버 오류가 발생했습니다."
            ));
        }
    }
    
    /**
     * 본인인증 OTP 완료
     */
    @PostMapping("/verify-auth-result")
    public ResponseEntity<Map<String, Object>> verifyAuthResult(@RequestBody Map<String, String> request) {
        try {
            String impUid = request.get("impUid");
            String otp = request.get("otp");
            
            boolean isValid = importAuthService.verifyAuthResult(impUid, otp);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "isValid", isValid,
                "message", isValid ? "본인인증이 성공했습니다." : "본인인증에 실패했습니다."
            ));
        } catch (Exception e) {
            log.error("본인인증 OTP 완료 오류: ", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "서버 오류가 발생했습니다."
            ));
        }
    }
    
    /**
     * 본인인증 상태 조회
     */
    @GetMapping("/get-auth-status")
    public ResponseEntity<Map<String, Object>> getAuthStatus(@RequestParam String impUid) {
        try {
            Map<String, Object> status = importAuthService.getAuthStatus(impUid);
            
            if (status != null) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "status", status
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "본인인증 상태를 조회할 수 없습니다."
                ));
            }
        } catch (Exception e) {
            log.error("본인인증 상태 조회 오류: ", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "서버 오류가 발생했습니다."
            ));
        }
    }
    
    /**
     * 최종 회원가입 (3단계)
     */
    @PostMapping("/step3-final-signup")
    public ResponseEntity<Map<String, Object>> finalSignup(@RequestBody Step3UserInfoDTO userInfoDTO) {
        try {
            String userId = stepwiseSignupService.finalSignup(userInfoDTO);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "userId", userId,
                "message", "회원가입이 성공적으로 완료되었습니다."
            ));
        } catch (Exception e) {
            log.error("회원가입 오류: ", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * 사용자 검색 (추천인 찾기)
     */
    @GetMapping("/search-user")
    public ResponseEntity<UserSearchResultDTO> searchUser(@RequestParam String identifier) {
        try {
            UserSearchResultDTO result = referralService.searchUserByIdentifier(identifier);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("사용자 검색 오류: ", e);
            return ResponseEntity.internalServerError().body(new UserSearchResultDTO(
                null, null, null, null, null, false, "서버 오류가 발생했습니다."
            ));
        }
    }
    
    /**
     * 추천인 관계 생성
     */
    @PostMapping("/create-referral")
    public ResponseEntity<Map<String, Object>> createReferral(@RequestBody ReferralRequestDTO requestDTO) {
        try {
            var result = referralService.createReferralRelation(requestDTO);
            
            return ResponseEntity.ok(Map.of(
                "success", result.isSuccess(),
                "message", result.getMessage(),
                "referralCode", result.getReferralCode(),
                "referralCount", result.getReferralCount(),
                "referrerName", result.getReferrerName(),
                "referrerNickname", result.getReferrerNickname()
            ));
        } catch (Exception e) {
            log.error("추천인 관계 생성 오류: ", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "서버 오류가 발생했습니다."
            ));
        }
    }
} 