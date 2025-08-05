package com.jakdang.labs.api.taekjun.user_log.service;

import com.jakdang.labs.api.taekjun.user_log.dto.UserLogResponseDTO;
import com.jakdang.labs.api.taekjun.user_log.repository.UserLogJtjRepo;
import com.jakdang.labs.entity.UserCmLog;
import com.jakdang.labs.entity.UserTesseris;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 사용자 CM 사용 내역 서비스
 * 
 * 주요 기능:
 * 1. 사용자별 CM 사용 내역 조회
 * 2. 페이징 처리
 * 3. 월별 필터링
 * 4. 거래 타입별 필터링
 * 5. 통계 정보 제공
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserLogService {

    private final UserLogJtjRepo userLogRepository;

    /**
     * 사용자 CM 사용 내역 조회 (페이징)
     * 
     * @param userIndex 사용자 인덱스
     * @param page 페이지 번호
     * @param size 페이지당 데이터 개수
     * @return 페이징된 CM 사용 내역
     */
    public Map<String, Object> getUserLogs(Integer userIndex, int page, int size) {
        log.info("사용자 CM 사용 내역 조회 - userIndex: {}, page: {}, size: {}", userIndex, page, size);
        
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<UserCmLog> userCmLogPage = userLogRepository.findByUserIndex(userIndex, pageRequest);
        
        List<UserLogResponseDTO> userLogDtos = userCmLogPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", userLogDtos);
        response.put("totalElements", userCmLogPage.getTotalElements());
        response.put("totalPages", userCmLogPage.getTotalPages());
        response.put("currentPage", userCmLogPage.getNumber());
        response.put("size", userCmLogPage.getSize());
        response.put("hasNext", userCmLogPage.hasNext());
        response.put("hasPrevious", userCmLogPage.hasPrevious());
        
        log.info("사용자 CM 사용 내역 조회 완료 - userIndex: {}, 총 {}개", userIndex, userCmLogPage.getTotalElements());
        return response;
    }

    /**
     * 사용자별 CM 사용 내역 조회 (페이징 + 월별 필터)
     * 
     * @param userIndex 사용자 인덱스
     * @param year 년도
     * @param month 월
     * @param page 페이지 번호
     * @param size 페이지당 데이터 개수
     * @return CM 사용 내역 목록
     */
    public Map<String, Object> getUserLogsByMonth(Integer userIndex, int year, int month, int page, int size) {
        log.info("사용자별 CM 사용 내역 조회 (월별) - userIndex: {}, year: {}, month: {}, page: {}, size: {}", 
                userIndex, year, month, page, size);
        
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<UserCmLog> userCmLogPage = userLogRepository.findByUserIndexAndMonth(userIndex, year, month, pageRequest);
        
        List<UserLogResponseDTO> userLogDtos = userCmLogPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", userLogDtos);
        response.put("totalElements", userCmLogPage.getTotalElements());
        response.put("totalPages", userCmLogPage.getTotalPages());
        response.put("currentPage", userCmLogPage.getNumber());
        response.put("size", userCmLogPage.getSize());
        response.put("hasNext", userCmLogPage.hasNext());
        response.put("hasPrevious", userCmLogPage.hasPrevious());
        
        log.info("사용자별 CM 사용 내역 조회 (월별) 완료 - 총 {}개", userCmLogPage.getTotalElements());
        return response;
    }

    /**
     * 사용자별 CM 로그 조회 (페이징)
     */
    public Map<String, Object> getAllLogs(Integer userIndex, int page, int size, Integer year, Integer month) {
        log.info("전체 CM 로그 조회 - userIndex: {}, page: {}, size: {}, year: {}, month: {}", 
                userIndex, page, size, year, month);
        
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<UserCmLog> userCmLogPage;
        
        if (year != null && month != null) {
            userCmLogPage = userLogRepository.findByUserIndexAndMonth(userIndex, year, month, pageRequest);
        } else {
            userCmLogPage = userLogRepository.findByUserIndex(userIndex, pageRequest);
        }
        
        List<UserLogResponseDTO> userLogDtos = userCmLogPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", userLogDtos);
        response.put("totalElements", userCmLogPage.getTotalElements());
        response.put("totalPages", userCmLogPage.getTotalPages());
        response.put("currentPage", userCmLogPage.getNumber());
        response.put("size", userCmLogPage.getSize());
        response.put("hasNext", userCmLogPage.hasNext());
        response.put("hasPrevious", userCmLogPage.hasPrevious());
        
        log.info("전체 CM 로그 조회 완료 - 총 {}개", userCmLogPage.getTotalElements());
        return response;
    }

    /**
     * 사용자 월별 CM 사용 내역 조회
     * 
     * @param userIndex 사용자 인덱스
     * @param year 년도
     * @param month 월
     * @return 해당 월의 CM 사용 내역
     */
    public List<UserLogResponseDTO> getUserMonthlyLogs(Integer userIndex, int year, int month) {
        log.info("사용자 월별 CM 사용 내역 조회 - userIndex: {}, year: {}, month: {}", userIndex, year, month);
        
        List<UserCmLog> userCmLogs = userLogRepository.findByUserIndexAndMonth(userIndex, year, month);
        
        List<UserLogResponseDTO> result = userCmLogs.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        log.info("사용자 월별 CM 사용 내역 조회 완료 - userIndex: {}, {}개", userIndex, result.size());
        return result;
    }

    /**
     * 사용자 거래 타입별 CM 사용 내역 조회 (페이징)
     * 
     * @param userIndex 사용자 인덱스
     * @param transactionType 거래 타입
     * @param page 페이지 번호
     * @param size 페이지당 데이터 개수
     * @return 거래 타입별 CM 사용 내역
     */
    public Map<String, Object> getUserLogsByTransactionType(Integer userIndex, Integer transactionType, int page, int size) {
        log.info("사용자 거래 타입별 CM 사용 내역 조회 - userIndex: {}, transactionType: {}, page: {}, size: {}", 
                userIndex, transactionType, page, size);
        
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<UserCmLog> userCmLogPage = userLogRepository.findByUserIndexAndTransactionType(userIndex, transactionType, pageRequest);
        
        List<UserLogResponseDTO> userLogDtos = userCmLogPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", userLogDtos);
        response.put("totalElements", userCmLogPage.getTotalElements());
        response.put("totalPages", userCmLogPage.getTotalPages());
        response.put("currentPage", userCmLogPage.getNumber());
        response.put("size", userCmLogPage.getSize());
        response.put("hasNext", userCmLogPage.hasNext());
        response.put("hasPrevious", userCmLogPage.hasPrevious());
        
        log.info("사용자 거래 타입별 CM 사용 내역 조회 완료 - userIndex: {}, transactionType: {}, 총 {}개", 
                userIndex, transactionType, userCmLogPage.getTotalElements());
        return response;
    }

    /**
     * 내가 쓴 금액 조회
     */
    public Map<String, Object> getSpentLogs(Integer userIndex, int page, int size, Integer year, Integer month) {
        log.info("내가 쓴 금액 조회 - userIndex: {}, page: {}, size: {}, year: {}, month: {}", 
                userIndex, page, size, year, month);
        
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<UserCmLog> userCmLogPage;
        
        if (year != null && month != null) {
            userCmLogPage = userLogRepository.findSpentLogsByMonth(userIndex, year, month, pageRequest);
        } else {
            userCmLogPage = userLogRepository.findSpentLogs(userIndex, pageRequest);
        }
        
        List<UserLogResponseDTO> userLogDtos = userCmLogPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", userLogDtos);
        response.put("totalElements", userCmLogPage.getTotalElements());
        response.put("totalPages", userCmLogPage.getTotalPages());
        response.put("currentPage", userCmLogPage.getNumber());
        response.put("size", userCmLogPage.getSize());
        response.put("hasNext", userCmLogPage.hasNext());
        response.put("hasPrevious", userCmLogPage.hasPrevious());
        
        log.info("내가 쓴 금액 조회 완료 - 총 {}개", userCmLogPage.getTotalElements());
        return response;
    }

    /**
     * 내가 받은 금액 조회
     */
    public Map<String, Object> getReceivedLogs(Integer userIndex, int page, int size, Integer year, Integer month) {
        log.info("내가 받은 금액 조회 - userIndex: {}, page: {}, size: {}, year: {}, month: {}", 
                userIndex, page, size, year, month);
        
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<UserCmLog> userCmLogPage;
        
        if (year != null && month != null) {
            userCmLogPage = userLogRepository.findReceivedLogsByMonth(userIndex, year, month, pageRequest);
        } else {
            userCmLogPage = userLogRepository.findReceivedLogs(userIndex, pageRequest);
        }
        
        List<UserLogResponseDTO> userLogDtos = userCmLogPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", userLogDtos);
        response.put("totalElements", userCmLogPage.getTotalElements());
        response.put("totalPages", userCmLogPage.getTotalPages());
        response.put("currentPage", userCmLogPage.getNumber());
        response.put("size", userCmLogPage.getSize());
        response.put("hasNext", userCmLogPage.hasNext());
        response.put("hasPrevious", userCmLogPage.hasPrevious());
        
        log.info("내가 받은 금액 조회 완료 - 총 {}개", userCmLogPage.getTotalElements());
        return response;
    }

    /**
     * 사용자 CM 사용 통계 조회
     * 
     * @param userIndex 사용자 인덱스
     * @return CM 사용 통계 정보
     */
    public Map<String, Object> getUserLogStatistics(Integer userIndex) {
        log.info("사용자 CM 사용 통계 조회 - userIndex: {}", userIndex);
        
        Object[] statistics = userLogRepository.getUserLogStatistics(userIndex);
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalTransactions", statistics[0]);
        result.put("totalSpent", statistics[1]);
        result.put("totalReceived", statistics[2]);
        result.put("totalCouponUsed", statistics[3]);
        
        log.info("사용자 CM 사용 통계 조회 완료 - userIndex: {}, 총 거래: {}, 총 지출: {}, 총 수입: {}, 총 쿠폰 사용: {}", 
                userIndex, statistics[0], statistics[1], statistics[2], statistics[3]);
        return result;
    }

    /**
     * UserCmLog 엔티티를 UserLogResponseDTO로 변환
     * 
     * @param userCmLog UserCmLog 엔티티
     * @return UserLogResponseDTO
     */
    private UserLogResponseDTO convertToDto(UserCmLog userCmLog) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        // 안전한 UserTesseris 처리
        UserTesseris triggerUser = userCmLog.getUserIndexEventTrigger();
        UserTesseris partyUser = userCmLog.getUserIndexEventParty();
        
        // triggerUser가 null이 아니고 userIndex가 0이 아닌 경우에만 처리
        Integer triggerUserIndex = null;
        String triggerUserEmail = null;
        String triggerUserName = null;
        
        if (triggerUser != null && triggerUser.getUserIndex() != null && triggerUser.getUserIndex() != 0) {
            try {
                triggerUserIndex = triggerUser.getUserIndex();
                if (triggerUser.getUsersId() != null) {
                    triggerUserEmail = triggerUser.getUsersId().getEmail();
                    triggerUserName = triggerUser.getUsersId().getName();
                }
            } catch (Exception e) {
                log.warn("triggerUser 처리 중 오류 발생: {}", e.getMessage());
            }
        }
        
        // partyUser가 null이 아니고 userIndex가 0이 아닌 경우에만 처리
        Integer partyUserIndex = null;
        String partyUserEmail = null;
        String partyUserName = null;
        
        if (partyUser != null && partyUser.getUserIndex() != null && partyUser.getUserIndex() != 0) {
            try {
                partyUserIndex = partyUser.getUserIndex();
                if (partyUser.getUsersId() != null) {
                    partyUserEmail = partyUser.getUsersId().getEmail();
                    partyUserName = partyUser.getUsersId().getName();
                }
            } catch (Exception e) {
                log.warn("partyUser 처리 중 오류 발생: {}", e.getMessage());
            }
        }
        
        return UserLogResponseDTO.builder()
                .userCmLogIndex(userCmLog.getUserCmLogIndex())
                .userCmLogValue(userCmLog.getUserCmLogValue())
                .userCmLogReason(userCmLog.getUserCmLogReason())
                .userCmLogCreateTime(userCmLog.getUserCmLogCreateTime())
                .userCouponValue(userCmLog.getUserCouponValue())
                .userCmLogTransactionTypeIndex(userCmLog.getUserCmLogTransactionTypeIndex())
                .transactionTypeName(getTransactionTypeName(userCmLog.getUserCmLogTransactionTypeIndex()))
                .userCmLogPaymentIndex(userCmLog.getUserCmLogPaymentIndex())
                .paymentTypeName(getPaymentTypeName(userCmLog.getUserCmLogPaymentIndex()))
                .userCmLogValueTypeIndex(userCmLog.getUserCmLogValueTypeIndex())
                .valueTypeName(getValueTypeName(userCmLog.getUserCmLogValueTypeIndex()))
                .userIndexEventTrigger(triggerUserIndex)
                .triggerUserEmail(triggerUserEmail)
                .triggerUserName(triggerUserName)
                .userIndexEventParty(partyUserIndex)
                .partyUserEmail(partyUserEmail)
                .partyUserName(partyUserName)
                .partyUserRole(null) // UserRole 정보는 별도 조회 필요
                .userCmLogTransactionCancel(userCmLog.getUserCmLogTransactionCancel())
                .formattedAmount(formatAmount(userCmLog.getUserCmLogValue()))
                .formattedCreateTime(userCmLog.getUserCmLogCreateTime() != null ? userCmLog.getUserCmLogCreateTime().format(formatter) : null)
                .build();
    }

    /**
     * 거래 타입 이름 반환
     */
    private String getTransactionTypeName(Integer transactionTypeIndex) {
        if (transactionTypeIndex == null) return null;
        
        return switch (transactionTypeIndex) {
            case 1 -> "중개수수료";
            case 8 -> "판매";
            case 9 -> "구매";
            case 14 -> "쿠폰";
            case 15 -> "쿠폰발행취소";
            default -> "기타";
        };
    }

    /**
     * 결제 타입 이름 반환
     */
    private String getPaymentTypeName(Integer paymentIndex) {
        if (paymentIndex == null) return null;
        
        return switch (paymentIndex) {
            case 1 -> "입금";
            case 2 -> "출금";
            default -> "기타";
        };
    }

    /**
     * 값 타입 이름 반환
     */
    private String getValueTypeName(Integer valueTypeIndex) {
        if (valueTypeIndex == null) return null;
        
        return switch (valueTypeIndex) {
            case 1 -> "CMP";
            case 2 -> "CM";
            case 3 -> "Cash";
            default -> "기타";
        };
    }

    /**
     * 금액 포맷팅
     */
    private String formatAmount(Integer amount) {
        if (amount == null) return "0";
        
        String sign = amount < 0 ? "-" : "";
        return sign + String.valueOf(Math.abs(amount)) + " CM";
    }
} 