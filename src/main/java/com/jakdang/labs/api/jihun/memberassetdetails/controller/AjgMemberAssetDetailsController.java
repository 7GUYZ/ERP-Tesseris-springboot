package com.jakdang.labs.api.jihun.memberassetdetails.controller;

import com.jakdang.labs.api.jihun.memberassetdetails.dto.MemberAssetDetailsResponseDto;
import com.jakdang.labs.api.jihun.memberassetdetails.dto.MemberAssetDetailsSearchDto;
import com.jakdang.labs.api.jihun.memberassetdetails.service.AjgMemberAssetDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/memberassetdetails")
@RequiredArgsConstructor
@Slf4j
public class AjgMemberAssetDetailsController {
    
    private final AjgMemberAssetDetailsService ajgMemberAssetDetailsService;
    
    @GetMapping
    public ResponseEntity<Page<MemberAssetDetailsResponseDto>> getMemberAssetDetails(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "25") Integer size) {
        
        // 최대 크기 제한 (메모리 보호)
        int maxSize = 100000;
        if (size != null && size > maxSize) {
            size = maxSize;
        }
        
        // 요청된 크기 우선 사용, 기본값 25
        int actualSize = size != null && size > 0 ? size : 25;
        MemberAssetDetailsSearchDto.PaginationInfo paginationInfo = new MemberAssetDetailsSearchDto.PaginationInfo(page, actualSize);
        MemberAssetDetailsSearchDto searchDto = new MemberAssetDetailsSearchDto(null, paginationInfo);
        
        Page<MemberAssetDetailsResponseDto> result = ajgMemberAssetDetailsService.searchMemberAssetDetails(searchDto);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/search")
    public ResponseEntity<Page<MemberAssetDetailsResponseDto>> searchMemberAssetDetails(
            @RequestBody MemberAssetDetailsSearchDto searchDto) {
        Page<MemberAssetDetailsResponseDto> result = ajgMemberAssetDetailsService.searchMemberAssetDetails(searchDto);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<MemberAssetDetailsResponseDto>> searchMemberAssetDetailsGet(
            @RequestParam(required = false) String userEmail,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String userPhone,
            @RequestParam(required = false) Integer userRoleIndex,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "25") Integer size) {
        
        // 최대 크기 제한 (메모리 보호)
        int maxSize = 100000;
        if (size != null && size > maxSize) {
            size = maxSize;
        }
        
        // 요청된 크기 우선 사용, 기본값 25
        int actualSize = size != null && size > 0 ? size : 25;
        MemberAssetDetailsSearchDto.SearchCriteria searchCriteria = new MemberAssetDetailsSearchDto.SearchCriteria(userEmail, userName, userPhone, userRoleIndex);
        MemberAssetDetailsSearchDto.PaginationInfo paginationInfo = new MemberAssetDetailsSearchDto.PaginationInfo(page, actualSize);
        MemberAssetDetailsSearchDto searchDto = new MemberAssetDetailsSearchDto(searchCriteria, paginationInfo);
        
        Page<MemberAssetDetailsResponseDto> result = ajgMemberAssetDetailsService.searchMemberAssetDetails(searchDto);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/lookup/grades")
    public ResponseEntity<Map<String, Object>> getGrades() {
        try {
            List<Map<String, Object>> grades = ajgMemberAssetDetailsService.getUserRoles();
            return ResponseEntity.ok(Map.of("success", true, "data", grades));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "등급 조회 중 오류가 발생했습니다."));
        }
    }
    
    @PostMapping("/payment")
    public ResponseEntity<Map<String, Object>> processPayment(@RequestBody Map<String, Object> paymentRequest) {
        try {
            log.info("paymentRequest: {}", paymentRequest);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> members = (List<Map<String, Object>>) paymentRequest.get("members");
            Integer amount = (Integer) paymentRequest.get("amount");
            String reason = (String) paymentRequest.get("reason");

            // amount null 체크
            if (amount == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "지급 금액(amount)이 필요합니다."));
            }
            
            // 단일 처리 호환성을 위한 처리
            if (members == null) {
                // 기존 단일 처리 방식 지원
                Object memberIdObj = paymentRequest.get("memberId");
                String memberId = memberIdObj != null ? memberIdObj.toString() : null;
            Integer currentCmHeld = (Integer) paymentRequest.get("currentCmHeld");
            
                if (memberId == null || memberId.trim().isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("success", false, "message", "회원 ID가 필요합니다."));
                }
                
                // 단일 회원을 배열로 변환
                members = List.of(Map.of(
                    "memberId", memberId,
                    "currentCmHeld", currentCmHeld != null ? currentCmHeld : 0
                ));
            }
            
            if (members == null || members.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "처리할 회원 목록이 필요합니다."));
            }
            
            AjgMemberAssetDetailsService.BulkPaymentResult result = ajgMemberAssetDetailsService.processBulkPaymentWithFullTransaction(members, amount, reason);
            
            if (result.isOverallSuccess()) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", result.getMessage(),
                    "totalCount", result.getTotalCount(),
                    "successCount", result.getSuccessCount(),
                    "failureCount", result.getFailureCount(),
                    "results", result.getResults()
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", result.getMessage(),
                    "totalCount", result.getTotalCount(),
                    "successCount", result.getSuccessCount(),
                    "failureCount", result.getFailureCount(),
                    "results", result.getResults()
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "처리 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    @PostMapping("/collection")
    public ResponseEntity<Map<String, Object>> processCollection(@RequestBody Map<String, Object> collectionRequest) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> members = (List<Map<String, Object>>) collectionRequest.get("members");
            Integer amount = (Integer) collectionRequest.get("amount");
            String reason = (String) collectionRequest.get("reason");

            // amount null 체크
            if (amount == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "회수 금액(amount)이 필요합니다."));
            }
            
            // 단일 처리 호환성을 위한 처리
            if (members == null) {
                // 기존 단일 처리 방식 지원
                Object memberIdObj = collectionRequest.get("memberId");
                String memberId = memberIdObj != null ? memberIdObj.toString() : null;
            Integer currentCmHeld = (Integer) collectionRequest.get("currentCmHeld");
            
                if (memberId == null || memberId.trim().isEmpty()) {
                    return ResponseEntity.badRequest().body(Map.of("success", false, "message", "회원 ID가 필요합니다."));
                }
                
                // 단일 회원을 배열로 변환
                members = List.of(Map.of(
                    "memberId", memberId,
                    "currentCmHeld", currentCmHeld != null ? currentCmHeld : 0
                ));
            }
            
            if (members == null || members.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "처리할 회원 목록이 필요합니다."));
            }
            
            AjgMemberAssetDetailsService.BulkPaymentResult result = ajgMemberAssetDetailsService.processBulkCollectionWithFullTransaction(members, amount, reason);
            
            if (result.isOverallSuccess()) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", result.getMessage(),
                    "totalCount", result.getTotalCount(),
                    "successCount", result.getSuccessCount(),
                    "failureCount", result.getFailureCount(),
                    "results", result.getResults()
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", result.getMessage(),
                    "totalCount", result.getTotalCount(),
                    "successCount", result.getSuccessCount(),
                    "failureCount", result.getFailureCount(),
                    "results", result.getResults()
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "처리 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
} 