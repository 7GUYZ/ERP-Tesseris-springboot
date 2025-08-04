package com.jakdang.labs.api.taekjun.user_log.controller;

import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.taekjun.user_log.dto.UserLogResponseDTO;
import com.jakdang.labs.api.taekjun.user_log.service.UserLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 사용자 CM 사용 내역 API 컨트롤러
 * 
 * 주요 기능:
 * 1. 사용자별 CM 사용 내역 조회
 * 2. 페이징 처리된 내역 조회
 * 3. 월별 사용 내역 조회
 * 4. 거래 타입별 필터링
 */
@RestController
@RequestMapping("/api/user_log")
@RequiredArgsConstructor
@Slf4j
public class UserLogController {

    private final UserLogService userLogService;

    /**
     * 사용자 CM 사용 내역 조회 (페이징)
     * 
     * @param userIndex 사용자 인덱스
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지당 데이터 개수 (기본값: 20)
     * @return CM 사용 내역 목록
     */
    @GetMapping("/{userIndex}")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getUserLogs(
            @PathVariable("userIndex") Integer userIndex,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        
        log.info("사용자 CM 사용 내역 조회 - userIndex: {}, page: {}, size: {}, year: {}, month: {}", 
                userIndex, page, size, year, month);
        
        try {
            Map<String, Object> result;
            if (year != null && month != null) {
                result = userLogService.getUserLogsByMonth(userIndex, year, month, page, size);
            } else {
                result = userLogService.getUserLogs(userIndex, page, size);
            }
            return ResponseEntity.ok(ResponseDTO.createSuccessResponse("CM 사용 내역 조회 성공", result));
        } catch (Exception e) {
            log.error("CM 사용 내역 조회 실패 - userIndex: {}, error: {}", userIndex, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ResponseDTO.<Map<String, Object>>builder()
                .resultCode(400)
                .resultMessage("CM 사용 내역 조회 실패")
                .build());
        }
    }

    /**
     * 사용자 월별 CM 사용 내역 조회
     * 
     * @param userIndex 사용자 인덱스
     * @param year 년도 (예: 2025)
     * @param month 월 (예: 7)
     * @return 해당 월의 CM 사용 내역
     */
    @GetMapping("/{userIndex}/monthly")
    public ResponseEntity<ResponseDTO<List<UserLogResponseDTO>>> getUserMonthlyLogs(
            @PathVariable("userIndex") Integer userIndex,
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        
        log.info("사용자 월별 CM 사용 내역 조회 - userIndex: {}, year: {}, month: {}", userIndex, year, month);
        
        try {
            List<UserLogResponseDTO> result = userLogService.getUserMonthlyLogs(userIndex, year, month);
            return ResponseEntity.ok(ResponseDTO.createSuccessResponse("월별 CM 사용 내역 조회 성공", result));
        } catch (Exception e) {
            log.error("월별 CM 사용 내역 조회 실패 - userIndex: {}, error: {}", userIndex, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ResponseDTO.<List<UserLogResponseDTO>>builder()
                .resultCode(400)
                .resultMessage("월별 CM 사용 내역 조회 실패")
                .build());
        }
    }

    /**
     * 사용자 거래 타입별 CM 사용 내역 조회
     * 
     * @param userIndex 사용자 인덱스
     * @param transactionType 거래 타입 (예: 9=구매, 8=판매)
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지당 데이터 개수 (기본값: 20)
     * @return 거래 타입별 CM 사용 내역
     */
    @GetMapping("/{userIndex}/transaction-type/{transactionType}")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getUserLogsByTransactionType(
            @PathVariable("userIndex") Integer userIndex,
            @PathVariable("transactionType") Integer transactionType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("사용자 거래 타입별 CM 사용 내역 조회 - userIndex: {}, transactionType: {}, page: {}, size: {}", 
                userIndex, transactionType, page, size);
        
        try {
            Map<String, Object> result = userLogService.getUserLogsByTransactionType(userIndex, transactionType, page, size);
            return ResponseEntity.ok(ResponseDTO.createSuccessResponse("거래 타입별 CM 사용 내역 조회 성공", result));
        } catch (Exception e) {
            log.error("거래 타입별 CM 사용 내역 조회 실패 - userIndex: {}, error: {}", userIndex, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ResponseDTO.<Map<String, Object>>builder()
                .resultCode(400)
                .resultMessage("거래 타입별 CM 사용 내역 조회 실패")
                .build());
        }
    }

