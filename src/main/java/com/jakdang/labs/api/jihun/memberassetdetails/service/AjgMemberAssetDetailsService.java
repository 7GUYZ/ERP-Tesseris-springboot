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
        
        return ajgMemberAssetDetailsRepository.findMemberAssetDetails(
            searchDto.getUserId(),
            searchDto.getUserName(),
            searchDto.getUserPhone(),
            searchDto.getUserRoleIndex(),
            pageable
        );
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