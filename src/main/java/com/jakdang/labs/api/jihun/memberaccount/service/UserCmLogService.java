package com.jakdang.labs.api.jihun.memberaccount.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.jakdang.labs.api.jihun.memberaccount.dto.LookupDataDto;
import com.jakdang.labs.api.jihun.memberaccount.dto.UserCmLogResponseDto;
import com.jakdang.labs.api.jihun.memberaccount.dto.UserCmLogSearchRequestDto;
import com.jakdang.labs.entity.*;
import com.jakdang.labs.api.jihun.memberaccount.repository.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * UserCmLog 관련 비즈니스 로직을 담당하는 서비스 클래스
 * 
 * 주요 기능:
 * 1. 데이터 변환 (Entity → DTO)
 * 2. 페이징 처리 로직
 * 3. 무한 스크롤 지원
 * 4. 사용자별 필터링
 * 5. 안전한 null 처리
 * 6. 동적 검색 기능
 * 7. 룩업 데이터 조회
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용 트랜잭션으로 성능 최적화
@Slf4j
public class UserCmLogService {

    private final AjhUserCmLogRepository userCmLogRepository;
    private final AjhUserRoleRepository userRoleRepository;
    private final AjhUserCmLogValueTypeRepository userCmLogValueTypeRepository;
    private final AjhUserCmLogPaymentRepository userCmLogPaymentRepository;
    private final AjhUserCmLogTransactionTypeRepository userCmLogTransactionTypeRepository;

    /**
     * 모든 UserCmLog 조회 (기본 메서드)
     * 
     * 목적: 전체 데이터 조회 (테스트, 관리자용)
     * 
     * 특징:
     * - @Transactional(readOnly = true): 읽기 전용으로 성능 최적화
     * - Repository에서 JOIN FETCH로 N+1 문제 해결
     * - Entity를 DTO로 변환하여 반환
     * - ⚠️ 대용량 데이터 시 메모리 부족 위험
     * 
     * @return 모든 UserCmLog의 DTO 리스트
     */
    public List<UserCmLogResponseDto> getAllUserCmLogs() {
        log.info("모든 UserCmLog 조회 시작");
        
        // Repository에서 JOIN FETCH로 연관 데이터까지 한 번에 조회
        List<UserCmLog> userCmLogs = userCmLogRepository.findTop100WithJoins(
            PageRequest.of(0, 25) // 첫 페이지, 25개씩
        );
        
        // Entity를 DTO로 변환하여 반환
        List<UserCmLogResponseDto> result = userCmLogs.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        
        log.info("UserCmLog 조회 완료: {}개", result.size());
        return result;
    }

    /**
     * 페이징 처리된 모든 UserCmLog 조회 (무한 스크롤용)
     * 
     * 목적: 무한 스크롤 구현을 위한 메인 메서드
     * 
     * 특징:
     * - Page 객체 반환: 총 개수, 페이지 정보 등 메타데이터 포함
     * - 메모리 효율적: 한 번에 제한된 개수만 처리
     * - 프론트엔드에서 "더 보기" 버튼 클릭 시 호출
     * - 성능 최적화: 필요한 데이터만 조회
     * 
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지당 데이터 개수
     * @return 페이징 정보와 데이터를 포함한 Map
     */
    public Map<String, Object> getAllUserCmLogsPaged(int page, int size) {
        log.info("페이징 UserCmLog 조회 시작 - page: {}, size: {}", page, size);
        
        // 페이징 정보 생성
        Pageable pageable = PageRequest.of(page, size);
        
        try {
            // Repository에서 페이징 처리된 데이터 조회
            Page<UserCmLog> userCmLogPage = userCmLogRepository.findAllWithJoinsPaged(pageable);
            
            // Entity를 DTO로 변환
            List<UserCmLogResponseDto> userCmLogDtos = userCmLogPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
            
            // 응답 데이터 구성
            Map<String, Object> response = new HashMap<>();
            response.put("content", userCmLogDtos);
            response.put("totalElements", userCmLogPage.getTotalElements());
            response.put("totalPages", userCmLogPage.getTotalPages());
            response.put("currentPage", userCmLogPage.getNumber());
            response.put("size", userCmLogPage.getSize());
            response.put("hasNext", userCmLogPage.hasNext());
            response.put("hasPrevious", userCmLogPage.hasPrevious());
            
            log.info("페이징 UserCmLog 조회 완료 - 총 {}개, 현재 페이지: {}, 조회된 데이터: {}개", 
                    userCmLogPage.getTotalElements(), page, userCmLogDtos.size());
            return response;
            
        } catch (Exception e) {
            log.error("페이징 UserCmLog 조회 중 오류 발생", e);
            throw e;
        }
    }

