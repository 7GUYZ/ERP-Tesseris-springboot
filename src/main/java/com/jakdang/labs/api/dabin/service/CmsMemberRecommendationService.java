package com.jakdang.labs.api.dabin.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jakdang.labs.api.dabin.dto.MemberRecommendationSearchRequestDto;
import com.jakdang.labs.api.dabin.dto.MemberRecommendationSearchResponseDto;
import com.jakdang.labs.api.dabin.dto.UserRoleDto;
import com.jakdang.labs.api.dabin.repository.MemberRecommendationJdbRepo;
import com.jakdang.labs.api.dabin.repository.UserRoleJdbRepo;

@Service
public class CmsMemberRecommendationService {
    @Autowired
    private MemberRecommendationJdbRepo memberRecommendationJdbRepo;
    @Autowired
    private UserRoleJdbRepo userRoleJdbRepo;

    // 회원 추천현황 검색
    public List<MemberRecommendationSearchResponseDto> searchMemberRecommendations(MemberRecommendationSearchRequestDto dto) {
        // 빈 문자열 파라미터를 null로 변환
        if (dto.getSuggestionUserId() != null && dto.getSuggestionUserId().isBlank()) {
            dto.setSuggestionUserId(null);
        }
        if (dto.getSuggestionUserName() != null && dto.getSuggestionUserName().isBlank()) {
            dto.setSuggestionUserName(null);
        }
        if (dto.getSuggestionStoreName() != null && dto.getSuggestionStoreName().isBlank()) {
            dto.setSuggestionStoreName(null);
        }
        // recommendationUserRole을 Integer로 변환
        Integer recommendationUserRole = null;
        if (dto.getRecommendationUserRole() != null && !dto.getRecommendationUserRole().equals("0")) {
            try {
                recommendationUserRole = Integer.parseInt(dto.getRecommendationUserRole());
            } catch (NumberFormatException e) {
                recommendationUserRole = null;
            }
        }
        if (dto.getUserName() != null && dto.getUserName().isBlank()) {
            dto.setUserName(null);
        }
        
        // 날짜 변환
        java.time.LocalDateTime joinDateStart = null;
        java.time.LocalDateTime joinDateEnd = null;
        
        if (dto.getJoinDateStart() != null) {
            joinDateStart = dto.getJoinDateStart().atStartOfDay();
        }
        if (dto.getJoinDateEnd() != null) {
            joinDateEnd = dto.getJoinDateEnd().atTime(23, 59, 59);
        }
        
        return memberRecommendationJdbRepo.searchMemberRecommendations(
            dto.getSuggestionUserId(),
            dto.getSuggestionUserName(),
            dto.getSuggestionUserRole(),
            recommendationUserRole,
            dto.getSuggestionStoreName(),
            joinDateStart,
            joinDateEnd,
            dto.getUserName()
        );
    }

    // 사용자 역할 리스트 (추천인 등급, 가입자 등급)
    public List<UserRoleDto> getUserRoleList() {
        return userRoleJdbRepo.findAll().stream()
            .filter(role -> role.getUserRoleIndex() != 4 && role.getUserRoleIndex() != 6)
            .map(role -> {
                UserRoleDto dto = new UserRoleDto();
                dto.setUserRoleIndex(role.getUserRoleIndex());
                dto.setUserRoleKorNm(role.getUserRoleKorNm());
                return dto;
            }).collect(Collectors.toList());
    }
} 