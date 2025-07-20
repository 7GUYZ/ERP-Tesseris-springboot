package com.jakdang.labs.api.jihun.memberassetdetails.service;

import com.jakdang.labs.api.jihun.memberassetdetails.dto.MemberAssetDetailsResponseDto;
import com.jakdang.labs.api.jihun.memberassetdetails.dto.MemberAssetDetailsSearchDto;
import com.jakdang.labs.api.jihun.memberassetdetails.repository.AjgMemberAssetDetailsRepository;
import com.jakdang.labs.api.jihun.memberassetdetails.repository.AjgUserCmLogRepository;
import com.jakdang.labs.api.jihun.memberassetdetails.repository.AjgUserCmLogPaymentRepository;
import com.jakdang.labs.api.jihun.memberassetdetails.repository.AjgUserCmLogTransactionTypeRepository;
import com.jakdang.labs.api.jihun.memberassetdetails.repository.AjgUserCmLogValueTypeRepository;
import com.jakdang.labs.entity.UserCmLog;
import com.jakdang.labs.entity.UserCmLogPayment;
import com.jakdang.labs.entity.UserCmLogTransactionType;
import com.jakdang.labs.entity.UserCmLogValueType;
import com.jakdang.labs.entity.UserTesseris;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AjgMemberAssetDetailsService {
    
    private final AjgMemberAssetDetailsRepository ajgMemberAssetDetailsRepository;
    private final AjgUserCmLogRepository userCmLogRepository;
    private final AjgUserCmLogPaymentRepository userCmLogPaymentRepository;
    private final AjgUserCmLogTransactionTypeRepository userCmLogTransactionTypeRepository;
    private final AjgUserCmLogValueTypeRepository userCmLogValueTypeRepository;
    
    public Page<MemberAssetDetailsResponseDto> searchMemberAssetDetails(MemberAssetDetailsSearchDto searchDto) {
        Pageable pageable = PageRequest.of(
            searchDto.getPage() != null ? searchDto.getPage() : 0,
            searchDto.getSize() != null ? searchDto.getSize() : 25
        );
        
        // 검색 파라미터 로깅
        System.out.println("=== 검색 파라미터 디버깅 ===");
        System.out.println("userEmail: " + searchDto.getUserEmail());
        System.out.println("userName: " + searchDto.getUserName());
        System.out.println("userPhone: " + searchDto.getUserPhone());
        System.out.println("userRoleIndex: " + searchDto.getUserRoleIndex());
        System.out.println("==========================");
        
        Page<Object[]> results = ajgMemberAssetDetailsRepository.findMemberAssetDetails(
            searchDto.getUserEmail(),
            searchDto.getUserName(),
            searchDto.getUserPhone(),
            searchDto.getUserRoleIndex(),
            pageable
        );
        
        // Object[]를 DTO로 변환
        List<MemberAssetDetailsResponseDto> dtos = results.getContent().stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
        
        return new org.springframework.data.domain.PageImpl<>(
            dtos, 
            pageable, 
            results.getTotalElements()
        );
    }
    
    private MemberAssetDetailsResponseDto mapToDto(Object[] row) {
        MemberAssetDetailsResponseDto dto = new MemberAssetDetailsResponseDto();
        
        // 안전한 null 체크와 타입 변환
        dto.setUserIndex(row[0] != null ? (Integer) row[0] : null);
        dto.setUserId(row[1] != null ? (String) row[1] : null);
        dto.setUserName(row[2] != null ? (String) row[2] : null);
        dto.setUserPhone(row[3] != null ? (String) row[3] : null);
        dto.setUserEmail(row[4] != null ? (String) row[4] : null); // 이메일 필드 매핑
        dto.setUserRoleKorNm(row[5] != null ? (String) row[5] : "알 수 없음");
        dto.setStoreName(row[6] != null ? (String) row[6] : null);
        dto.setUserCmCurrent(row[7] != null ? String.valueOf(row[7]) : "0");
        dto.setUserCmpCurrent(row[8] != null ? String.valueOf(row[8]) : "0");
        dto.setUserCashCurrent(row[9] != null ? String.valueOf(row[9]) : "0");
        
        // LocalDateTime 변환 (안전한 처리)
        if (row[10] != null) {
            try {
                if (row[10] instanceof LocalDateTime) {
                    dto.setUserCreateTime((LocalDateTime) row[10]);
                } else if (row[10] instanceof String) {
                    dto.setUserCreateTime(LocalDateTime.parse((String) row[10], DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                } else {
                    dto.setUserCreateTime(null);
                }
            } catch (Exception e) {
                dto.setUserCreateTime(null);
            }
        } else {
            dto.setUserCreateTime(null);
        }
        
        dto.setUserBankName(row[11] != null ? (String) row[11] : null);
        dto.setUserBankNumber(row[12] != null ? (String) row[12] : null);
        dto.setUserBankHolder(row[13] != null ? (String) row[13] : null);
        dto.setUserJumin(row[14] != null ? (String) row[14] : null);
        dto.setSuggestionUserId(row[15] != null ? (String) row[15] : null);
        dto.setSuggestionUserName(row[16] != null ? (String) row[16] : null);
        dto.setTemporaryStoreCashValue(row[17] != null ? (String) row[17] : "0");
        
        return dto;
    }
    
    public List<Map<String, Object>> getUserRoles() {
        List<Object[]> roles = ajgMemberAssetDetailsRepository.findUserRoles();
        return roles.stream()
            .map(role -> Map.of(
                "index", role[0] != null ? role[0] : 0,
                "name", role[1] != null ? role[1] : "알 수 없음"
            ))
            .collect(Collectors.toList());
    }
    
    @Transactional
    public boolean processPayment(String memberId, Integer amount, String reason, Integer currentCmHeld) {
        try {
            System.out.println("CM 지급 처리: " + memberId + ", 금액: " + amount + ", 사유: " + reason);
            
            // 1. 회원 조회
            Optional<UserTesseris> memberOpt = ajgMemberAssetDetailsRepository.findByUsersId_Id(memberId);
            if (!memberOpt.isPresent()) {
                System.err.println("CM 지급 처리 실패: 회원을 찾을 수 없습니다. " + memberId);
                return false;
            }
            UserTesseris member = memberOpt.get();
            
            // 2. CM 지급 처리 (레코드가 없으면 INSERT, 있으면 UPDATE)
            int insertedRows = ajgMemberAssetDetailsRepository.insertCmDeposit(memberId, amount);
            int updatedRows = ajgMemberAssetDetailsRepository.updateCmDeposit(memberId, amount);
            
            // INSERT 또는 UPDATE 중 하나라도 성공하면 처리 완료
            if (insertedRows > 0 || updatedRows > 0) {
                System.out.println("CM 지급 처리 완료: " + memberId + ", 금액: " + amount);
                
                // 3. 거래 내역 기록
                boolean logResult = createPaymentLog(member, amount, reason);
                if (!logResult) {
                    System.err.println("거래 내역 기록 실패");
                    return false;
                }
                
                return true;
            } else {
                System.err.println("CM 지급 처리 실패: 회원을 찾을 수 없습니다. " + memberId);
                return false;
            }
        } catch (Exception e) {
            System.err.println("CM 지급 처리 중 오류: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 지급 거래 내역 기록
     */
    private boolean createPaymentLog(UserTesseris member, Integer amount, String reason) {
        try {
            // 1. 지급 관련 인덱스 조회
            Optional<UserCmLogPayment> paymentOpt = userCmLogPaymentRepository.findByUserCmLogPaymentName("입금");
            Optional<UserCmLogTransactionType> transactionOpt = userCmLogTransactionTypeRepository.findByUserCmLogTransactionTypeName("본사지급(CM)");
            Optional<UserCmLogValueType> valueTypeOpt = userCmLogValueTypeRepository.findByUserCmLogValueTypeName("CM");
            
            if (!paymentOpt.isPresent() || !transactionOpt.isPresent() || !valueTypeOpt.isPresent()) {
                System.err.println("거래 내역 기록 실패: 필요한 인덱스를 찾을 수 없습니다.");
                return false;
            }
            
            // 2. UserCmLog 엔티티 생성
            UserCmLog userCmLog = UserCmLog.builder()
                .userCmLogPaymentIndex(paymentOpt.get().getUserCmLogPaymentIndex())
                .userCmLogTransactionTypeIndex(transactionOpt.get().getUserCmLogTransactionTypeIndex())
                .userCmLogValueTypeIndex(valueTypeOpt.get().getUserCmLogValueTypeIndex())
                .userIndexEventTrigger(null) // TODO: 로그인한 관리자 정보 필요
                .userIndexEventParty(member)
                .userCmLogValue(amount)
                .userCmLogReason(reason)
                .userCmLogCreateTime(LocalDateTime.now())
                .build();
            
            // 3. 로그 저장
            userCmLogRepository.save(userCmLog);
            
            System.out.println("거래 내역 기록 완료: " + member.getUsersId().getId() + ", 금액: " + amount);
            return true;
            
        } catch (Exception e) {
            System.err.println("거래 내역 기록 중 오류: " + e.getMessage());
            return false;
        }
    }
    
    @Transactional
    public boolean processCollection(String memberId, Integer amount, String reason, Integer currentCmHeld) {
        try {
            System.out.println("CM 회수 처리: " + memberId + ", 금액: " + amount + ", 사유: " + reason);
            
            // 1. 회원 조회
            Optional<UserTesseris> memberOpt = ajgMemberAssetDetailsRepository.findByUsersId_Id(memberId);
            if (!memberOpt.isPresent()) {
                System.err.println("CM 회수 처리 실패: 회원을 찾을 수 없습니다. " + memberId);
                return false;
            }
            UserTesseris member = memberOpt.get();
            
            // 2. CM 회수 처리 (레코드가 없으면 INSERT, 있으면 UPDATE)
            int insertedRows = ajgMemberAssetDetailsRepository.insertCmWithdrawal(memberId, amount);
            int updatedRows = ajgMemberAssetDetailsRepository.updateCmWithdrawal(memberId, amount);
            
            // INSERT 또는 UPDATE 중 하나라도 성공하면 처리 완료
            if (insertedRows > 0 || updatedRows > 0) {
                System.out.println("CM 회수 처리 완료: " + memberId + ", 금액: " + amount);
                
                // 3. 거래 내역 기록
                boolean logResult = createCollectionLog(member, amount, reason);
                if (!logResult) {
                    System.err.println("거래 내역 기록 실패");
                    return false;
                }
                
                return true;
            } else {
                System.err.println("CM 회수 처리 실패: 회원을 찾을 수 없습니다. " + memberId);
                return false;
            }
        } catch (Exception e) {
            System.err.println("CM 회수 처리 중 오류: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 회수 거래 내역 기록
     */
    private boolean createCollectionLog(UserTesseris member, Integer amount, String reason) {
        try {
            // 1. 회수 관련 인덱스 조회
            Optional<UserCmLogPayment> paymentOpt = userCmLogPaymentRepository.findByUserCmLogPaymentName("출금");
            Optional<UserCmLogTransactionType> transactionOpt = userCmLogTransactionTypeRepository.findByUserCmLogTransactionTypeName("본사회수(CM)");
            Optional<UserCmLogValueType> valueTypeOpt = userCmLogValueTypeRepository.findByUserCmLogValueTypeName("CM");
            
            if (!paymentOpt.isPresent() || !transactionOpt.isPresent() || !valueTypeOpt.isPresent()) {
                System.err.println("거래 내역 기록 실패: 필요한 인덱스를 찾을 수 없습니다.");
                return false;
            }
            
            // 2. UserCmLog 엔티티 생성
            UserCmLog userCmLog = UserCmLog.builder()
                .userCmLogPaymentIndex(paymentOpt.get().getUserCmLogPaymentIndex())
                .userCmLogTransactionTypeIndex(transactionOpt.get().getUserCmLogTransactionTypeIndex())
                .userCmLogValueTypeIndex(valueTypeOpt.get().getUserCmLogValueTypeIndex())
                .userIndexEventTrigger(null) // TODO: 로그인한 관리자 정보 필요
                .userIndexEventParty(member)
                .userCmLogValue(amount)
                .userCmLogReason(reason)
                .userCmLogCreateTime(LocalDateTime.now())
                .build();
            
            // 3. 로그 저장
            userCmLogRepository.save(userCmLog);
            
            System.out.println("거래 내역 기록 완료: " + member.getUsersId().getId() + ", 금액: " + amount);
            return true;
            
        } catch (Exception e) {
            System.err.println("거래 내역 기록 중 오류: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 다중 CM 지급 처리
     * 각 회원마다 개별 트랜잭션과 로그 발생
     */
    public List<Map<String, Object>> processBulkPayment(List<Map<String, Object>> members, Integer amount, String reason) {
        return members.stream().map(member -> {
            try {
                String memberId = (String) member.get("memberId");
                Integer currentCmHeld = (Integer) member.get("currentCmHeld");
                
                System.out.println("다중 CM 지급 처리 시작: " + memberId + ", 금액: " + amount);
                
                // 각 회원별로 개별 트랜잭션 처리
                boolean result = processPayment(memberId, amount, reason, currentCmHeld);
                
                System.out.println("다중 CM 지급 처리 완료: " + memberId + ", 결과: " + result);
                
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("memberId", memberId);
                resultMap.put("success", result);
                resultMap.put("message", result ? "처리 완료" : "처리 실패");
                return resultMap;
            } catch (Exception e) {
                String memberId = (String) member.get("memberId");
                System.err.println("다중 CM 지급 처리 중 오류: " + memberId + ", " + e.getMessage());
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("memberId", memberId);
                resultMap.put("success", false);
                resultMap.put("message", "처리 중 오류 발생: " + e.getMessage());
                return resultMap;
            }
        }).collect(Collectors.toList());
    }
    
    /**
     * 다중 CM 회수 처리
     * 각 회원마다 개별 트랜잭션과 로그 발생
     */
    public List<Map<String, Object>> processBulkCollection(List<Map<String, Object>> members, Integer amount, String reason) {
        return members.stream().map(member -> {
            try {
                String memberId = (String) member.get("memberId");
                Integer currentCmHeld = (Integer) member.get("currentCmHeld");
                
                System.out.println("다중 CM 회수 처리 시작: " + memberId + ", 금액: " + amount);
                
                // 각 회원별로 개별 트랜잭션 처리
                boolean result = processCollection(memberId, amount, reason, currentCmHeld);
                
                System.out.println("다중 CM 회수 처리 완료: " + memberId + ", 결과: " + result);
                
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("memberId", memberId);
                resultMap.put("success", result);
                resultMap.put("message", result ? "처리 완료" : "처리 실패");
                return resultMap;
            } catch (Exception e) {
                String memberId = (String) member.get("memberId");
                System.err.println("다중 CM 회수 처리 중 오류: " + memberId + ", " + e.getMessage());
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("memberId", memberId);
                resultMap.put("success", false);
                resultMap.put("message", "처리 중 오류 발생: " + e.getMessage());
                return resultMap;
            }
        }).collect(Collectors.toList());
    }
    
    /**
     * 전체 트랜잭션 방식의 다중 CM 지급 처리
     * 모든 회원이 성공해야만 커밋, 하나라도 실패하면 전체 롤백
     */
    @Transactional
    public BulkPaymentResult processBulkPaymentWithFullTransaction(List<Map<String, Object>> members, Integer amount, String reason) {
        String transactionId = UUID.randomUUID().toString();
        List<PaymentResult> results = new ArrayList<>();
        List<String> memberIds = new ArrayList<>();
        
        System.out.println("=== 전체 트랜잭션 CM 지급 시작 ===");
        System.out.println("트랜잭션 ID: " + transactionId);
        System.out.println("처리할 회원 수: " + members.size());
        System.out.println("지급 금액: " + amount);
        System.out.println("사유: " + reason);
        
        try {
            // 1단계: 모든 회원 검증
            System.out.println("=== 1단계: 회원 검증 시작 ===");
            for (Map<String, Object> member : members) {
                String memberId = (String) member.get("memberId");
                memberIds.add(memberId);
                
                // 회원 존재 여부 확인
                Optional<UserTesseris> memberOpt = ajgMemberAssetDetailsRepository.findByUsersId_Id(memberId);
                if (!memberOpt.isPresent()) {
                    String errorMsg = "회원을 찾을 수 없습니다: " + memberId;
                    System.err.println(errorMsg);
                    throw new RuntimeException(errorMsg);
                }
                
                System.out.println("회원 검증 완료: " + memberId);
            }
            
            // 2단계: 모든 회원 처리
            System.out.println("=== 2단계: CM 지급 처리 시작 ===");
            for (Map<String, Object> member : members) {
                String memberId = (String) member.get("memberId");
                Integer currentCmHeld = (Integer) member.get("currentCmHeld");
                
                System.out.println("회원 처리 시작: " + memberId);
                
                // 개별 회원 처리
                boolean result = processPayment(memberId, amount, reason, currentCmHeld);
                
                if (result) {
                    results.add(new PaymentResult(memberId, true, "처리 완료"));
                    System.out.println("회원 처리 성공: " + memberId);
                } else {
                    String errorMsg = "회원 처리 실패: " + memberId;
                    System.err.println(errorMsg);
                    throw new RuntimeException(errorMsg);
                }
            }
            
            // 3단계: 감사 로그 생성
            System.out.println("=== 3단계: 감사 로그 생성 ===");
            createBulkPaymentAuditLog(transactionId, memberIds, amount, reason, "SUCCESS", results);
            
            System.out.println("=== 전체 트랜잭션 CM 지급 완료 ===");
            return new BulkPaymentResult(true, "모든 CM 지급 처리 완료", results);
            
        } catch (Exception e) {
            // 전체 롤백
            System.err.println("=== 전체 트랜잭션 롤백 ===");
            System.err.println("실패 원인: " + e.getMessage());
            
            // 실패 감사 로그 생성
            createBulkPaymentAuditLog(transactionId, memberIds, amount, reason, "FAILED", results);
            
            throw new RuntimeException("CM 지급 처리 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * 전체 트랜잭션 방식의 다중 CM 회수 처리
     * 모든 회원이 성공해야만 커밋, 하나라도 실패하면 전체 롤백
     */
    @Transactional
    public BulkPaymentResult processBulkCollectionWithFullTransaction(List<Map<String, Object>> members, Integer amount, String reason) {
        String transactionId = UUID.randomUUID().toString();
        List<PaymentResult> results = new ArrayList<>();
        List<String> memberIds = new ArrayList<>();
        
        System.out.println("=== 전체 트랜잭션 CM 회수 시작 ===");
        System.out.println("트랜잭션 ID: " + transactionId);
        System.out.println("처리할 회원 수: " + members.size());
        System.out.println("회수 금액: " + amount);
        System.out.println("사유: " + reason);
        
        try {
            // 1단계: 모든 회원 검증
            System.out.println("=== 1단계: 회원 검증 시작 ===");
            for (Map<String, Object> member : members) {
                String memberId = (String) member.get("memberId");
                memberIds.add(memberId);
                
                // 회원 존재 여부 확인
                Optional<UserTesseris> memberOpt = ajgMemberAssetDetailsRepository.findByUsersId_Id(memberId);
                if (!memberOpt.isPresent()) {
                    String errorMsg = "회원을 찾을 수 없습니다: " + memberId;
                    System.err.println(errorMsg);
                    throw new RuntimeException(errorMsg);
                }
                
                System.out.println("회원 검증 완료: " + memberId);
            }
            
            // 2단계: 모든 회원 처리
            System.out.println("=== 2단계: CM 회수 처리 시작 ===");
            for (Map<String, Object> member : members) {
                String memberId = (String) member.get("memberId");
                Integer currentCmHeld = (Integer) member.get("currentCmHeld");
                
                System.out.println("회원 처리 시작: " + memberId);
                
                // 개별 회원 처리
                boolean result = processCollection(memberId, amount, reason, currentCmHeld);
                
                if (result) {
                    results.add(new PaymentResult(memberId, true, "처리 완료"));
                    System.out.println("회원 처리 성공: " + memberId);
                } else {
                    String errorMsg = "회원 처리 실패: " + memberId;
                    System.err.println(errorMsg);
                    throw new RuntimeException(errorMsg);
                }
            }
            
            // 3단계: 감사 로그 생성
            System.out.println("=== 3단계: 감사 로그 생성 ===");
            createBulkCollectionAuditLog(transactionId, memberIds, amount, reason, "SUCCESS", results);
            
            System.out.println("=== 전체 트랜잭션 CM 회수 완료 ===");
            return new BulkPaymentResult(true, "모든 CM 회수 처리 완료", results);
            
        } catch (Exception e) {
            // 전체 롤백
            System.err.println("=== 전체 트랜잭션 롤백 ===");
            System.err.println("실패 원인: " + e.getMessage());
            
            // 실패 감사 로그 생성
            createBulkCollectionAuditLog(transactionId, memberIds, amount, reason, "FAILED", results);
            
            throw new RuntimeException("CM 회수 처리 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * 다중 지급 감사 로그 생성
     */
    private void createBulkPaymentAuditLog(String transactionId, List<String> memberIds, Integer amount, String reason, String status, List<PaymentResult> results) {
        try {
            System.out.println("다중 지급 감사 로그 생성: " + transactionId);
            System.out.println("상태: " + status);
            System.out.println("처리된 회원 수: " + results.size());
            
            // 감사 로그 테이블에 저장 (실제 구현 시)
            // bulkPaymentAuditLogRepository.save(new BulkPaymentAuditLog(transactionId, memberIds, amount, reason, status, results));
            
        } catch (Exception e) {
            System.err.println("감사 로그 생성 실패: " + e.getMessage());
        }
    }
    
    /**
     * 다중 회수 감사 로그 생성
     */
    private void createBulkCollectionAuditLog(String transactionId, List<String> memberIds, Integer amount, String reason, String status, List<PaymentResult> results) {
        try {
            System.out.println("다중 회수 감사 로그 생성: " + transactionId);
            System.out.println("상태: " + status);
            System.out.println("처리된 회원 수: " + results.size());
            
            // 감사 로그 테이블에 저장 (실제 구현 시)
            // bulkCollectionAuditLogRepository.save(new BulkCollectionAuditLog(transactionId, memberIds, amount, reason, status, results));
            
        } catch (Exception e) {
            System.err.println("감사 로그 생성 실패: " + e.getMessage());
        }
    }
    
    /**
     * 개별 회원 처리 결과
     */
    public static class PaymentResult {
        private String memberId;
        private boolean success;
        private String message;
        
        public PaymentResult(String memberId, boolean success, String message) {
            this.memberId = memberId;
            this.success = success;
            this.message = message;
        }
        
        // Getters
        public String getMemberId() { return memberId; }
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
    
    /**
     * 전체 다중 처리 결과
     */
    public static class BulkPaymentResult {
        private boolean overallSuccess;
        private String message;
        private List<PaymentResult> results;
        private int totalCount;
        private int successCount;
        private int failureCount;
        
        public BulkPaymentResult(boolean overallSuccess, String message, List<PaymentResult> results) {
            this.overallSuccess = overallSuccess;
            this.message = message;
            this.results = results;
            this.totalCount = results.size();
            this.successCount = (int) results.stream().filter(r -> r.isSuccess()).count();
            this.failureCount = totalCount - successCount;
        }
        
        // Getters
        public boolean isOverallSuccess() { return overallSuccess; }
        public String getMessage() { return message; }
        public List<PaymentResult> getResults() { return results; }
        public int getTotalCount() { return totalCount; }
        public int getSuccessCount() { return successCount; }
        public int getFailureCount() { return failureCount; }
    }
} 