    /**
     * 특정 사용자의 페이징 처리된 UserCmLog 조회
     * 
     * 목적: 사용자별 무한 스크롤 구현
     * 
     * 특징:
     * - 사용자 필터링 + 페이징 조합
     * - eventTriggerUser OR eventPartyUser 조건으로 사용자 관련 모든 로그 조회
     * - 무한 스크롤에서 사용자별 데이터 로드
     * 
     * @param userId 조회할 사용자 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지당 데이터 개수
     * @return 페이징 정보와 데이터를 포함한 Map
     */
    public Map<String, Object> getUserCmLogsByUserIdPaged(Long userId, int page, int size) {
        log.info("사용자별 페이징 UserCmLog 조회 시작 - userId: {}, page: {}, size: {}", userId, page, size);
        
        // 페이징 정보 생성
        Pageable pageable = PageRequest.of(page, size);
        
        // Repository에서 사용자별 페이징 처리된 데이터 조회
        Page<UserCmLog> userCmLogPage = userCmLogRepository.findByUserIndexWithJoinsPaged(userId, pageable);
        
        // Entity를 DTO로 변환
        List<UserCmLogResponseDto> userCmLogDtos = userCmLogPage.getContent().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        
        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("content", userCmLogDtos);
        response.put("totalElements", userCmLogPage.getTotalElements());
        response.put("totalPages", userCmLogPage.getTotalPages());
        response.put("currentPage", userCmLogPage.getNumber());
        response.put("size", userCmLogPage.getSize());
        response.put("hasNext", userCmLogPage.hasNext());
        response.put("hasPrevious", userCmLogPage.hasPrevious());
        
        log.info("사용자별 페이징 UserCmLog 조회 완료 - userId: {}, 총 {}개", 
                userId, userCmLogPage.getTotalElements());
        return response;
    }

    /**
     * 모든 UserCmLog 조회 (완전한 데이터)
     * 
     * ⚠️ 주의: 대용량 데이터가 있을 경우 메모리 부족 위험
     * 
     * 목적: 모든 데이터를 한 번에 조회 (데이터 분석, 관리자용)
     * 
     * 특징:
     * - List 반환: 모든 데이터를 메모리에 로드
     * - 성능 이슈 가능성: 데이터가 많을 경우
     * - JOIN FETCH로 N+1 문제는 해결
     * 
     * @return 모든 UserCmLog의 DTO 리스트
     */
    public List<UserCmLogResponseDto> getAllUserCmLogsComplete() {
        log.info("전체 UserCmLog 조회 시작 (완전한 데이터)");
        
        // Repository에서 모든 데이터 조회 (JOIN FETCH 포함)
        List<UserCmLog> userCmLogs = userCmLogRepository.findAllWithJoins();
        
        // Entity를 DTO로 변환
        List<UserCmLogResponseDto> result = userCmLogs.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        
        log.info("전체 UserCmLog 조회 완료: {}개", result.size());
        return result;
    }

    /**
     * 특정 ID의 UserCmLog 조회
     * 
     * 목적: 개별 로그의 상세 정보 조회
     * 
     * 특징:
     * - 단일 결과 조회
     * - JOIN FETCH로 연관 데이터도 함께 조회
     * - null 안전 처리
     * 
     * @param id 조회할 UserCmLog의 ID
     * @return UserCmLogResponseDto 또는 null
     */
    public UserCmLogResponseDto getUserCmLogById(Long id) {
        log.info("특정 ID UserCmLog 조회 시작 - id: {}", id);
        
        // Repository에서 특정 ID 조회 (JOIN FETCH 포함)
        UserCmLog userCmLog = userCmLogRepository.findByIdWithJoins(id);
        
        if (userCmLog == null) {
            log.warn("ID {}에 해당하는 UserCmLog를 찾을 수 없습니다.", id);
            return null;
        }
        
        UserCmLogResponseDto result = convertToDto(userCmLog);
        log.info("특정 ID UserCmLog 조회 완료 - id: {}", id);
        return result;
    }

