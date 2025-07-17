package com.jakdang.labs.api.taekjun.signin.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.api.auth.dto.RoleType;
import com.jakdang.labs.entity.UserGender;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.entity.UserCm;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.UserTesserisRepository;
import com.jakdang.labs.api.taekjun.signin.repository.UserCmRepository;
import com.jakdang.labs.api.taekjun.signin.dto.Step1AgreementDTO;
import com.jakdang.labs.api.taekjun.signin.dto.Step2PhoneAuthDTO;
import com.jakdang.labs.api.taekjun.signin.dto.Step3UserInfoDTO;
import com.jakdang.labs.api.taekjun.signin.repository.SignupRepository;
import com.jakdang.labs.api.taekjun.signin.repository.UserGenderRepository;
import com.jakdang.labs.api.taekjun.signin.service.ReferralService;
import com.jakdang.labs.api.taekjun.signin.dto.ReferralRequestDTO;
import com.jakdang.labs.api.taekjun.signin.service.ImportAuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StepwiseSignupService {
    
    private final SignupRepository signupRepository;
    private final UserTesserisRepository userRepository;
    private final UserCmRepository userCmRepository;
    private final UserGenderRepository userGenderRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReferralService referralService;
    private final ImportAuthService importAuthService;
    
    /**
     * 1단계: 약관 동의
     */
    public boolean validateAgreements(Step1AgreementDTO agreementDTO) {
        // 필수 약관 동의 확인
        return "Y".equals(agreementDTO.getServiceAgree()) && 
               "Y".equals(agreementDTO.getPrivacyAgree());
    }
    
    /**
     * 2단계: 휴대폰 인증
     */
    public boolean validatePhoneAuth(Step2PhoneAuthDTO phoneAuthDTO) {
        // NICE 본인인증 결과 검증
        if (phoneAuthDTO.getPhone() == null || phoneAuthDTO.getAuthCode() == null) {
            return false;
        }
        
        // 아임포트 API로 인증 결과 검증
        return importAuthService.verifyAuthResult(phoneAuthDTO.getImpUid(), phoneAuthDTO.getAuthCode());
    }
    
    /**
     * 3단계: 최종 회원가입
     */
    @Transactional
    public String finalSignup(Step3UserInfoDTO userInfoDTO) {
        try {
            // 1. 중복 체크
            if (signupRepository.existsByEmail(userInfoDTO.getEmail())) {
                throw new RuntimeException("이미 존재하는 이메일입니다.");
            }
            
            if (signupRepository.existsByNickname(userInfoDTO.getNickname())) {
                throw new RuntimeException("이미 존재하는 닉네임입니다.");
            }
            
            // 2. 추천인 코드 찾기
            String referralCode = null;
            if (userInfoDTO.getReferralId() != null && !userInfoDTO.getReferralId().trim().isEmpty()) {
                Optional<UserEntity> referrerOpt = referralService.findUserByIdentifier(userInfoDTO.getReferralId());
                if (referrerOpt.isPresent()) {
                    referralCode = referrerOpt.get().getReferralCode();
                }
            }
            
            // 3. Users 엔티티 생성 및 저장
            UserEntity users = UserEntity.builder()
                .id(UUID.randomUUID().toString())
                .email(userInfoDTO.getEmail())
                .password(passwordEncoder.encode(userInfoDTO.getPassword()))
                .name(userInfoDTO.getName())
                .nickname(userInfoDTO.getNickname())
                .provider("local")
                .referralCode(referralCode)
                .role(RoleType.ROLE_USER)
                .activated(true)
                .advertise(false)
                .build();
            
            UserEntity savedUsers = signupRepository.save(users);
            
            // 4. UserTesseris 엔티티 생성 및 저장
            UserTesseris user = new UserTesseris();
            user.setUsersId(savedUsers);
            user.setUserRoleIndex(1);
            user.setSignupPath("web");
            user.setUserAmount(0);
            user.setUserTransactionStatus("NORMAL");
            user.setUserLoginStatus("OFFLINE");
            user.setUserMarketingChecked("N");
            user.setUserAdvertisementChecked("N");
            user.setUserPositionChecked("N");
            user.setUserLoginStatus2(0);
            user.setUserUpgrade("N");
            user.setUserVip("N");
            
            UserTesseris savedUser = userRepository.save(user);
            
            // 5. UserCm 엔티티 생성 및 저장 (핀번호 포함)
            UserCm userCm = UserCm.builder()
                .userCmIndex(savedUser.getUserIndex())
                .userCmDeposit(0)
                .userCmWithdrawal(0)
                .userCashDeposit(0)
                .userCashWithdrawal(0)
                .userCmpDeposit(0)
                .userCmpWithdrawal(0)
                .userCmpInit(0)
                .userCmPincode(userInfoDTO.getPin())
                .build();
            
            userCmRepository.save(userCm);
            
            // 6. 추천인 관계 생성 (별도 트랜잭션)
            if (referralCode != null && !referralCode.trim().isEmpty()) {
                try {
                    createReferralRelationAsync(referralCode, savedUser.getUserIndex());
                } catch (Exception e) {
                    System.err.println("추천인 관계 생성 실패: " + e.getMessage());
                }
            }
            
            return savedUsers.getId();
            
        } catch (Exception e) {
            throw new RuntimeException("회원가입 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    /**
     * 추천인 관계 생성을 별도 트랜잭션으로 처리
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void createReferralRelationAsync(String referralCode, Integer userIndex) {
        try {
            ReferralRequestDTO referralRequest = new ReferralRequestDTO();
            referralRequest.setReferralCode(referralCode);
            referralRequest.setUserIndex(userIndex);
            
            referralService.createReferralRelation(referralRequest);
        } catch (Exception e) {
            System.err.println("추천인 관계 생성 실패 (별도 트랜잭션): " + e.getMessage());
        }
    }
} 