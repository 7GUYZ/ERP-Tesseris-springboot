package com.jakdang.labs.api.jihun.memberassetdetails.service;

import com.jakdang.labs.api.jihun.memberassetdetails.dto.MemberAssetDetailsResponseDto;
import com.jakdang.labs.api.jihun.memberassetdetails.dto.MemberAssetDetailsSearchDto;
import com.jakdang.labs.api.jihun.memberassetdetails.repository.AjgMemberAssetDetailsRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AjgMemberAssetDetailsService {
    
    private final AjgMemberAssetDetailsRepository ajgMemberAssetDetailsRepository;
    
    public Page<MemberAssetDetailsResponseDto> searchMemberAssetDetails(MemberAssetDetailsSearchDto searchDto) {
        Pageable pageable = PageRequest.of(
            searchDto.getPage() != null ? searchDto.getPage() : 0,
            searchDto.getSize() != null ? searchDto.getSize() : 10
        );
        
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
        
        dto.setUserIndex((Integer) row[0]);
        dto.setUserId((String) row[1]);
        dto.setUserName((String) row[2]);
        dto.setUserPhone((String) row[3]);
        dto.setUserEmail((String) row[4]); // 이메일 필드 매핑
        dto.setUserRoleKorNm((String) row[5]);
        dto.setStoreName((String) row[6]);
        dto.setUserCmCurrent((String) row[7]);
        dto.setUserCmpCurrent((String) row[8]);
        dto.setUserCashCurrent((String) row[9]);
        
        // LocalDateTime 변환
        if (row[10] != null) {
            if (row[10] instanceof LocalDateTime) {
                dto.setUserCreateTime((LocalDateTime) row[10]);
            } else if (row[10] instanceof String) {
                try {
                    dto.setUserCreateTime(LocalDateTime.parse((String) row[10], DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                } catch (Exception e) {
                    dto.setUserCreateTime(null);
                }
            }
        }
        
        dto.setUserBankName((String) row[11]);
        dto.setUserBankNumber((String) row[12]);
        dto.setUserBankHolder((String) row[13]);
        dto.setUserJumin((String) row[14]);
        dto.setSuggestionUserId((String) row[15]);
        dto.setSuggestionUserName((String) row[16]);
        dto.setTemporaryStoreCashValue((String) row[17]);
        
        return dto;
    }
    
    public List<Map<String, Object>> getUserRoles() {
        List<Object[]> roles = ajgMemberAssetDetailsRepository.findUserRoles();
        return roles.stream()
            .map(role -> Map.of(
                "index", role[0],
                "name", role[1]
            ))
            .collect(Collectors.toList());
    }
    
    @Transactional
    public boolean processPayment(String memberId, Integer amount, String reason, Integer currentCmHeld) {
        try {
            System.out.println("CM 지급 처리: " + memberId + ", 금액: " + amount + ", 사유: " + reason);
            
            // CM 지급 처리 (deposit 증가)
            int updatedRows = ajgMemberAssetDetailsRepository.updateCmDeposit(memberId, amount);
            
            if (updatedRows > 0) {
                System.out.println("CM 지급 처리 완료: " + memberId + ", 금액: " + amount);
                // TODO: 거래 내역 기록 로직 추가
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
    
    @Transactional
    public boolean processCollection(String memberId, Integer amount, String reason, Integer currentCmHeld) {
        try {
            System.out.println("CM 회수 처리: " + memberId + ", 금액: " + amount + ", 사유: " + reason);
            
            // CM 회수 처리 (withdrawal 증가)
            int updatedRows = ajgMemberAssetDetailsRepository.updateCmWithdrawal(memberId, amount);
            
            if (updatedRows > 0) {
                System.out.println("CM 회수 처리 완료: " + memberId + ", 금액: " + amount);
                // TODO: 거래 내역 기록 로직 추가
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
} 