    /**
     * 특정 사용자의 UserCmLog 조회 (상위 100개)
     * 
     * 목적: 사용자별 로그 조회 (초기 로드용)
     * 
     * 특징:
     * - 사용자 필터링 적용
     * - 상위 100개로 제한하여 성능 보장
     * - eventTriggerUser OR eventPartyUser 조건
     * 
     * @param userId 조회할 사용자 ID
     * @return 사용자별 UserCmLog DTO 리스트
     */
    public List<UserCmLogResponseDto> getUserCmLogsByUserId(Long userId) {
        log.info("사용자별 UserCmLog 조회 시작 (상위 100개) - userId: {}", userId);
        
        // Repository에서 사용자별 상위 100개 조회
        List<UserCmLog> userCmLogs = userCmLogRepository.findTop100ByUserIndexWithJoins(
            userId, PageRequest.of(0, 100)
        );
        
        // Entity를 DTO로 변환
        List<UserCmLogResponseDto> result = userCmLogs.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        
        log.info("사용자별 UserCmLog 조회 완료 - userId: {}, {}개", userId, result.size());
        return result;
    }

    /**
     * 특정 사용자의 모든 UserCmLog 조회
     * 
     * ⚠️ 주의: 사용자별 데이터가 많을 경우 메모리 부족 위험
     * 
     * 목적: 사용자의 모든 로그 조회 (데이터 분석용)
     * 
     * 특징:
     * - 사용자 필터링 적용
     * - 페이징 없이 모든 데이터 조회
     * - 성능 이슈 가능성
     * 
     * @param userId 조회할 사용자 ID
     * @return 사용자별 모든 UserCmLog DTO 리스트
     */
    public List<UserCmLogResponseDto> getUserCmLogsByUserIdComplete(Long userId) {
        log.info("사용자별 전체 UserCmLog 조회 시작 - userId: {}", userId);
        
        // Repository에서 사용자별 모든 데이터 조회
        List<UserCmLog> userCmLogs = userCmLogRepository.findAllByUserIndexWithJoins(userId);
        
        // Entity를 DTO로 변환
        List<UserCmLogResponseDto> result = userCmLogs.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        
        log.info("사용자별 전체 UserCmLog 조회 완료 - userId: {}, {}개", userId, result.size());
        return result;
    }