    /**
     * 사용자 CM 사용 통계 조회
     * 
     * @param userIndex 사용자 인덱스
     * @return CM 사용 통계 정보
     */
    @GetMapping("/{userIndex}/statistics")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getUserLogStatistics(
            @PathVariable("userIndex") Integer userIndex) {
        
        log.info("사용자 CM 사용 통계 조회 - userIndex: {}", userIndex);
        
        try {
            Map<String, Object> result = userLogService.getUserLogStatistics(userIndex);
            return ResponseEntity.ok(ResponseDTO.createSuccessResponse("CM 사용 통계 조회 성공", result));
        } catch (Exception e) {
            log.error("CM 사용 통계 조회 실패 - userIndex: {}, error: {}", userIndex, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ResponseDTO.<Map<String, Object>>builder()
                .resultCode(400)
                .resultMessage("CM 사용 통계 조회 실패")
                .build());
        }
    }

    /**
     * 사용자별 CM 로그 조회 (페이징)
     * 
     * @param userIndex 사용자 인덱스
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지당 데이터 개수 (기본값: 20)
     * @param year 연도 (선택)
     * @param month 월 (선택)
     * @return 페이징된 CM 로그 목록
     */
    @GetMapping("/{userIndex}/all")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getAllLogs(
            @PathVariable("userIndex") Integer userIndex,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        
        log.info("전체 CM 로그 조회 - userIndex: {}, page: {}, size: {}, year: {}, month: {}", 
                userIndex, page, size, year, month);
        
        try {
            Map<String, Object> result = userLogService.getAllLogs(userIndex, page, size, year, month);
            return ResponseEntity.ok(ResponseDTO.createSuccessResponse("전체 CM 로그 조회 성공", result));
        } catch (Exception e) {
            log.error("전체 CM 로그 조회 실패 - userIndex: {}, error: {}", userIndex, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ResponseDTO.<Map<String, Object>>builder()
                .resultCode(400)
                .resultMessage("전체 CM 로그 조회 실패")
                .build());
        }
    }

    /**
     * 내가 쓴 금액 조회
     * 
     * @param userIndex 사용자 인덱스
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지당 데이터 개수 (기본값: 20)
     * @param year 연도 (선택)
     * @param month 월 (선택)
     * @return 내가 쓴 금액 내역
     */
    @GetMapping("/{userIndex}/spent")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getSpentLogs(
            @PathVariable("userIndex") Integer userIndex,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        
        log.info("내가 쓴 금액 조회 - userIndex: {}, page: {}, size: {}, year: {}, month: {}", 
                userIndex, page, size, year, month);
        
        try {
            Map<String, Object> result = userLogService.getSpentLogs(userIndex, page, size, year, month);
            return ResponseEntity.ok(ResponseDTO.createSuccessResponse("내가 쓴 금액 조회 성공", result));
        } catch (Exception e) {
            log.error("내가 쓴 금액 조회 실패 - userIndex: {}, error: {}", userIndex, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ResponseDTO.<Map<String, Object>>builder()
                .resultCode(400)
                .resultMessage("내가 쓴 금액 조회 실패")
                .build());
        }
    }

    /**
     * 내가 받은 금액 조회
     * 
     * @param userIndex 사용자 인덱스
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지당 데이터 개수 (기본값: 20)
     * @param year 연도 (선택)
     * @param month 월 (선택)
     * @return 내가 받은 금액 내역
     */
    @GetMapping("/{userIndex}/received")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> getReceivedLogs(
            @PathVariable("userIndex") Integer userIndex,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        
        log.info("내가 받은 금액 조회 - userIndex: {}, page: {}, size: {}, year: {}, month: {}", 
                userIndex, page, size, year, month);
        
        try {
            Map<String, Object> result = userLogService.getReceivedLogs(userIndex, page, size, year, month);
            return ResponseEntity.ok(ResponseDTO.createSuccessResponse("내가 받은 금액 조회 성공", result));
        } catch (Exception e) {
            log.error("내가 받은 금액 조회 실패 - userIndex: {}, error: {}", userIndex, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ResponseDTO.<Map<String, Object>>builder()
                .resultCode(400)
                .resultMessage("내가 받은 금액 조회 실패")
                .build());
        }
    }
} 