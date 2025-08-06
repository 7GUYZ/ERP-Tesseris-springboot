package com.jakdang.labs.api.taekjun.signin.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jakdang.labs.api.taekjun.signin.dto.ReferralListDTO;
import com.jakdang.labs.api.taekjun.signin.dto.ReferralRequestDTO;
import com.jakdang.labs.api.taekjun.signin.dto.ReferralResponseDTO;
import com.jakdang.labs.api.taekjun.signin.dto.UserSearchDTO;
import com.jakdang.labs.api.taekjun.signin.dto.UserSearchResultDTO;
import com.jakdang.labs.entity.SuggestionUser;
import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.entity.UserCm;
import com.jakdang.labs.entity.UserCmLog;
import com.jakdang.labs.entity.UserCmLogPayment;
import com.jakdang.labs.entity.UserCmLogTransactionType;
import com.jakdang.labs.entity.UserCmLogValueType;
import com.jakdang.labs.api.taekjun.signin.repository.SuggestionUserRepository;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.UserRepository;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.UserTesserisRepository;
import com.jakdang.labs.api.taekjun.signin.repository.UserCmRepository;
import com.jakdang.labs.api.taekjun.signin.repository.UserCmLogRepository;
import com.jakdang.labs.api.taekjun.signin.repository.UserCmLogPaymentRepository;
import com.jakdang.labs.api.taekjun.signin.repository.UserCmLogTransactionTypeRepository;
import com.jakdang.labs.api.taekjun.signin.repository.UserCmLogValueTypeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReferralService {
    
    private final SuggestionUserRepository suggestionUserRepository;
    private final UserRepository userRepository;
    private final UserTesserisRepository userTesserisRepository;
    private final UserCmRepository userCmRepository;
    private final UserCmLogRepository userCmLogRepository;
    private final UserCmLogPaymentRepository userCmLogPaymentRepository;
    private final UserCmLogTransactionTypeRepository userCmLogTransactionTypeRepository;
    private final UserCmLogValueTypeRepository userCmLogValueTypeRepository;
    
    /**
     * 추천인 코드로 사용자 찾기 (referralCode, 이메일, 닉네임 모두 검색)
     */
    public Optional<UserEntity> findUserByReferralCode(String referralCode) {
        // 1. 먼저 referralCode로 검색
        Optional<UserEntity> userOpt = userRepository.findByReferralCode(referralCode);
        if (userOpt.isPresent()) {
            return userOpt;
        }
        
        // 2. 이메일로 검색
        userOpt = userRepository.findByEmail(referralCode);
        if (userOpt.isPresent()) {
            return userOpt;
        }
        
        // 3. 닉네임으로 검색
        userOpt = userRepository.findByNickname(referralCode);
        if (userOpt.isPresent()) {
            return userOpt;
        }
        
        // 4. 이메일 또는 닉네임으로 검색
        return userRepository.findByEmailOrNickname(referralCode);
    }
    
    /**
     * 아이디(이메일 또는 닉네임)로 사용자 찾기
     */
    public Optional<UserEntity> findUserByIdentifier(String identifier) {
        return userRepository.findByEmailOrNickname(identifier);
    }
    
    /**
     * 검색 타입에 따라 사용자 찾기
     */
    public Optional<UserEntity> findUserBySearchType(UserSearchDTO searchDTO) {
        if ("email".equals(searchDTO.getSearchType())) {
            return userRepository.findByEmail(searchDTO.getSearchValue());
        } else if ("nickname".equals(searchDTO.getSearchType())) {
            return userRepository.findByNickname(searchDTO.getSearchValue());
        } else {
            // 기본적으로 이메일 또는 닉네임으로 검색
            return userRepository.findByEmailOrNickname(searchDTO.getSearchValue());
        }
    }
    
    /**
     * 아이디로 사용자 검색 결과 반환
     */
    public UserSearchResultDTO searchUserByIdentifier(String identifier) {
        log.info("사용자 검색 시작 - identifier: {}", identifier);
        
        Optional<UserEntity> userOpt = findUserByIdentifier(identifier);
        
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            log.info("사용자 검색 성공 - userId: {}, email: {}, nickname: {}", 
                    user.getId(), user.getEmail(), user.getNickname());
            return new UserSearchResultDTO(
                user.getId(),
                user.getName(),
                user.getNickname(),
                user.getEmail(),
                user.getReferralCode(),
                true,
                "사용자를 찾았습니다."
            );
        } else {
            log.warn("사용자 검색 실패 - identifier: {}", identifier);
            return new UserSearchResultDTO(
                null, null, null, null, null, false,
                "해당 아이디로 사용자를 찾을 수 없습니다."
            );
        }
    }
    
    /**
     * 검색 타입에 따라 사용자 검색 결과 반환
     */
    public UserSearchResultDTO searchUserByType(UserSearchDTO searchDTO) {
        Optional<UserEntity> userOpt = findUserBySearchType(searchDTO);
        
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            return new UserSearchResultDTO(
                user.getId(),
                user.getName(),
                user.getNickname(),
                user.getEmail(),
                user.getReferralCode(),
                true,
                "사용자를 찾았습니다."
            );
        } else {
            return new UserSearchResultDTO(
                null, null, null, null, null, false,
                "해당 " + searchDTO.getSearchType() + "으로 사용자를 찾을 수 없습니다."
            );
        }
    }
    
    /**
     * 추천인 관계 생성 및 포인트 지급
     */
    @Transactional
    public ReferralResponseDTO createReferralRelation(ReferralRequestDTO requestDTO) {
        try {
            log.info("추천인 관계 생성 시작: referralCode={}, userIndex={}", requestDTO.getReferralCode(), requestDTO.getUserIndex());
            
            // 1. 추천인 코드로 추천인 찾기
            Optional<UserEntity> referrerOpt = findUserByReferralCode(requestDTO.getReferralCode());
            if (referrerOpt.isEmpty()) {
                log.warn("유효하지 않은 추천인 코드: {}", requestDTO.getReferralCode());
                return new ReferralResponseDTO("유효하지 않은 추천인 코드입니다.", false, null, 0, null, null);
            }
            
            UserEntity referrer = referrerOpt.get();
            log.info("추천인 찾기 성공: referrerId={}, referrerName={}", referrer.getId(), referrer.getName());
            
            // 2. 추천받는 사용자 찾기
            Optional<UserTesseris> userOpt = userTesserisRepository.findById(requestDTO.getUserIndex());
            if (userOpt.isEmpty()) {
                log.warn("사용자를 찾을 수 없음: userIndex={}", requestDTO.getUserIndex());
                return new ReferralResponseDTO("사용자를 찾을 수 없습니다.", false, null, 0, null, null);
            }
            
            UserTesseris user = userOpt.get();
            log.info("추천받는 사용자 찾기 성공: userIndex={}, userName={}", user.getUserIndex(), user.getUsersId().getName());
            
            // 3. 자기 자신을 추천인으로 등록하려는 경우 방지
            if (user.getUsersId().getId().equals(referrer.getId())) {
                log.warn("자기 자신을 추천인으로 등록 시도: userId={}", user.getUsersId().getId());
                return new ReferralResponseDTO("자기 자신을 추천인으로 등록할 수 없습니다.", false, null, 0, null, null);
            }
            
            // 4. 추천인 UserTesseris 찾기
            Optional<UserTesseris> referrerUserOpt = userTesserisRepository.findByUsersId(referrer);
            if (referrerUserOpt.isEmpty()) {
                log.warn("추천인 UserTesseris 정보를 찾을 수 없음: referrerId={}", referrer.getId());
                return new ReferralResponseDTO("추천인 정보를 찾을 수 없습니다.", false, null, 0, null, null);
            }
            
            UserTesseris referrerUser = referrerUserOpt.get();
            log.info("추천인 UserTesseris 찾기 성공: referrerUserIndex={}", referrerUser.getUserIndex());
            
            // 5. 추천받는 사람이 이미 추천인을 가지고 있는지 확인 (회원가입 시에만 생성 가능)
            if (suggestionUserRepository.existsBySuggestionUserIndex(user.getUserIndex())) {
                log.warn("이미 추천인이 존재함: userIndex={}", user.getUserIndex());
                return new ReferralResponseDTO("이미 추천인이 존재합니다. 추천인은 회원가입 시에만 설정 가능합니다.", false, null, 0, null, null);
            }
            
            // 6. 추천인 관계 생성 (테이블이 존재하는 경우에만)
            try {
                SuggestionUser suggestionUser = new SuggestionUser();
                suggestionUser.setSuggestionUserIndex(user.getUserIndex()); // 추천받는 사용자
                suggestionUser.setRecommendationUserIndex(referrerUser.getUserIndex()); // 추천인 (올바른 설정)
                suggestionUser.setJoinDate(LocalDateTime.now());
                
                suggestionUserRepository.save(suggestionUser);
                log.info("추천인 관계 저장 완료: 추천인={}, 추천받는사용자={}", referrerUser.getUserIndex(), user.getUserIndex());
            } catch (Exception e) {
                log.warn("SuggestionUser 테이블에 저장 실패, 포인트 지급만 진행: {}", e.getMessage());
            }
            
            // 7. 추천인에게 포인트 지급 (PHP 코드와 동일한 로직)
            log.info("포인트 지급 시작: 추천인={}, 새사용자={}", referrerUser.getUserIndex(), user.getUserIndex());
            giveReferralReward(referrerUser.getUserIndex(), user.getUserIndex());
            
            // 8. 추천인 수 조회
            long referralCount = suggestionUserRepository.countByRecommendationUserIndex(referrerUser.getUserIndex());
            log.info("추천인 수 조회: count={}", referralCount);
            
            return new ReferralResponseDTO(
                "추천인 관계가 성공적으로 생성되었습니다.",
                true,
                requestDTO.getReferralCode(),
                (int) referralCount,
                referrer.getName(),
                referrer.getNickname()
            );
            
        } catch (Exception e) {
            log.error("추천인 관계 생성 중 오류 발생: ", e);
            throw new RuntimeException("추천인 관계 생성 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    /**
     * 사용자의 추천인 목록 조회
     */
    public List<ReferralListDTO> getReferralList(Integer userIndex) {
        try {
            List<SuggestionUser> suggestionUsers = suggestionUserRepository.findByRecommendationUserIndex(userIndex);
            
            return suggestionUsers.stream()
                .map(su -> {
                    Optional<UserTesseris> userOpt = userTesserisRepository.findById(su.getSuggestionUserIndex());
                    if (userOpt.isPresent()) {
                        UserTesseris user = userOpt.get();
                        UserEntity userInfo = user.getUsersId();
                        return new ReferralListDTO(
                            user.getUserIndex(),
                            userInfo.getName(),
                            userInfo.getNickname(),
                            userInfo.getEmail(),
                            su.getJoinDate(),
                            userInfo.getReferralCode()
                        );
                    }
                    return null;
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("추천인 목록 조회 중 오류 발생: ", e);
            return List.of();
        }
    }
    
    /**
     * 추천인 코드 생성 (사용자 ID 기반)
     */
    public String generateReferralCode(String userId) {
        // 간단한 해시 기반 추천인 코드 생성
        return "REF" + userId.substring(0, Math.min(8, userId.length())).toUpperCase();
    }
    
    /**
     * 사용자의 추천인 수 조회
     */
    public int getReferralCount(Integer userIndex) {
        try {
            return (int) suggestionUserRepository.countByRecommendationUserIndex(userIndex);
        } catch (Exception e) {
            log.error("추천인 수 조회 중 오류 발생: ", e);
            return 0;
        }
    }
    
    /**
     * 추천인에게 포인트 지급 (PHP 코드와 동일한 로직)
     */
    @Transactional
    public void giveReferralReward(Integer referrerUserIndex, Integer newUserIndex) {
        try {
            int rewardAmount = 10000; // 10,000cm로 변경
            
            // 1. 추천인에게 포인트 지급
            Optional<UserCm> referrerUserCmOpt = userCmRepository.findById(referrerUserIndex);
            if (referrerUserCmOpt.isPresent()) {
                UserCm referrerUserCm = referrerUserCmOpt.get();
                referrerUserCm.setUserCmDeposit(referrerUserCm.getUserCmDeposit() + rewardAmount);
                userCmRepository.save(referrerUserCm);
                
                // 추천인 포인트 지급 로그 생성
                createReferralRewardLog(referrerUserIndex, rewardAmount, "추천인 보상 지급");
                
                log.info("추천인 포인트 지급 완료: 사용자={}, 지급금액={}", referrerUserIndex, rewardAmount);
            } else {
                log.warn("추천인 UserCm 정보를 찾을 수 없습니다: {}", referrerUserIndex);
            }
            
            // 2. 새로 가입한 사용자에게 포인트 지급
            Optional<UserCm> newUserCmOpt = userCmRepository.findById(newUserIndex);
            if (newUserCmOpt.isPresent()) {
                UserCm newUserCm = newUserCmOpt.get();
                newUserCm.setUserCmDeposit(newUserCm.getUserCmDeposit() + rewardAmount);
                userCmRepository.save(newUserCm);
                
                // 새 사용자 포인트 지급 로그 생성
                createReferralRewardLog(newUserIndex, rewardAmount, "추천인 가입 보상 지급");
                
                log.info("새 사용자 포인트 지급 완료: 사용자={}, 지급금액={}", newUserIndex, rewardAmount);
            } else {
                log.warn("새 사용자 UserCm 정보를 찾을 수 없습니다: {}", newUserIndex);
            }
            
        } catch (Exception e) {
            log.error("추천인 포인트 지급 중 오류 발생: ", e);
            throw new RuntimeException("추천인 포인트 지급 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
    
    /**
     * 추천인 포인트 지급 로그 생성
     */
    private void createReferralRewardLog(Integer userIndex, int amount, String reason) {
        try {
            // 1. 로그 타입 조회
            Optional<UserCmLogTransactionType> transactionTypeOpt = userCmLogTransactionTypeRepository.findByTransactionTypeName("REFERRAL_REWARD");
            if (transactionTypeOpt.isEmpty()) {
                log.warn("REFERRAL_REWARD 트랜잭션 타입을 찾을 수 없습니다.");
                return;
            }
            
            Optional<UserCmLogValueType> valueTypeOpt = userCmLogValueTypeRepository.findByValueTypeName("POINT");
            if (valueTypeOpt.isEmpty()) {
                log.warn("POINT 값 타입을 찾을 수 없습니다.");
                return;
            }
            
            Optional<UserCmLogPayment> paymentOpt = userCmLogPaymentRepository.findByPaymentName("SYSTEM");
            if (paymentOpt.isEmpty()) {
                log.warn("SYSTEM 결제 타입을 찾을 수 없습니다.");
                return;
            }
            
            // 2. 로그 생성
            UserCmLog log = new UserCmLog();
            log.setUserCmLogIndex(userIndex);
            log.setUserCmLogTransactionTypeIndex(transactionTypeOpt.get().getUserCmLogTransactionTypeIndex());
            log.setUserCmLogValueTypeIndex(valueTypeOpt.get().getUserCmLogValueTypeIndex());
            log.setUserCmLogPaymentIndex(paymentOpt.get().getUserCmLogPaymentIndex());
            log.setUserCmLogValue(amount);
            log.setUserCmLogCreateTime(LocalDateTime.now());
            log.setUserCmLogReason(reason);
            
            userCmLogRepository.save(log);
            
        } catch (Exception e) {
            log.error("추천인 포인트 지급 로그 생성 중 오류 발생: ", e);
        }
    }
} 