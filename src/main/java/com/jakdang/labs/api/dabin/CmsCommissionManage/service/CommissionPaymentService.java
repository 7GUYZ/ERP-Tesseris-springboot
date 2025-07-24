package com.jakdang.labs.api.dabin.CmsCommissionManage.service;


import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.dabin.CmsCommissionManage.dto.CommissionPaymentRequest;
import com.jakdang.labs.api.dabin.CmsCommissionManage.dto.CommissionPaymentResponse;
import com.jakdang.labs.api.dabin.CmsCommissionManage.dto.CommissionPaymentUpdateRequest;
import com.jakdang.labs.api.dabin.CmsCommissionManage.repository.CommissionPaymentJdbRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommissionPaymentService {
    
    private final CommissionPaymentJdbRepo commissionPaymentRepository;
    // private final PasswordEncoder passwordEncoder;
    
    public ResponseDTO<List<CommissionPaymentResponse>> searchCommissionPayments(CommissionPaymentRequest request) {
        
        try {
            // 날짜 파라미터 처리
            LocalDateTime chargeTimeStart = null;
            LocalDateTime chargeTimeEnd = null;
            
            if (request.getChargeTimeStart() != null && !request.getChargeTimeStart().isEmpty()) {
                chargeTimeStart = LocalDateTime.parse(request.getChargeTimeStart() + " 00:00:00", 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            if (request.getChargeTimeEnd() != null && !request.getChargeTimeEnd().isEmpty()) {
                chargeTimeEnd = LocalDateTime.parse(request.getChargeTimeEnd() + " 23:59:59", 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            
            List<CommissionPaymentResponse> results = commissionPaymentRepository.searchCommissionPayments(
                request.getUserId(),
                request.getUserName(),
                request.getUserPhone(),
                chargeTimeStart,
                chargeTimeEnd,
                request.getTransactionName(),
                request.getSuggestionUserId(),
                request.getSuggestionUserName(),
                request.getUserRoleIndex(),
                request.getPaymentStatus(),
                request.getDescription()
            );
            
            return ResponseDTO.<List<CommissionPaymentResponse>>builder()
                    .resultCode(200)
                    .data(results)
                    .resultMessage("수당 지급 내역 조회 성공")
                    .build();
                    
        } catch (Exception e) {
            log.error("수당 지급 내역 조회 중 오류 발생", e);
            return ResponseDTO.<List<CommissionPaymentResponse>>builder()
                    .resultCode(500)
                    .resultMessage("수당 지급 내역 조회 중 오류가 발생했습니다.")
                    .build();
        }
    }
    
    @Transactional
    public ResponseDTO<String> updatePaymentStatus(CommissionPaymentUpdateRequest request) {
        log.info("=== updatePaymentStatus 메서드 시작 ===");
        log.info("요청 파라미터: {}", request);
        try {
            // 관리자 비밀번호 검증 (실제 구현에서는 세션에서 관리자 정보를 가져와야 함)
            // String adminPassword = getCurrentAdminPassword();
            // if (!passwordEncoder.matches(request.getAdminPassword(), adminPassword)) {
            //     return ResponseDTO.<String>builder()
            //             .success(false)
            //             .message("관리자 비밀번호가 일치하지 않습니다.")
            //             .build();
            // }
            
            int successCount = 0;
            int failCount = 0;
            
            for (Integer detailIndex : request.getDetailIndexes()) {
                try {
                    log.info("=== detailIndex {} 처리 시작 ===", detailIndex);
                    
                    // 지급 자격 검증 상세 정보 조회
                    try {
                        Object[] eligibilityDetails = commissionPaymentRepository.getPaymentEligibilityDetails(detailIndex);
                        log.info("상세 정보 조회 결과: {}", eligibilityDetails);
                        
                        if (eligibilityDetails != null && eligibilityDetails.length >= 4) {
                            Integer userRoleIndex = (Integer) eligibilityDetails[0];
                            String userBankNumber = (String) eligibilityDetails[1];
                            Integer userBankIndex = (Integer) eligibilityDetails[2];
                            String userJumin = (String) eligibilityDetails[3];
                            
                            log.info("=== 지급 자격 검증 상세 정보 ===");
                            log.info("detailIndex: {}", detailIndex);
                            log.info("userRoleIndex: {}", userRoleIndex);
                            log.info("userBankNumber: '{}'", userBankNumber);
                            log.info("userBankIndex: {}", userBankIndex);
                            log.info("userJumin: '{}'", userJumin);
                            
                            // 실패 원인 분석
                            if (userRoleIndex != null && userRoleIndex == 1) {
                                log.warn("실패 원인: userRoleIndex가 1입니다 (관리자 권한)");
                            }
                            if (userBankNumber == null || userBankNumber.isEmpty()) {
                                log.warn("실패 원인: userBankNumber가 null이거나 빈 문자열입니다");
                            }
                            if (userBankIndex != null && userBankIndex == 0) {
                                log.warn("실패 원인: userBankIndex가 0입니다");
                            }
                            if (userJumin == null || userJumin.isEmpty()) {
                                log.warn("실패 원인: userJumin이 null이거나 빈 문자열입니다");
                            }
                        } else {
                            log.warn("상세 정보 조회 결과가 null이거나 부족함: {}", eligibilityDetails);
                        }
                        
                        // 상세 정보 출력
                        if (eligibilityDetails != null && eligibilityDetails.length >= 4) {
                            Integer userRoleIndex = (Integer) eligibilityDetails[0];
                            String userBankNumber = (String) eligibilityDetails[1];
                            Integer userBankIndex = (Integer) eligibilityDetails[2];
                            String userJumin = (String) eligibilityDetails[3];
                            
                            log.info("=== 지급 자격 검증 상세 정보 ===");
                            log.info("detailIndex: {}", detailIndex);
                            log.info("userRoleIndex: {}", userRoleIndex);
                            log.info("userBankNumber: '{}'", userBankNumber);
                            log.info("userBankIndex: {}", userBankIndex);
                            log.info("userJumin: '{}'", userJumin);
                            
                            // 실패 원인 분석
                            if (userRoleIndex != null && userRoleIndex == 1) {
                                log.warn("실패 원인: userRoleIndex가 1입니다 (관리자 권한)");
                            }
                            if (userBankNumber == null || userBankNumber.isEmpty()) {
                                log.warn("실패 원인: userBankNumber가 null이거나 빈 문자열입니다");
                            }
                            if (userBankIndex != null && userBankIndex == 0) {
                                log.warn("실패 원인: userBankIndex가 0입니다");
                            }
                            if (userJumin == null || userJumin.isEmpty()) {
                                log.warn("실패 원인: userJumin이 null이거나 빈 문자열입니다");
                            }
                        }
                    } catch (Exception e) {
                        log.error("상세 정보 조회 중 오류 발생: detailIndex={}", detailIndex, e);
                    }
                    
                    // 지급 자격 검증
                    if (!commissionPaymentRepository.validatePaymentEligibility(detailIndex)) {
                        failCount++;
                        log.warn("지급 자격 검증 실패: detailIndex={}", detailIndex);
                        continue;
                    }
                    
                    // 지급 상태 업데이트
                    commissionPaymentRepository.updatePaymentStatus(detailIndex, request.getPaymentStatus());
                    successCount++;
                    
                } catch (Exception e) {
                    failCount++;
                    log.error("지급 상태 업데이트 실패: detailIndex={}", detailIndex, e);
                }
            }
            
            String message = String.format("%d건 %s 완료", successCount, request.getPaymentStatus());
            if (failCount > 0) {
                message += String.format(", %d건 실패", failCount);
            }
            
            return ResponseDTO.<String>builder()
                    .resultCode(200)
                    .data(message)
                    .resultMessage(message)
                    .build();
                    
        } catch (Exception e) {
            log.error("지급 상태 업데이트 중 오류 발생", e);
            return ResponseDTO.<String>builder()
                    .resultCode(500)
                    .resultMessage("지급 상태 업데이트 중 오류가 발생했습니다.")
                    .build();
        }
    }
    
    public ResponseDTO<Boolean> validatePaymentEligibility(Integer detailIndex) {
        try {
            // 지급 자격 검증 상세 정보 조회
            Object[] eligibilityDetails = commissionPaymentRepository.getPaymentEligibilityDetails(detailIndex);
            if (eligibilityDetails != null && eligibilityDetails.length >= 4) {
                Integer userRoleIndex = (Integer) eligibilityDetails[0];
                String userBankNumber = (String) eligibilityDetails[1];
                Integer userBankIndex = (Integer) eligibilityDetails[2];
                String userJumin = (String) eligibilityDetails[3];
                
                log.info("=== 유효성 검사 상세 정보 ===");
                log.info("detailIndex: {}", detailIndex);
                log.info("userRoleIndex: {}", userRoleIndex);
                log.info("userBankNumber: '{}'", userBankNumber);
                log.info("userBankIndex: {}", userBankIndex);
                log.info("userJumin: '{}'", userJumin);
                
                // 실패 원인 분석
                if (userRoleIndex != null && userRoleIndex == 1) {
                    log.warn("실패 원인: userRoleIndex가 1입니다 (관리자 권한)");
                }
                if (userBankNumber == null || userBankNumber.isEmpty()) {
                    log.warn("실패 원인: userBankNumber가 null이거나 빈 문자열입니다");
                }
                if (userBankIndex != null && userBankIndex == 0) {
                    log.warn("실패 원인: userBankIndex가 0입니다");
                }
                if (userJumin == null || userJumin.isEmpty()) {
                    log.warn("실패 원인: userJumin이 null이거나 빈 문자열입니다");
                }
            }
            
            boolean isValid = commissionPaymentRepository.validatePaymentEligibility(detailIndex);
            
            return ResponseDTO.<Boolean>builder()
                    .resultCode(200)
                    .data(isValid)
                    .resultMessage(isValid ? "지급 가능" : "지급 불가능")
                    .build();
                    
        } catch (Exception e) {
            log.error("지급 자격 검증 중 오류 발생", e);
            return ResponseDTO.<Boolean>builder()
                    .resultCode(500)
                    .resultMessage("지급 자격 검증 중 오류가 발생했습니다.")
                    .build();
        }
    }
} 