    /**
     * 🆕 동적 검색 기능 (PHP 검색 기능 재현)
     * 
     * 목적: 복합 조건을 이용한 동적 검색 기능
     * 
     * 특징:
     * - 다중 조건 검색 지원
     * - 빈 값/null 처리
     * - 페이징 지원
     * - LIKE 검색 지원
     * 
     * @param searchRequest 검색 조건 DTO
     * @return 검색 결과와 페이징 정보
     */
    public Map<String, Object> searchUserCmLogs(UserCmLogSearchRequestDto searchRequest) {
        log.info("동적 검색 시작 - 조건: {}", searchRequest);
        
        // 페이징 정보 생성 (요청 파라미터 우선, 기본값 25)
        int page = searchRequest.getPage();
        int size = searchRequest.getSize() > 0 ? searchRequest.getSize() : 25; // 요청된 크기 또는 기본값 25
        
        // 페이지 번호 안전 처리
        if (page < 0) page = 0;
        
        Pageable pageable = PageRequest.of(page, size);
        
        // LIKE 검색을 위한 파라미터 처리
        String triggerUserEmail = processLikeParameter(searchRequest.getEventTriggerUserEmail());
        String partyUserEmail = processLikeParameter(searchRequest.getEventPartyUserEmail());
        String partyUserName = processLikeParameter(searchRequest.getEventPartyUserName());
        
        // 기존 ID 방식 검색도 지원 (하위 호환성)
        if (triggerUserEmail == null && searchRequest.getUserIndexEventTrigger() != null) {
            triggerUserEmail = processLikeParameter(searchRequest.getUserIndexEventTrigger());
        }
        
        if (partyUserEmail == null && searchRequest.getUserIndexEventParty() != null) {
            partyUserEmail = processLikeParameter(searchRequest.getUserIndexEventParty());
        }
        
        // 날짜 파라미터를 LocalDateTime으로 변환
        LocalDateTime startDateTime = parseDateTime(searchRequest.getUserCmLogCreateTimeStart());
        LocalDateTime endDateTime = parseDateTime(searchRequest.getUserCmLogCreateTimeEnd());
        
        log.info("처리된 검색 파라미터 - triggerEmail: {}, partyEmail: {}, partyName: {}, startDate: {}, endDate: {}", 
                triggerUserEmail, partyUserEmail, partyUserName, startDateTime, endDateTime);
        
        // Repository에서 동적 검색 실행 (페이징 지원)
        Page<UserCmLog> userCmLogPage = userCmLogRepository.findBySearchCriteriaWithLike(
            triggerUserEmail,
            partyUserEmail,
            partyUserName,
            searchRequest.getUserRoleIndex(),
            searchRequest.getUserRoleIndex2(),
            searchRequest.getUserCmLogValueTypeIndex(),
            startDateTime,
            endDateTime,
            searchRequest.getUserCmLogPaymentIndex(),
            searchRequest.getUserCmLogTransactionTypeIndex(),
            pageable
        );
        
        // Entity를 DTO로 변환
        List<UserCmLogResponseDto> userCmLogDtos = userCmLogPage.getContent().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        
        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("content", userCmLogDtos);
        response.put("totalElements", userCmLogPage.getTotalElements());
        response.put("totalPages", userCmLogPage.getTotalPages());
        response.put("currentPage", userCmLogPage.getNumber());
        response.put("size", userCmLogPage.getSize());
        response.put("hasNext", userCmLogPage.hasNext());
        response.put("hasPrevious", userCmLogPage.hasPrevious());
        
        log.info("동적 검색 완료 - 총 {}개 결과", userCmLogPage.getTotalElements());
        return response;
    }

    /**
     * 🆕 사용자 역할 목록 조회
     * 
     * @return 사용자 역할 목록
     */
    public List<LookupDataDto> getUserRoles() {
        log.info("사용자 역할 목록 조회 시작");
        
        List<UserRole> userRoles = userRoleRepository.findAll();
        
        List<LookupDataDto> result = userRoles.stream()
            .map(role -> LookupDataDto.builder()
                .index(role.getUserRoleIndex().longValue())
                .name(role.getUserRoleKorNm())
                .build())
            .collect(Collectors.toList());
        
        log.info("사용자 역할 목록 조회 완료: {}개", result.size());
        return result;
    }

    /**
     * 🆕 가치 유형 목록 조회 (전체값으로 고정)
     * 
     * 목적: 가치 유형을 항상 전체값으로만 보여주되, 프론트엔드에서 어떤 값을 보내는지에 따라 동적 검색이 가능하도록 함
     * 
     * 특징:
     * - 모든 가치 유형 데이터를 반환 (CM, CMP, Cash 등)
     * - 프론트엔드에서 선택하지 않아도 전체 검색 가능
     * - 확장성을 고려한 설계
     * 
     * @return 가치 유형 목록 (전체)
     */
    public List<LookupDataDto> getValueTypes() {
        log.info("가치 유형 목록 조회 시작 (전체값)");
        
        List<UserCmLogValueType> valueTypes = userCmLogValueTypeRepository.findAll();
        
        List<LookupDataDto> result = valueTypes.stream()
            .map(type -> LookupDataDto.builder()
                .index(type.getUserCmLogValueTypeIndex().longValue())
                .name(type.getUserCmLogValueTypeName())
                .build())
            .collect(Collectors.toList());
        
        log.info("가치 유형 목록 조회 완료 (전체값): {}개", result.size());
        return result;
    }



    /**
     * 🆕 결제 수단 목록 조회
     * 
     * @return 결제 수단 목록
     */
    public List<LookupDataDto> getPaymentTypes() {
        log.info("결제 수단 목록 조회 시작");
        
        List<UserCmLogPayment> payments = userCmLogPaymentRepository.findAll();
        
        List<LookupDataDto> result = payments.stream()
            .map(payment -> LookupDataDto.builder()
                .index(payment.getUserCmLogPaymentIndex().longValue())
                .name(payment.getUserCmLogPaymentName())
                .build())
            .collect(Collectors.toList());
        
        log.info("결제 수단 목록 조회 완료: {}개", result.size());
        return result;
    }

