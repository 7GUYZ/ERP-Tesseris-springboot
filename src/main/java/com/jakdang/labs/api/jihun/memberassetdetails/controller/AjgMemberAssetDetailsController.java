package com.jakdang.labs.api.jihun.memberassetdetails.controller;

import com.jakdang.labs.api.jihun.memberassetdetails.dto.MemberAssetDetailsResponseDto;
import com.jakdang.labs.api.jihun.memberassetdetails.dto.MemberAssetDetailsSearchDto;
import com.jakdang.labs.api.jihun.memberassetdetails.service.AjgMemberAssetDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/memberassetdetails")
@RequiredArgsConstructor
public class AjgMemberAssetDetailsController {
    
    private final AjgMemberAssetDetailsService ajgMemberAssetDetailsService;
    
    @GetMapping
    public ResponseEntity<Page<MemberAssetDetailsResponseDto>> getMemberAssetDetails(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        MemberAssetDetailsSearchDto.PaginationInfo paginationInfo = new MemberAssetDetailsSearchDto.PaginationInfo(page, size);
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
            @RequestParam(defaultValue = "10") Integer size) {
        
        MemberAssetDetailsSearchDto.SearchCriteria searchCriteria = new MemberAssetDetailsSearchDto.SearchCriteria(userEmail, userName, userPhone, userRoleIndex);
        MemberAssetDetailsSearchDto.PaginationInfo paginationInfo = new MemberAssetDetailsSearchDto.PaginationInfo(page, size);
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
            String memberId = (String) paymentRequest.get("memberId");
            Integer amount = (Integer) paymentRequest.get("amount");
            String reason = (String) paymentRequest.get("reason");
            Integer currentCmHeld = (Integer) paymentRequest.get("currentCmHeld");
            
            boolean result = ajgMemberAssetDetailsService.processPayment(memberId, amount, reason, currentCmHeld);
            
            if (result) {
                return ResponseEntity.ok(Map.of("success", true, "message", "CM 지급이 완료되었습니다."));
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "CM 지급 처리 중 오류가 발생했습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "처리 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    @PostMapping("/collection")
    public ResponseEntity<Map<String, Object>> processCollection(@RequestBody Map<String, Object> collectionRequest) {
        try {
            String memberId = (String) collectionRequest.get("memberId");
            Integer amount = (Integer) collectionRequest.get("amount");
            String reason = (String) collectionRequest.get("reason");
            Integer currentCmHeld = (Integer) collectionRequest.get("currentCmHeld");
            
            boolean result = ajgMemberAssetDetailsService.processCollection(memberId, amount, reason, currentCmHeld);
            
            if (result) {
                return ResponseEntity.ok(Map.of("success", true, "message", "CM 회수가 완료되었습니다."));
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "CM 회수 처리 중 오류가 발생했습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "처리 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
} 