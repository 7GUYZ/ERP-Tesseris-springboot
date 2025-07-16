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
import java.time.format.DateTimeFormatter;
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
            PageRequest.of(0, 100) // 첫 페이지, 100개씩
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
        
        // Repository에서 페이징 처리된 데이터 조회
        Page<UserCmLog> userCmLogPage = userCmLogRepository.findAllWithJoinsPaged(pageable);
        
        // Entity를 DTO로 변환
        List<UserCmLogResponseDto> userCmLogDtos = userCmLogPage.getContent().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        
        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();
        response.put("content", userCmLogDtos); // 실제 데이터
        response.put("totalElements", userCmLogPage.getTotalElements()); // 전체 데이터 개수
        response.put("totalPages", userCmLogPage.getTotalPages()); // 전체 페이지 수
        response.put("currentPage", userCmLogPage.getNumber()); // 현재 페이지 번호
        response.put("size", userCmLogPage.getSize()); // 페이지당 크기
        response.put("hasNext", userCmLogPage.hasNext()); // 다음 페이지 존재 여부
        response.put("hasPrevious", userCmLogPage.hasPrevious()); // 이전 페이지 존재 여부
        
        log.info("페이징 UserCmLog 조회 완료 - 총 {}개, 현재 페이지: {}", 
                userCmLogPage.getTotalElements(), page);
        return response;
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
        
        // 페이징 정보 생성
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize());
        
        // Repository에서 동적 검색 실행
        Page<UserCmLog> userCmLogPage = userCmLogRepository.findBySearchCriteria(
            searchRequest.getUserIndexEventTrigger(),
            searchRequest.getUserIndexEventParty(),
            searchRequest.getUserRoleIndex(),
            searchRequest.getUserRoleIndex2(),
            searchRequest.getUserCmLogValueTypeIndex(),
            searchRequest.getUserCmLogCreateTimeStart(),
            searchRequest.getUserCmLogCreateTimeEnd(),
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
            .eventTriggerUserIndex(userCmLog.getUserIndexEventTrigger() != null ? 
                userCmLog.getUserIndexEventTrigger().getUserIndex().longValue() : null)
            .eventTriggerUserEmail(userCmLog.getUserIndexEventTrigger() != null && 
                userCmLog.getUserIndexEventTrigger().getUsersId() != null ? 
                userCmLog.getUserIndexEventTrigger().getUsersId().getEmail() : null)
            .eventTriggerUserName(userCmLog.getUserIndexEventTrigger() != null && 
                userCmLog.getUserIndexEventTrigger().getUsersId() != null ? 
                userCmLog.getUserIndexEventTrigger().getUsersId().getName() : null)
            .eventTriggerUserRole(getEventTriggerUserRole(userCmLog))
            
            // Event Party User 정보 (null 안전 처리)
            .eventPartyUserIndex(userCmLog.getUserIndexEventParty() != null ? 
                userCmLog.getUserIndexEventParty().getUserIndex().longValue() : null)
            .eventPartyUserEmail(userCmLog.getUserIndexEventParty() != null && 
                userCmLog.getUserIndexEventParty().getUsersId() != null ? 
                userCmLog.getUserIndexEventParty().getUsersId().getEmail() : null)
            .eventPartyUserName(userCmLog.getUserIndexEventParty() != null && 
                userCmLog.getUserIndexEventParty().getUsersId() != null ? 
                userCmLog.getUserIndexEventParty().getUsersId().getName() : null)
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
     * Event Trigger User의 역할 정보 조회
     * 
     * 목적: Event Trigger User의 역할명을 안전하게 조회
     * 
     * 특징:
     * - null 안전 처리: UserRole이 null일 경우 기본값 반환
     * - 중첩된 연관 관계 처리: User → UserRole
     * 
     * @param userCmLog UserCmLog Entity
     * @return 역할명 또는 "알 수 없음"
     */
    private String getEventTriggerUserRole(UserCmLog userCmLog) {
        if (userCmLog.getUserIndexEventTrigger() != null) {
            Integer userRoleIndex = userCmLog.getUserIndexEventTrigger().getUserRoleIndex();
            if (userRoleIndex != null) {
                return getUserRoleName(userRoleIndex);
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
     * 
     * @param userCmLog UserCmLog Entity
     * @return 역할명 또는 "알 수 없음"
     */
    private String getEventPartyUserRole(UserCmLog userCmLog) {
        if (userCmLog.getUserIndexEventParty() != null) {
            Integer userRoleIndex = userCmLog.getUserIndexEventParty().getUserRoleIndex();
            if (userRoleIndex != null) {
                return getUserRoleName(userRoleIndex);
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
} 