    /**
     * 🆕 거래 유형 목록 조회
     * 
     * @return 거래 유형 목록
     */
    public List<LookupDataDto> getTransactionTypes() {
        log.info("거래 유형 목록 조회 시작");
        
        List<UserCmLogTransactionType> transactionTypes = userCmLogTransactionTypeRepository.findAll();
        
        List<LookupDataDto> result = transactionTypes.stream()
            .map(type -> LookupDataDto.builder()
                .index(type.getUserCmLogTransactionTypeIndex().longValue())
                .name(type.getUserCmLogTransactionTypeName())
                .build())
            .collect(Collectors.toList());
        
        log.info("거래 유형 목록 조회 완료: {}개", result.size());
        return result;
    }

    /**
     * UserCmLog Entity를 UserCmLogResponseDto로 변환
     * 
     * 목적: Entity를 프론트엔드에서 사용할 수 있는 DTO로 변환
     * 
     * 특징:
     * - null 안전 처리: 연관 엔티티가 null일 경우 기본값 설정
     * - 중첩된 연관 관계 처리: User → UserRole 등
     * - BigDecimal 타입 처리: 금액 관련 데이터
     * - LocalDateTime을 String으로 변환: JSON 직렬화 문제 해결
     * 
     * @param userCmLog 변환할 UserCmLog Entity
     * @return 변환된 UserCmLogResponseDto
     */
    private UserCmLogResponseDto convertToDto(UserCmLog userCmLog) {
        return UserCmLogResponseDto.builder()
            // 기본 UserCmLog 정보
            .userCmLogIndex(userCmLog.getUserCmLogIndex().longValue())
            .userCmLogValue(userCmLog.getUserCmLogValue() != null ? 
                BigDecimal.valueOf(userCmLog.getUserCmLogValue()) : null)
            .userCouponValue(userCmLog.getUserCouponValue() != null ? 
                BigDecimal.valueOf(userCmLog.getUserCouponValue()) : null)
            .userCmLogReason(userCmLog.getUserCmLogReason())
            .userCmLogCreateTime(userCmLog.getUserCmLogCreateTime() != null ? 
                userCmLog.getUserCmLogCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
            
            // Event Trigger User 정보 (null 안전 처리)
            .eventTriggerUserIndex(getEventTriggerUserIndex(userCmLog))
            .eventTriggerUserEmail(getEventTriggerUserEmail(userCmLog))
            .eventTriggerUserName(getEventTriggerUserName(userCmLog))
            .eventTriggerUserRole(getEventTriggerUserRole(userCmLog))
            
            // Event Party User 정보 (null 안전 처리)
            .eventPartyUserIndex(getEventPartyUserIndex(userCmLog))
            .eventPartyUserEmail(getEventPartyUserEmail(userCmLog))
            .eventPartyUserName(getEventPartyUserName(userCmLog))
            .eventPartyUserRole(getEventPartyUserRole(userCmLog))
            
            // Value Type 정보 (null 안전 처리)
            .valueTypeIndex(userCmLog.getUserCmLogValueTypeIndex() != null ? 
                userCmLog.getUserCmLogValueTypeIndex().longValue() : null)
            .valueTypeName(getValueTypeName(userCmLog.getUserCmLogValueTypeIndex()))
            
            // Payment 정보 (null 안전 처리)
            .paymentIndex(userCmLog.getUserCmLogPaymentIndex() != null ? 
                userCmLog.getUserCmLogPaymentIndex().longValue() : null)
            .paymentName(getPaymentName(userCmLog.getUserCmLogPaymentIndex()))
            
            // Transaction Type 정보 (null 안전 처리)
            .transactionTypeIndex(userCmLog.getUserCmLogTransactionTypeIndex() != null ? 
                userCmLog.getUserCmLogTransactionTypeIndex().longValue() : null)
            .transactionTypeName(getTransactionTypeName(userCmLog.getUserCmLogTransactionTypeIndex()))
            .build();
    }

    /**
     * Event Trigger User의 인덱스 정보 조회
     * 
     * 목적: Event Trigger User의 인덱스를 안전하게 조회
     * 
     * 특징:
     * - null 안전 처리: UserTesseris가 null일 경우 null 반환
     * - 지연 로딩 오류 방지: try-catch로 EntityNotFoundException 처리
     * 
     * @param userCmLog UserCmLog Entity
     * @return 사용자 인덱스 또는 null
     */
    private Long getEventTriggerUserIndex(UserCmLog userCmLog) {
        if (userCmLog.getUserIndexEventTrigger() != null) {
            try {
                Integer userIndex = userCmLog.getUserIndexEventTrigger().getUserIndex();
                return userIndex != null ? userIndex.longValue() : null;
            } catch (Exception e) {
                log.warn("Event Trigger User 인덱스 조회 중 오류 발생", e);
            }
        }
        return null;
    }

    /**
     * Event Party User의 인덱스 정보 조회
     * 
     * 목적: Event Party User의 인덱스를 안전하게 조회
     * 
     * 특징:
     * - null 안전 처리: UserTesseris가 null일 경우 null 반환
     * - 지연 로딩 오류 방지: try-catch로 EntityNotFoundException 처리
     * 
     * @param userCmLog UserCmLog Entity
     * @return 사용자 인덱스 또는 null
     */
    private Long getEventPartyUserIndex(UserCmLog userCmLog) {
        if (userCmLog.getUserIndexEventParty() != null) {
            try {
                Integer userIndex = userCmLog.getUserIndexEventParty().getUserIndex();
                return userIndex != null ? userIndex.longValue() : null;
            } catch (Exception e) {
                log.warn("Event Party User 인덱스 조회 중 오류 발생", e);
            }
        }
        return null;
    }

    /**
     * Event Trigger User의 이메일 정보 조회
     * 
     * 목적: Event Trigger User의 이메일을 안전하게 조회
     * 
     * 특징:
     * - null 안전 처리: UserTesseris가 null이거나 Users가 null일 경우 null 반환
     * - 지연 로딩 오류 방지: try-catch로 EntityNotFoundException 처리
     * 
     * @param userCmLog UserCmLog Entity
     * @return 이메일 또는 null
     */
    private String getEventTriggerUserEmail(UserCmLog userCmLog) {
        if (userCmLog.getUserIndexEventTrigger() != null) {
            try {
                // getUserIndex() 호출도 안전하게 처리
                Integer userIndex = userCmLog.getUserIndexEventTrigger().getUserIndex();
                if (userCmLog.getUserIndexEventTrigger().getUsersId() != null) {
                    return userCmLog.getUserIndexEventTrigger().getUsersId().getEmail();
                }
            } catch (Exception e) {
                log.warn("Event Trigger User 이메일 조회 중 오류 발생", e);
            }
        }
        return null;
    }

    /**
     * Event Trigger User의 이름 정보 조회
     * 
     * 목적: Event Trigger User의 이름을 안전하게 조회
     * 
     * 특징:
     * - null 안전 처리: UserTesseris가 null이거나 Users가 null일 경우 null 반환
     * - 지연 로딩 오류 방지: try-catch로 EntityNotFoundException 처리
     * 
     * @param userCmLog UserCmLog Entity
     * @return 이름 또는 null
     */
    private String getEventTriggerUserName(UserCmLog userCmLog) {
        if (userCmLog.getUserIndexEventTrigger() != null) {
            try {
                // getUserIndex() 호출도 안전하게 처리
                Integer userIndex = userCmLog.getUserIndexEventTrigger().getUserIndex();
                if (userCmLog.getUserIndexEventTrigger().getUsersId() != null) {
                    return userCmLog.getUserIndexEventTrigger().getUsersId().getName();
                }
            } catch (Exception e) {
                log.warn("Event Trigger User 이름 조회 중 오류 발생", e);
            }
        }
        return null;
    }

    /**
     * Event Party User의 이메일 정보 조회
     * 
     * 목적: Event Party User의 이메일을 안전하게 조회
     * 
     * 특징:
     * - null 안전 처리: UserTesseris가 null이거나 Users가 null일 경우 null 반환
     * - 지연 로딩 오류 방지: try-catch로 EntityNotFoundException 처리
     * 
     * @param userCmLog UserCmLog Entity
     * @return 이메일 또는 null
     */
    private String getEventPartyUserEmail(UserCmLog userCmLog) {
        if (userCmLog.getUserIndexEventParty() != null) {
            try {
                // getUserIndex() 호출도 안전하게 처리
                Integer userIndex = userCmLog.getUserIndexEventParty().getUserIndex();
                if (userCmLog.getUserIndexEventParty().getUsersId() != null) {
                    return userCmLog.getUserIndexEventParty().getUsersId().getEmail();
                }
            } catch (Exception e) {
                log.warn("Event Party User 이메일 조회 중 오류 발생", e);
            }
        }
        return null;
    }

    /**
     * Event Party User의 이름 정보 조회
     * 
     * 목적: Event Party User의 이름을 안전하게 조회
     * 
     * 특징:
     * - null 안전 처리: UserTesseris가 null이거나 Users가 null일 경우 null 반환
     * - 지연 로딩 오류 방지: try-catch로 EntityNotFoundException 처리
     * 
     * @param userCmLog UserCmLog Entity
     * @return 이름 또는 null
     */
    private String getEventPartyUserName(UserCmLog userCmLog) {
        if (userCmLog.getUserIndexEventParty() != null) {
            try {
                // getUserIndex() 호출도 안전하게 처리
                Integer userIndex = userCmLog.getUserIndexEventParty().getUserIndex();
                if (userCmLog.getUserIndexEventParty().getUsersId() != null) {
                    return userCmLog.getUserIndexEventParty().getUsersId().getName();
                }
            } catch (Exception e) {
                log.warn("Event Party User 이름 조회 중 오류 발생", e);
            }
        }
        return null;
    }

    /**
     * Event Trigger User의 역할 정보 조회
     * 
     * 목적: Event Trigger User의 역할명을 안전하게 조회
     * 
     * 특징:
     * - null 안전 처리: UserRole이 null일 경우 기본값 반환
     * - 중첩된 연관 관계 처리: User → UserRole
     * - 지연 로딩 오류 방지: try-catch로 EntityNotFoundException 처리
     * 
     * @param userCmLog UserCmLog Entity
     * @return 역할명 또는 "알 수 없음"
     */
    private String getEventTriggerUserRole(UserCmLog userCmLog) {
        if (userCmLog.getUserIndexEventTrigger() != null) {
            try {
                Integer userRoleIndex = userCmLog.getUserIndexEventTrigger().getUserRoleIndex();
                if (userRoleIndex != null) {
                    return getUserRoleName(userRoleIndex);
                }
            } catch (Exception e) {
                log.warn("Event Trigger User 역할 조회 중 오류 발생", e);
            }
        }
        return "알 수 없음";
    }

    /**
     * Event Party User의 역할 정보 조회
     * 
     * 목적: Event Party User의 역할명을 안전하게 조회
     * 
     * 특징:
     * - null 안전 처리: UserRole이 null일 경우 기본값 반환
     * - 중첩된 연관 관계 처리: User → UserRole
     * - 지연 로딩 오류 방지: try-catch로 EntityNotFoundException 처리
     * 
     * @param userCmLog UserCmLog Entity
     * @return 역할명 또는 "알 수 없음"
     */
    private String getEventPartyUserRole(UserCmLog userCmLog) {
        if (userCmLog.getUserIndexEventParty() != null) {
            try {
                Integer userRoleIndex = userCmLog.getUserIndexEventParty().getUserRoleIndex();
                if (userRoleIndex != null) {
                    return getUserRoleName(userRoleIndex);
                }
            } catch (Exception e) {
                log.warn("Event Party User 역할 조회 중 오류 발생", e);
            }
        }
        return "알 수 없음";
    }

    /**
     * 사용자 역할명 조회
     * 
     * @param userRoleIndex 사용자 역할 인덱스
     * @return 역할명 또는 "알 수 없음"
     */
    private String getUserRoleName(Integer userRoleIndex) {
        if (userRoleIndex == null) {
            return "알 수 없음";
        }
        
        try {
            UserRole userRole = userRoleRepository.findById(userRoleIndex.longValue()).orElse(null);
            return userRole != null ? userRole.getUserRoleKorNm() : "알 수 없음";
        } catch (Exception e) {
            log.warn("사용자 역할 조회 중 오류 발생 - userRoleIndex: {}", userRoleIndex, e);
            return "알 수 없음";
        }
    }

    /**
     * 가치 유형명 조회
     * 
     * @param valueTypeIndex 가치 유형 인덱스
     * @return 가치 유형명 또는 "알 수 없음"
     */
    private String getValueTypeName(Integer valueTypeIndex) {
        if (valueTypeIndex == null) {
            return "알 수 없음";
        }
        
        try {
            UserCmLogValueType valueType = userCmLogValueTypeRepository.findById(valueTypeIndex.longValue()).orElse(null);
            return valueType != null ? valueType.getUserCmLogValueTypeName() : "알 수 없음";
        } catch (Exception e) {
            log.warn("가치 유형 조회 중 오류 발생 - valueTypeIndex: {}", valueTypeIndex, e);
            return "알 수 없음";
        }
    }

    /**
     * 결제 수단명 조회
     * 
     * @param paymentIndex 결제 수단 인덱스
     * @return 결제 수단명 또는 "알 수 없음"
     */
    private String getPaymentName(Integer paymentIndex) {
        if (paymentIndex == null) {
            return "알 수 없음";
        }
        
        try {
            UserCmLogPayment payment = userCmLogPaymentRepository.findById(paymentIndex.longValue()).orElse(null);
            return payment != null ? payment.getUserCmLogPaymentName() : "알 수 없음";
        } catch (Exception e) {
            log.warn("결제 수단 조회 중 오류 발생 - paymentIndex: {}", paymentIndex, e);
            return "알 수 없음";
        }
    }

    /**
     * 거래 유형명 조회
     * 
     * @param transactionTypeIndex 거래 유형 인덱스
     * @return 거래 유형명 또는 "알 수 없음"
     */
    private String getTransactionTypeName(Integer transactionTypeIndex) {
        if (transactionTypeIndex == null) {
            return "알 수 없음";
        }
        
        try {
            UserCmLogTransactionType transactionType = userCmLogTransactionTypeRepository.findById(transactionTypeIndex.longValue()).orElse(null);
            return transactionType != null ? transactionType.getUserCmLogTransactionTypeName() : "알 수 없음";
        } catch (Exception e) {
            log.warn("거래 유형 조회 중 오류 발생 - transactionTypeIndex: {}", transactionTypeIndex, e);
            return "알 수 없음";
        }
    }
    
    /**
     * 🆕 LIKE 검색을 위한 파라미터 처리
     * 
     * 목적: 프론트엔드에서 전달받은 검색어를 LIKE 검색에 적합하게 처리
     * 
     * 특징:
     * - null, 빈 문자열, 공백만 있는 경우 null 반환
     * - % 문자가 이미 포함된 경우 그대로 사용
     * - 일반 문자열인 경우 양쪽에 % 추가하지 않음 (Repository에서 처리)
     * - trim() 처리로 앞뒤 공백 제거
     * 
     * @param parameter 프론트엔드에서 전달받은 검색 파라미터
     * @return 처리된 검색 파라미터 또는 null
     */
    private String processLikeParameter(String parameter) {
        if (parameter == null || parameter.trim().isEmpty()) {
            return null;
        }
        
        String trimmed = parameter.trim();
        
        // 이미 % 문자가 포함된 경우 (프론트엔드에서 직접 LIKE 패턴 전달)
        if (trimmed.contains("%")) {
            return trimmed;
        }
        
        // 일반 검색어인 경우 그대로 반환 (Repository에서 LIKE 처리)
        return trimmed;
    }

    /**
     * 날짜 문자열을 LocalDateTime으로 변환
     * 
     * 목적: 프론트엔드에서 전달받은 날짜 문자열을 LocalDateTime으로 변환하여 사용
     * 
     * 특징:
     * - 빈 문자열이나 null일 경우 null 반환
     * - 형식이 맞지 않으면 예외 발생
     * - 형식: "yyyy-MM-dd HH:mm:ss"
     * 
     * @param dateString 변환할 날짜 문자열
     * @return 변환된 LocalDateTime 객체 또는 null
     */
    private LocalDateTime parseDateTime(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        
        try {
            return LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            log.warn("날짜 문자열 파싱 중 오류 발생 - dateString: {}, 예외: {}", dateString, e.getMessage());
            return null;
        }
    }


} 