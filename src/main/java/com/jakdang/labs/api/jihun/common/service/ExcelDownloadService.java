package com.jakdang.labs.api.jihun.common.service;

import com.jakdang.labs.api.jihun.common.dto.ExcelDownloadDto;
import com.jakdang.labs.api.jihun.common.repository.ExcelDownloadRepository;
import com.jakdang.labs.entity.UserCmLog;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.api.jihun.memberaccount.repository.AjhUserCmLogRepository;
import com.jakdang.labs.api.jihun.memberaccount.service.UserCmLogService;
import com.jakdang.labs.api.jihun.memberaccount.dto.UserCmLogResponseDto;
import com.jakdang.labs.api.jihun.memberassetdetails.repository.AjgMemberAssetDetailsRepository;
import com.jakdang.labs.api.jihun.memberassetdetails.dto.MemberAssetDetailsResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 엑셀 다운로드 성능 최적화 Service
 * 
 * 주요 최적화 포인트:
 * 1. JOIN FETCH를 사용하여 N+1 문제 해결
 * 2. Object[] 배열을 직접 매핑하여 변환 오버헤드 최소화
 * 3. EntityNotFoundException 방지를 위한 안전한 데이터 처리
 * 4. 대용량 데이터 처리에 최적화된 구조
 * 5. 메모리 효율적인 데이터 변환
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ExcelDownloadService {

    // 기존 Repository (호환성을 위해 유지)
    private final AjhUserCmLogRepository userCmLogRepository;
    private final AjgMemberAssetDetailsRepository memberAssetDetailsRepository;
    
    // 🆕 기존 서비스 (실제 이메일 데이터를 가져오기 위해 사용)
    private final UserCmLogService userCmLogService;
    
    // 🆕 성능 최적화를 위한 새로운 Repository
    private final ExcelDownloadRepository excelDownloadRepository;

    /**
     * 회원 자산 내역 엑셀 다운로드 데이터 조회 (기존 서비스 사용)
     * 
     * 기존 memberaccount 서비스를 사용하여 실제 이메일 데이터를 가져옴
     * 
     * @param page 페이지 번호
     * @param size 페이지당 데이터 개수
     * @return 엑셀 다운로드용 데이터
     */
    public Map<String, Object> getMemberAccountExcelData(int page, int size) {
        log.info("회원 자산 내역 엑셀 다운로드 데이터 조회 시작 (기존 서비스 사용) - page: {}, size: {}", page, size);
        
        // 🆕 최근 3만건만 다운로드하도록 수정
        // 전체 데이터가 3만건을 초과하는 경우, 최근 3만건만 가져오기
        int maxRecords = 30000;
        int adjustedSize = Math.min(size, maxRecords);
        
        // 🆕 엑셀 다운로드 전용 메서드 사용 (최근 3만건 제한)
        Map<String, Object> searchResult = userCmLogService.getLatestUserCmLogsForExcel(page, adjustedSize);
        
        // 기존 서비스에서 받은 데이터를 엑셀 형식으로 변환
        List<Map<String, Object>> content = ((List<UserCmLogResponseDto>) searchResult.get("content")).stream()
            .map(dto -> {
                    Map<String, Object> map = new HashMap<>();
                map.put("userCmLogIndex", dto.getUserCmLogIndex() != null ? dto.getUserCmLogIndex() : "");
                map.put("userCmLogValue", dto.getUserCmLogValue() != null ? dto.getUserCmLogValue() : "");
                map.put("userCmLogReason", dto.getUserCmLogReason() != null ? dto.getUserCmLogReason() : "");
                map.put("userCmLogCreateTime", dto.getUserCmLogCreateTime() != null ? dto.getUserCmLogCreateTime() : "");
                map.put("userCouponValue", dto.getUserCouponValue() != null ? dto.getUserCouponValue() : "");
                map.put("eventTriggerUserEmail", dto.getEventTriggerUserEmail() != null ? dto.getEventTriggerUserEmail() : "");
                map.put("eventTriggerUserRole", dto.getEventTriggerUserRole() != null ? dto.getEventTriggerUserRole() : "");
                map.put("eventPartyUserEmail", dto.getEventPartyUserEmail() != null ? dto.getEventPartyUserEmail() : "");
                map.put("eventPartyUserName", dto.getEventPartyUserName() != null ? dto.getEventPartyUserName() : "");
                map.put("eventPartyUserRole", dto.getEventPartyUserRole() != null ? dto.getEventPartyUserRole() : "");
                map.put("transactionTypeName", dto.getTransactionTypeName() != null ? dto.getTransactionTypeName() : "");
                    
                    return map;
            })
            .collect(Collectors.toList());
        
        // 페이징 정보와 함께 반환
        Map<String, Object> result = new HashMap<>();
        result.put("content", content);
        result.put("totalElements", searchResult.get("totalElements"));
        result.put("totalPages", searchResult.get("totalPages"));
        result.put("currentPage", searchResult.get("currentPage"));
        result.put("size", searchResult.get("size"));
        result.put("hasNext", searchResult.get("hasNext"));
        result.put("hasPrevious", searchResult.get("hasPrevious"));
        
        log.info("회원 자산 내역 엑셀 다운로드 데이터 조회 완료 (기존 서비스 사용) - 총 {}개 중 {}개 반환 (최근 3만건 제한)", 
                searchResult.get("totalElements"), content.size());
        return result;
    }

    /**
     * 회원 자산 현황 엑셀 다운로드 데이터 조회 (성능 최적화 버전)
     * 
     * 성능 최적화 내용:
     * - 필요한 컬럼만 선택하여 메모리 사용량 최소화
     * - Object[] 배열을 직접 매핑하여 변환 속도 향상
     * - 안전한 데이터 변환 처리
     * - 대용량 데이터 처리에 최적화된 페이징
     * 
     * @param page 페이지 번호
     * @param size 페이지당 데이터 개수
     * @return 엑셀 다운로드용 데이터
     */
    public Map<String, Object> getMemberAssetDetailsExcelData(int page, int size) {
        log.info("회원 자산 현황 엑셀 다운로드 데이터 조회 시작 (최적화 버전) - page: {}, size: {}", page, size);
        
        // 페이징 정보 생성
        Pageable pageable = PageRequest.of(page, size);
        
        // 🆕 최적화된 쿼리로 데이터 조회
        Page<Object[]> rawDataPage = excelDownloadRepository.findMemberAssetDetailsExcelDataOptimized(pageable);
        
        // Object[] 배열을 DTO로 변환 (성능 최적화)
        List<Map<String, Object>> content = rawDataPage.getContent().stream()
            .map(row -> {
                try {
                    // DTO로 변환 후 Map으로 변환
                    ExcelDownloadDto.MemberAssetDetailsExcelData dto = 
                        ExcelDownloadDto.MemberAssetDetailsExcelData.fromObjectArray(row);
                    
                    Map<String, Object> map = new HashMap<>();
                    map.put("userIndex", dto.getUserIndex());
                    map.put("usersId", dto.getUsersId());
                    map.put("userRoleKorNm", row[2] != null ? String.valueOf(row[2]) : "알 수 없음"); // user_role_kor_nm 직접 사용
                    map.put("userName", dto.getUserName());
                    map.put("userPhone", dto.getUserPhone());
                    map.put("storeName", dto.getStoreName());
                    map.put("userCmCurrent", dto.getUserCmCurrent());
                    map.put("userCmpCurrent", dto.getUserCmpCurrent());
                    map.put("userCashCurrent", dto.getUserCashCurrent());
                    
                    return map;
                } catch (Exception e) {
                    log.warn("데이터 변환 중 오류 발생: {}", e.getMessage());
                    return null;
                }
            })
            .filter(map -> map != null) // null 값 필터링
            .collect(Collectors.toList());
        
        // 페이징 정보와 함께 반환
        Map<String, Object> result = new HashMap<>();
        result.put("content", content);
        result.put("totalElements", rawDataPage.getTotalElements());
        result.put("totalPages", rawDataPage.getTotalPages());
        result.put("currentPage", rawDataPage.getNumber());
        result.put("size", rawDataPage.getSize());
        result.put("hasNext", rawDataPage.hasNext());
        result.put("hasPrevious", rawDataPage.hasPrevious());
        
        log.info("회원 자산 현황 엑셀 다운로드 데이터 조회 완료 (최적화 버전) - 총 {}개 중 {}개 반환", 
                rawDataPage.getTotalElements(), content.size());
        return result;
    }

    /**
     * 역할 인덱스를 역할명으로 변환
     * 
     * @param roleIndex 역할 인덱스
     * @return 역할명
     */
    private String getRoleName(Integer roleIndex) {
        if (roleIndex == null) return "알 수 없음";
        
        return switch (roleIndex) {
            case 1 -> "관리자";
            case 2 -> "사업자";
            case 3 -> "가맹점";
            case 4 -> "일반회원";
            default -> "알 수 없음";
        };
    }

    /**
     * 거래 유형 인덱스를 거래 유형명으로 변환
     * 
     * @param transactionTypeIndex 거래 유형 인덱스
     * @return 거래 유형명
     */
    private String getTransactionTypeName(Integer transactionTypeIndex) {
        if (transactionTypeIndex == null) return "알 수 없음";
        
        return switch (transactionTypeIndex) {
            case 1 -> "충전";
            case 2 -> "사용";
            case 3 -> "환불";
            case 4 -> "이체";
            case 5 -> "수수료";
            default -> "알 수 없음";
        };
    }
} 