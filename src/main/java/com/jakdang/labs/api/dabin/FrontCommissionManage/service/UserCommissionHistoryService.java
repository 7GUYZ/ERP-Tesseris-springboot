package com.jakdang.labs.api.dabin.FrontCommissionManage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jakdang.labs.api.dabin.FrontCommissionManage.dto.UserCommissionHistoryResponse;
import com.jakdang.labs.api.dabin.FrontCommissionManage.repository.UserCommissionHistoryJdbRepo;
import com.jakdang.labs.api.dabin.FrontMyPageStoreInfo.repository.UserTesserisJdbRepo;

import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class UserCommissionHistoryService {
    

    @Qualifier("userCommissionHistoryRepository")
    private final UserCommissionHistoryJdbRepo userCommissionHistoryRepository;
    
    private final UserTesserisJdbRepo userTesserisRepository;
    
    public Integer getUserIndexByUserId(String userId) {
        return userTesserisRepository.findByUsersId_Id(userId)
            .map(userTesseris -> userTesseris.getUserIndex())
            .orElseThrow(() -> new RuntimeException("UserTesseris not found for userId: " + userId));
    }
    
    public List<UserCommissionHistoryResponse> getUserCommissionHistory(Integer userIndex, Integer page, Integer limit) {
        int offset = (page - 1) * limit;
        Long totalCount = userCommissionHistoryRepository.getTotalCount(userIndex);
        
        List<Object[]> results = userCommissionHistoryRepository.getUserCommissionHistoryWithPagination(userIndex, limit, offset);
        
        // Object[]를 DTO로 변환 (쿼리 결과 순서에 맞게 수정)
        List<UserCommissionHistoryResponse> dtoResults = results.stream()
            .map(row -> {
                UserCommissionHistoryResponse dto = new UserCommissionHistoryResponse();
                dto.setUserName((String) row[0]);
                dto.setChargeDate(row[1] != null ? row[1].toString() : "");
                dto.setDescription((String) row[2]);
                dto.setCommissionAmount((Double) row[3]);
                dto.setPaymentStatus((String) row[4]);
                return dto;
            })
            .toList();
        
        // 순번 계산 (전체 개수에서 역순으로 계산)
        for (int i = 0; i < dtoResults.size(); i++) {
            UserCommissionHistoryResponse item = dtoResults.get(i);
            item.setRowNumber(totalCount.intValue() - offset - i);
        }
        
        return dtoResults;
    }
    
    public Long getTotalCount(Integer userIndex) {
        return userCommissionHistoryRepository.getTotalCount(userIndex);
    }
} 