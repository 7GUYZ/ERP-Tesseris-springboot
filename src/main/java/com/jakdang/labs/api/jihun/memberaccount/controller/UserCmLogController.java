package com.jakdang.labs.api.jihun.memberaccount.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jakdang.labs.api.jihun.memberaccount.dto.LookupDataDto;
import com.jakdang.labs.api.jihun.memberaccount.dto.UserCmLogSearchRequestDto;
import com.jakdang.labs.api.jihun.memberaccount.service.UserCmLogService;

import java.util.List;
import java.util.Map;

/**
 * UserCmLog 관련 REST API 컨트롤러
 * 
 * 주요 기능:
 * 1. 페이징 처리된 전체 조회 API
 * 2. 동적 검색 API
 * 3. 룩업 데이터 API
 * 4. CORS 설정으로 React 앱과 통신
 * 
 * API 설계 원칙:
 * - RESTful API 설계
 * - 적절한 HTTP 상태 코드 반환
 * - 일관된 응답 형식
 * - 에러 처리 및 로깅
 */
@RestController
@RequestMapping("/api/memberaccount")
@RequiredArgsConstructor
@Slf4j
public class UserCmLogController {

    private final UserCmLogService userCmLogService;

    /**
     * 페이징 처리된 모든 UserCmLog 조회 API (메인 API)
     * 
     * 목적: 전체 데이터의 페이징 처리된 조회
     * 
     * 특징:
     * - 페이징 정보를 포함한 응답
     * - 메모리 효율적: 한 번에 제한된 개수만 처리
     * - 성능 최적화: 필요한 데이터만 조회
     * 
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지당 데이터 개수 (기본값: 100)
     * @return 페이징 정보와 데이터를 포함한 응답
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUserCmLogsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size) {
        log.info("페이징 UserCmLog 조회 API 호출 - page: {}, size: {}", page, size);

        try {
            Map<String, Object> result = userCmLogService.getAllUserCmLogsPaged(page, size);
            log.info("페이징 UserCmLog 조회 완료 - 페이지: {}, 데이터: {}개", page,
                    ((List<?>) result.get("content")).size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("페이징 UserCmLog 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 🆕 동적 검색 API (PHP 검색 기능 재현)
     * 
     * 목적: 복합 조건을 이용한 동적 검색 기능
     * 
     * 특징:
     * - POST 방식으로 복합 검색 조건 전달
     * - PHP와 동일한 검색 로직
     * - 페이징 지원
     * 
     * @param searchRequest 검색 조건 DTO
     * @return 검색 결과와 페이징 정보
     */
    @PostMapping("/search")
    public ResponseEntity<Map<String, Object>> searchUserCmLogs(@RequestBody UserCmLogSearchRequestDto searchRequest) {
        log.info("동적 검색 API 호출 - 조건: {}", searchRequest);

        try {
            Map<String, Object> result = userCmLogService.searchUserCmLogs(searchRequest);
            log.info("동적 검색 완료 - 총 {}개 결과", result.get("totalElements"));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("동적 검색 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 🆕 사용자 역할 목록 조회 API
     * 
     * @return 사용자 역할 목록
     */
    @GetMapping("/lookup/roles")
    public ResponseEntity<List<LookupDataDto>> getUserRoles() {
        log.info("사용자 역할 목록 조회 API 호출");

        try {
            List<LookupDataDto> result = userCmLogService.getUserRoles();
            log.info("사용자 역할 목록 조회 완료 - {}개", result.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("사용자 역할 목록 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 🆕 가치 유형 목록 조회 API (전체값)
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
    @GetMapping("/lookup/value-types")
    public ResponseEntity<List<LookupDataDto>> getValueTypes() {
        log.info("가치 유형 목록 조회 API 호출 (전체값)");

        try {
            List<LookupDataDto> result = userCmLogService.getValueTypes();
            log.info("가치 유형 목록 조회 완료 (전체값) - {}개", result.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("가치 유형 목록 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }



    /**
     * 🆕 결제 수단 목록 조회 API
     * 
     * @return 결제 수단 목록
     */
    @GetMapping("/lookup/payment-types")
    public ResponseEntity<List<LookupDataDto>> getPaymentTypes() {
        log.info("결제 수단 목록 조회 API 호출");

        try {
            List<LookupDataDto> result = userCmLogService.getPaymentTypes();
            log.info("결제 수단 목록 조회 완료 - {}개", result.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("결제 수단 목록 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 🆕 거래 유형 목록 조회 API
     * 
     * @return 거래 유형 목록
     */
    @GetMapping("/lookup/transaction-types")
    public ResponseEntity<List<LookupDataDto>> getTransactionTypes() {
        log.info("거래 유형 목록 조회 API 호출");

        try {
            List<LookupDataDto> result = userCmLogService.getTransactionTypes();
            log.info("거래 유형 목록 조회 완료 - {}개", result.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("거래 유형 목록 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }


}