package com.jakdang.labs.api.taekjun.signin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jakdang.labs.api.taekjun.signin.service.StepwiseSignupService;
import com.jakdang.labs.api.taekjun.signin.service.ReferralService;
import com.jakdang.labs.api.taekjun.address.service.KakaoAddressService;
import com.jakdang.labs.api.taekjun.signin.dto.Step3UserInfoDTO;
import com.jakdang.labs.api.taekjun.signin.dto.ReferralRequestDTO;
import com.jakdang.labs.api.taekjun.signin.dto.UserSearchDTO;
import com.jakdang.labs.api.taekjun.signin.dto.UserSearchResultDTO;
import com.jakdang.labs.api.taekjun.signin.repository.SignupRepository;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.UserTesserisRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/signin")
@RequiredArgsConstructor
public class SigninController {
    
    private final StepwiseSignupService stepwiseSignupService;
    private final ReferralService referralService;
    private final KakaoAddressService kakaoAddressService;
    private final SignupRepository signupRepository;
    private final UserTesserisRepository userTesserisRepository;
    
    /**
     * 주소 검색 API
     */
    @GetMapping("/search-address")
    public ResponseEntity<Map<String, Object>> searchAddress(@RequestParam String query) {
        try {
            log.info("주소 검색 요청 - query: {}", query);
            
            Map<String, Object> result = kakaoAddressService.searchAddress(query);
            
            if (result != null) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", result,
                    "message", "주소 검색 완료"
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "주소를 찾을 수 없습니다."
                ));
            }
            
        } catch (Exception e) {
            log.error("주소 검색 중 오류 발생: ", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "주소 검색 중 오류가 발생했습니다."
            ));
        }
    }
    
    /**
     * 키워드 검색 API (상세 주소 검색용)
     */
    @GetMapping("/search-address-keyword")
    public ResponseEntity<Map<String, Object>> searchAddressKeyword(@RequestParam String query) {
        try {
            log.info("키워드 검색 요청 - query: {}", query);
            
            Map<String, Object> result = kakaoAddressService.searchKeyword(query);
            
            if (result != null) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", result,
                    "message", "키워드 검색 완료"
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "검색 결과를 찾을 수 없습니다."
                ));
            }
            
        } catch (Exception e) {
            log.error("키워드 검색 중 오류 발생: ", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "키워드 검색 중 오류가 발생했습니다."
            ));
        }
    }
    
    /**
     * 이메일 인증 메일 발송
     */
    @PostMapping("/send-auth-email")
    public ResponseEntity<Map<String, Object>> sendAuthEmail(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String name = request.get("name");
            
            if (email == null || name == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "이메일과 이름을 입력해주세요."
                ));
            }
            
            String authToken = stepwiseSignupService.sendAuthEmail(email, name);
            
            if (authToken != null) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "authToken", authToken,
                    "message", "인증 메일이 발송되었습니다. 이메일을 확인해주세요."
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "인증 메일 발송에 실패했습니다."
                ));
            }
        } catch (Exception e) {
            log.error("인증 메일 발송 오류: ", e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "서버 오류가 발생했습니다."
            ));
        }
    }
    
    /**
     * 이메일 인증 코드 검증
     */
    @PostMapping("/verify-auth-email")
    public ResponseEntity<Map<String, Object>> verifyAuthEmail(@RequestBody Map<String, String> request) {
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
            
            // 임시 DTO 생성
            com.jakdang.labs.api.taekjun.signin.dto.Step2EmailAuthDTO emailAuthDTO = 
                new com.jakdang.labs.api.taekjun.signin.dto.Step2EmailAuthDTO();
            emailAuthDTO.setEmail(email);
            emailAuthDTO.setName(name);
            emailAuthDTO.setAuthCode(authCode);
            emailAuthDTO.setAuthToken(authToken);
            
            boolean isValid = stepwiseSignupService.validateEmailAuth(emailAuthDTO);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "isValid", isValid,
                "message", isValid ? "이메일 인증이 성공했습니다." : "이메일 인증에 실패했습니다."
            ));
        } catch (Exception e) {
            log.error("이메일 인증 검증 오류: ", e);
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

            // 회원가입 성공 후 추천인 관계 생성 (트랜잭션 분리)
            if (userInfoDTO.getReferralId() != null && !userInfoDTO.getReferralId().trim().isEmpty()) {
                // 추천인 코드 찾기
                var referrerOpt = referralService.findUserByIdentifier(userInfoDTO.getReferralId());
                if (referrerOpt.isPresent()) {
                    String referralCode = referrerOpt.get().getReferralCode();
                    // 추천인 관계 생성
                    var referralRequest = new ReferralRequestDTO();
                    referralRequest.setReferralCode(referralCode);
                    // userId(UUID)로 UserTesseris 조회
                    var userTesserisOpt = userTesserisRepository.findByUsersId_Id(userId);
                    if (userTesserisOpt.isPresent()) {
                        referralRequest.setUserIndex(userTesserisOpt.get().getUserIndex());
                        referralService.createReferralRelation(referralRequest);
                    }
                }
            }

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

    /**
     * 이메일/닉네임 중복 검사 API
     */
    @GetMapping("/check-duplicate")
    public ResponseEntity<Map<String, Object>> checkDuplicate(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String nickname) {
        boolean emailExists = false;
        boolean nicknameExists = false;
        if (email != null && !email.isBlank()) {
            emailExists = signupRepository.existsByEmail(email);
        }
        if (nickname != null && !nickname.isBlank()) {
            nicknameExists = signupRepository.existsByNickname(nickname);
        }
        return ResponseEntity.ok(Map.of(
            "emailExists", emailExists,
            "nicknameExists", nicknameExists
        ));
    }
} 