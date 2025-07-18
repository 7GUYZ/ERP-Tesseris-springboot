package com.jakdang.labs.api.taekjun.signin.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.api.auth.dto.RoleType;
import com.jakdang.labs.entity.UserGender;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.UserTesserisRepository;
import com.jakdang.labs.api.taekjun.signin.dto.SignupRequestDTO;
import com.jakdang.labs.api.taekjun.signin.dto.SignupResponseDTO;
import com.jakdang.labs.api.taekjun.signin.repository.SignupRepository;
import com.jakdang.labs.api.taekjun.signin.repository.UserGenderRepository;
import com.jakdang.labs.api.taekjun.signin.service.ReferralService;
import com.jakdang.labs.api.taekjun.signin.dto.ReferralRequestDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SignupService {
    
    private final SignupRepository signupRepository;
    private final UserTesserisRepository userRepository;
    private final UserGenderRepository userGenderRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReferralService referralService;
    
    @Transactional
    public SignupResponseDTO signup(SignupRequestDTO requestDTO) {
        try {
            // 1. 중복 체크
            if (signupRepository.existsByEmail(requestDTO.getEmail())) {
                return new SignupResponseDTO("이미 존재하는 이메일입니다.", null, null, false);
            }
            
            if (signupRepository.existsByNickname(requestDTO.getNickname())) {
                return new SignupResponseDTO("이미 존재하는 닉네임입니다.", null, null, false);
            }
            
            if (requestDTO.getPhone() != null && signupRepository.existsByPhone(requestDTO.getPhone())) {
                return new SignupResponseDTO("이미 존재하는 전화번호입니다.", null, null, false);
            }
            
            // 2. Users 엔티티 생성 및 저장
            String referralCode = requestDTO.getReferralCode();
            if (referralCode == null && requestDTO.getReferralIdentifier() != null) {
                // 추천인 아이디로 검색하여 추천인 코드 가져오기
                Optional<UserEntity> referrerOpt = referralService.findUserByIdentifier(requestDTO.getReferralIdentifier());
                if (referrerOpt.isPresent()) {
                    referralCode = referrerOpt.get().getReferralCode();
                }
            }
            UserEntity users = UserEntity.builder()
                .id(UUID.randomUUID().toString())
                .email(requestDTO.getEmail())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .name(requestDTO.getName())
                .nickname(requestDTO.getNickname())
                .phone(requestDTO.getPhone())
                .provider(requestDTO.getProvider() != null ? requestDTO.getProvider() : "local")
                .referralCode(referralCode)
                .role(requestDTO.getRole() != null ? RoleType.valueOf(requestDTO.getRole()) : RoleType.ROLE_USER)
                .activated(true)
                .advertise(false)
                .build();
            
            UserEntity savedUsers = signupRepository.save(users);
            
            // 3. User 엔티티 생성 및 저장
            UserTesseris user = new UserTesseris();
            user.setUsersId(savedUsers);
            user.setUserBirthday(requestDTO.getUserBirthday());
            
            // 성별 설정
            if (requestDTO.getUserGenderIndex() != null) {
                UserGender userGender = userGenderRepository.findById(requestDTO.getUserGenderIndex())
                    .orElseThrow(() -> new RuntimeException("성별 정보를 찾을 수 없습니다. ID: " + requestDTO.getUserGenderIndex()));
                user.setUserGender(userGender);
            }
            
            user.setUserRoleIndex(requestDTO.getUserRoleIndex() != null ? requestDTO.getUserRoleIndex() : 1);
            user.setUserZoneCode(requestDTO.getUserZoneCode());
            user.setUserAddress(requestDTO.getUserAddress());
            user.setUserDetailAddress(requestDTO.getUserDetailAddress());
            user.setUserJumin(requestDTO.getUserJumin());
            user.setSignupPath(requestDTO.getSignupPath() != null ? requestDTO.getSignupPath() : "web");
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
            
            // 4. 추천인 코드가 있는 경우 추천인 관계 생성 및 포인트 지급 (별도 트랜잭션)
            if (referralCode != null && !referralCode.trim().isEmpty()) {
                // 비동기로 추천인 관계 생성 (실패해도 회원가입은 성공)
                try {
                    createReferralRelationAsync(referralCode, savedUser.getUserIndex());
                } catch (Exception e) {
                    // 추천인 관계 생성 실패는 회원가입을 막지 않음
                    System.err.println("추천인 관계 생성 실패: " + e.getMessage());
                    // 로그만 남기고 계속 진행
                }
            }
            
            return new SignupResponseDTO(
                "회원가입이 성공적으로 완료되었습니다.", 
                savedUsers.getId(), 
                savedUser.getUserIndex(), 
                true
            );
            
        } catch (Exception e) {
            return new SignupResponseDTO("회원가입 중 오류가 발생했습니다: " + e.getMessage(), null, null, false);
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
            // 추천인 관계 생성 실패는 회원가입을 막지 않음
            System.err.println("추천인 관계 생성 실패 (별도 트랜잭션): " + e.getMessage());
            // 별도 트랜잭션이므로 예외를 다시 던지지 않음
        }
    }
} 