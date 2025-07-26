package com.jakdang.labs.api.taekjun.signin.service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.Instant;
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
import com.jakdang.labs.api.taekjun.signin.dto.Step2EmailAuthDTO;
import com.jakdang.labs.api.taekjun.signin.dto.Step3UserInfoDTO;
import com.jakdang.labs.api.taekjun.signin.repository.SignupRepository;
import com.jakdang.labs.api.taekjun.signin.service.ReferralService;
import com.jakdang.labs.api.taekjun.signin.dto.ReferralRequestDTO;
import com.jakdang.labs.api.taekjun.signin.service.NaverEmailAuthService;
import com.jakdang.labs.api.auth.repository.AuthRepository;
import com.jakdang.labs.entity.SuggestionUser;
import com.jakdang.labs.api.taekjun.signin.repository.SuggestionUserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StepwiseSignupService {
    
    private final SignupRepository signupRepository;
    private final UserTesserisRepository userRepository;
    private final UserCmRepository userCmRepository;
    @Autowired
    @Qualifier("userGenderJtjRepo")
    private JpaRepository<UserGender, Integer> userGenderRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReferralService referralService;
    private final NaverEmailAuthService naverEmailAuthService;
    private final SuggestionUserRepository suggestionUserRepository;
    
    /**
     * 한국 시간으로 현재 시간을 가져오는 메서드
     */
    private Instant getKoreanTime() {
        return ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant();
    }
    
    /**
     * UTC 시간으로 현재 시간을 가져오는 메서드
     */
    private Instant getUtcTime() {
        return Instant.now();
    }
    
    /**
     * 1단계: 약관 동의
     */
    public boolean validateAgreements(Step1AgreementDTO agreementDTO) {
        // 필수 약관 동의 확인
        return "Y".equals(agreementDTO.getServiceAgree()) && 
               "Y".equals(agreementDTO.getPrivacyAgree());
    }
    
    /**
     * 2단계: 이메일 인증 메일 발송
     */
    public String sendAuthEmail(String email, String name) {
        return naverEmailAuthService.sendAuthEmail(email, name);
    }
    
    /**
     * 2단계: 이메일 인증 검증
     */
    public boolean validateEmailAuth(Step2EmailAuthDTO emailAuthDTO) {
        // 이메일 인증 결과 검증
        if (emailAuthDTO.getEmail() == null || emailAuthDTO.getAuthCode() == null || emailAuthDTO.getAuthToken() == null) {
            return false;
        }
        
        // 네이버 이메일 인증으로 검증
        return naverEmailAuthService.verifyAuthCode(emailAuthDTO.getAuthToken(), emailAuthDTO.getAuthCode());
    }
    
    /**
     * 3단계: 최종 회원가입
     */
    @Transactional
    public String finalSignup(Step3UserInfoDTO userInfoDTO) {
        try {
            // 한국 시간 설정
            Instant koreanTime = java.time.ZonedDateTime.now(java.time.ZoneId.of("Asia/Seoul")).toInstant();
            
            // 1. 이메일 중복 체크
            if (signupRepository.existsByEmail(userInfoDTO.getEmail())) {
                throw new RuntimeException("이미 등록된 이메일입니다.");
            }
            
            if (signupRepository.existsByNickname(userInfoDTO.getNickname())) {
                throw new RuntimeException("이미 존재하는 닉네임입니다.");
            }
            
            // 2. 추천인 코드 찾기
            String referralCode = null;
            if (userInfoDTO.getReferralId() != null && !userInfoDTO.getReferralId().trim().isEmpty()) {
                // 추천인 ID가 직접 referralCode인 경우
                referralCode = userInfoDTO.getReferralId();
            } else {
                // 추천인 미입력 시 1번 계정의 referralCode 사용
                Optional<UserTesseris> defaultReferrer = userRepository.findByUserIndex(1);
                if (defaultReferrer.isPresent()) {
                    referralCode = defaultReferrer.get().getUsersId().getReferralCode();
                }
            }
            
            // 3. Users 엔티티 생성 및 저장 (UUID 자동 생성 사용)
            UserEntity users = UserEntity.builder()
                .email(userInfoDTO.getEmail())
                .password(passwordEncoder.encode(userInfoDTO.getPassword()))
                .name(userInfoDTO.getName())
                .nickname(userInfoDTO.getNickname())
                .phone(userInfoDTO.getPhone()) // 전화번호 추가
                .provider("local")
                .referralCode(referralCode)
                .role(RoleType.ROLE_USER)
                .activated(true)
                .advertise(false)
                .build();
            
            // 한국 시간으로 시간 필드 설정 (저장 전에 설정)
            // Instant koreanTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant();
            users.setCreatedAt(koreanTime);
            users.setUpdatedAt(koreanTime);
            
            UserEntity savedUsers = signupRepository.save(users);
            
            // UserEntity의 created_at, updated_at 값 직접 설정 (한국 시간)
            signupRepository.updateUserTimestamps(savedUsers.getId(), koreanTime, koreanTime);
            
            // 4. 새 사용자에게 추천인 코드 생성
            String newUserReferralCode = referralService.generateReferralCode(savedUsers.getId());
            savedUsers.setReferralCode(newUserReferralCode);
            signupRepository.save(savedUsers);
            
            // UserEntity의 updated_at 값 업데이트 (한국 시간)
            signupRepository.updateUserTimestamp(savedUsers.getId(), koreanTime);
            
            // 5. UserTesseris 엔티티 생성 및 저장
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
            
            // 생일 정보 설정
            if (userInfoDTO.getBirthday() != null && !userInfoDTO.getBirthday().trim().isEmpty()) {
                try {
                    LocalDate birthday = LocalDate.parse(userInfoDTO.getBirthday());
                    user.setUserBirthday(birthday);
                } catch (Exception e) {
                    // 생일 형식이 잘못된 경우 무시
                    System.err.println("생일 형식이 잘못되었습니다: " + userInfoDTO.getBirthday());
                }
            }
            
            // 성별 정보 설정
            if (userInfoDTO.getUserGenderIndex() != null) {
                Optional<UserGender> genderOpt = userGenderRepository.findById(userInfoDTO.getUserGenderIndex());
                if (genderOpt.isPresent()) {
                    user.setUserGender(genderOpt.get());
                }
            }
            
            // 주소 정보 설정
            if (userInfoDTO.getZoneCode() != null) {
                user.setUserZoneCode(userInfoDTO.getZoneCode());
            }
            if (userInfoDTO.getAddress() != null) {
                user.setUserAddress(userInfoDTO.getAddress());
            }
            if (userInfoDTO.getDetailAddress() != null) {
                user.setUserDetailAddress(userInfoDTO.getDetailAddress());
            }
            
            // 은행 정보 설정 (기본값: null)
            // user.setUserBank(null); // 선택사항이므로 null로 설정
            
            UserTesseris savedUser = userRepository.save(user);
            
            // 6. UserCm 엔티티 생성 및 저장 (핀번호 포함)
            UserCm userCm = UserCm.builder()
                .userCmIndex(savedUser.getUserIndex()) // UserTesseris의 userIndex와 동일하게 설정
                .userCmDeposit(0)
                .userCmWithdrawal(0)
                .userCashDeposit(0)
                .userCashWithdrawal(0)
                .userCmpDeposit(0)
                .userCmpWithdrawal(0)
                .userCmpInit(0)
                .userCmPincode(userInfoDTO.getPin())
                .build();
            
            // UserCm 저장
            UserCm savedUserCm = userCmRepository.save(userCm);
            
            // UserCm의 created_at, updated_at 값 직접 설정 (한국 시간)
            userCmRepository.updateUserCmTimestamps(savedUserCm.getUserCmIndex(), koreanTime, koreanTime);
            
            // 7. 한국 시간으로 시간 필드 설정 (UserEntity만)
            // Instant koreanTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant();
            // savedUsers.setCreatedAt(koreanTime);
            // savedUsers.setUpdatedAt(koreanTime);
            
            // 8. 시간이 설정된 엔티티를 다시 저장
            signupRepository.save(savedUsers);
            
            // 9. 추천인 관계 생성은 별도 서비스에서 처리하도록 제거
            // (SigninController에서 처리)
            
            // 10. UserCm의 userCmIndex를 UserTesseris의 userIndex로 업데이트
            // ID 변경이 불가능하므로 별도의 업데이트 쿼리 사용
            // 이는 다른 서비스에서 userCmRepository.findById(userTesseris.getUserIndex())로 조회하기 때문
            // savedUserCm.setUserCmIndex(savedUser.getUserIndex());
            // userCmRepository.save(savedUserCm);
            
            // TODO: 나중에 별도의 업데이트 쿼리로 userCmIndex를 설정해야 함
            // 현재는 자동 생성된 ID를 사용하고, 다른 서비스에서는 findByUserCmIndex를 사용하도록 수정 필요
            
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