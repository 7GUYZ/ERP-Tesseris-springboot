package com.jakdang.labs.api.deokkyu.withdrawal.service;

import com.jakdang.labs.entity.UserCmLog;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.entity.UserBank;
import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.api.auth.repository.UserRepository;
import com.jakdang.labs.api.deokkyu.store.repository.UserTesserishdkRepo;
import com.jakdang.labs.api.deokkyu.withdrawal.dto.WithdrawalDetailsRequestDto;
import com.jakdang.labs.api.deokkyu.withdrawal.dto.WithdrawalDetailsResponseDto;
import com.jakdang.labs.api.deokkyu.withdrawal.repository.UserBankdkRepo;
import com.jakdang.labs.api.deokkyu.withdrawal.repository.WithdrawaldkRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WithdrawalService {
    
    private final WithdrawaldkRepo withdrawalRepository;
    private final UserRepository userRepository;
    private final UserTesserishdkRepo userTesserisRepository;
    private final UserBankdkRepo userBankRepository;
    
    /**
     * 출금 상세 조회
     * @param requestDto 시작일, 종료일
     * @return 출금 상세 목록
     */
    public List<WithdrawalDetailsResponseDto> getWithdrawalDetails(WithdrawalDetailsRequestDto requestDto) {
        log.info("출금 상세 조회 시작: {} ~ {}", requestDto.getStartDate(), requestDto.getEndDate());
        
        // 날짜 파싱
        LocalDate startDate = LocalDate.parse(requestDto.getStartDate());
        LocalDate endDate = LocalDate.parse(requestDto.getEndDate());
        
        // 시작일 00:00:00, 종료일 23:59:59로 설정
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        
        // 출금 거래 조회 (user_cm_log_payment_index = 2)
        List<UserCmLog> withdrawalLogs = withdrawalRepository.findWithdrawalsByDateRange(startDateTime, endDateTime);
        
        log.info("조회된 출금 거래 수: {}", withdrawalLogs.size());
        
        // DTO로 변환
        List<WithdrawalDetailsResponseDto> result = withdrawalLogs.stream()
            .map(this::convertToResponseDto)
            .filter(dto -> dto.getUserId() != null && !dto.getUserId().isEmpty() && 
                          dto.getUserName() != null && !dto.getUserName().isEmpty()) // 더 엄격한 필터링
            .collect(Collectors.toList());
        
        log.info("출금 상세 조회 완료: {}건", result.size());
        return result;
    }
    
    /**
     * UserCmLog를 WithdrawalDetailsResponseDto로 변환
     */
    private WithdrawalDetailsResponseDto convertToResponseDto(UserCmLog userCmLog) {
        try {
            // user_index_event_party로 UserTesseris 조회
            UserTesseris userTesseris = userCmLog.getUserIndexEventParty();
            if (userTesseris == null) {
                log.warn("UserTesseris가 null입니다. userCmLogIndex: {}", userCmLog.getUserCmLogIndex());
                return createEmptyResponseDto();
            }
            
            // users_id로 UserEntity 조회
            UserEntity userEntity = userTesseris.getUsersId();
            if (userEntity == null) {
                log.warn("UserEntity가 null입니다. userIndex: {}", userTesseris.getUserIndex());
                return createEmptyResponseDto();
            }
            
            // user_bank_index로 UserBank 조회
            UserBank userBank = userTesseris.getUserBank();
            String userBankName = userBank != null ? userBank.getUserBankName() : "";
            
            WithdrawalDetailsResponseDto dto = WithdrawalDetailsResponseDto.builder()
                .userId(userEntity.getId() != null ? userEntity.getId() : "")
                .userName(userEntity.getName() != null ? userEntity.getName() : "")
                .userPhone(userEntity.getPhone() != null ? userEntity.getPhone() : "")
                .userBankName(userBankName)
                .userBankNumber(userTesseris.getUserBankNumber() != null ? userTesseris.getUserBankNumber() : "")
                .chargeAmount(userCmLog.getUserCmLogValue() != null ? userCmLog.getUserCmLogValue() : 0)
                .transactionName("출금")
                .chargeDate(userCmLog.getUserCmLogCreateTime() != null ? userCmLog.getUserCmLogCreateTime().toLocalDate() : LocalDate.now())
                .cmValue(userCmLog.getUserCmLogValue() != null ? userCmLog.getUserCmLogValue() : 0)
                .build();
            
            log.debug("DTO 변환 성공: userCmLogIndex={}, userId={}, userName={}", 
                     userCmLog.getUserCmLogIndex(), dto.getUserId(), dto.getUserName());
            
            return dto;
                
        } catch (Exception e) {
            log.error("DTO 변환 중 오류 발생. userCmLogIndex: {}, userIndexEventParty: {}, 오류: {}", 
                     userCmLog.getUserCmLogIndex(), 
                     userCmLog.getUserIndexEventParty() != null ? userCmLog.getUserIndexEventParty().getUserIndex() : "null",
                     e.getMessage());
            return createEmptyResponseDto();
        }
    }
    
    /**
     * 빈 응답 DTO 생성 (오류 시 사용)
     */
    private WithdrawalDetailsResponseDto createEmptyResponseDto() {
        return WithdrawalDetailsResponseDto.builder()
            .userId("")
            .userName("")
            .userPhone("")
            .userBankName("")
            .userBankNumber("")
            .chargeAmount(0)
            .transactionName("출금")
            .chargeDate(LocalDate.now())
            .cmValue(0)
            .build();
    }
} 