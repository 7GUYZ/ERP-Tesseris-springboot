package com.jakdang.labs.api.jihun.common.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jakdang.labs.api.jihun.common.service.ExcelDownloadService;

import java.util.Map;

/**
 * 엑셀 다운로드 전용 REST API 컨트롤러
 * 
 * 주요 기능:
 * 1. 회원 자산 내역 엑셀 다운로드
 * 2. 회원 자산 현황 엑셀 다운로드
 * 3. 청크 단위 대용량 데이터 처리
 * 
 * API 설계 원칙:
 * - 대용량 데이터 안전 처리
 * - 메모리 효율적 처리
 * - 엑셀 다운로드에 최적화된 응답
 */
@RestController
@RequestMapping("/api/common/exceldownload")
@RequiredArgsConstructor
@Slf4j
public class ExcelDownloadController {

    private final ExcelDownloadService excelDownloadService;

    /**
     * 회원 자산 내역 엑셀 다운로드 API
     * 
     * 목적: 회원 자산 내역 데이터를 청크 단위로 안전하게 처리
     * 
     * 특징:
     * - 5만개씩 청크 단위로 처리
     * - 메모리 효율적 처리
     * - EntityNotFoundException 방지
     * 
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지당 데이터 개수 (기본값: 50000)
     * @return 엑셀 다운로드용 데이터
     */
    @GetMapping("/memberaccount")
    public ResponseEntity<Map<String, Object>> getMemberAccountExcelData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50000") int size) {
        log.info("회원 자산 내역 엑셀 다운로드 API 호출 - page: {}, size: {}", page, size);

        // 최대 크기 제한 (메모리 보호)
        int maxSize = 100000;
        if (size > maxSize) {
            log.warn("요청된 size({})가 최대 크기({})를 초과하여 {}로 제한됨", size, maxSize, maxSize);
            size = maxSize;
        }

        try {
            Map<String, Object> result = excelDownloadService.getMemberAccountExcelData(page, size);
            log.info("회원 자산 내역 엑셀 다운로드 데이터 조회 완료 - 페이지: {}, 데이터: {}개", page,
                    ((java.util.List<?>) result.get("content")).size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("회원 자산 내역 엑셀 다운로드 데이터 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 회원 자산 현황 엑셀 다운로드 API
     * 
     * 목적: 회원 자산 현황 데이터를 청크 단위로 안전하게 처리
     * 
     * 특징:
     * - 5만개씩 청크 단위로 처리
     * - 메모리 효율적 처리
     * - EntityNotFoundException 방지
     * 
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지당 데이터 개수 (기본값: 50000)
     * @return 엑셀 다운로드용 데이터
     */
    @GetMapping("/memberassetdetails")
    public ResponseEntity<Map<String, Object>> getMemberAssetDetailsExcelData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50000") int size) {
        log.info("회원 자산 현황 엑셀 다운로드 API 호출 - page: {}, size: {}", page, size);

        // 최대 크기 제한 (메모리 보호)
        int maxSize = 100000;
        if (size > maxSize) {
            log.warn("요청된 size({})가 최대 크기({})를 초과하여 {}로 제한됨", size, maxSize, maxSize);
            size = maxSize;
        }

        try {
            Map<String, Object> result = excelDownloadService.getMemberAssetDetailsExcelData(page, size);
            log.info("회원 자산 현황 엑셀 다운로드 데이터 조회 완료 - 페이지: {}, 데이터: {}개", page,
                    ((java.util.List<?>) result.get("content")).size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("회원 자산 현황 엑셀 다운로드 데이터 조회 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }
} 