package com.jakdang.labs.api.jungeun.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.jungeun.dto.UserBusinessFilteredListDTO;
import com.jakdang.labs.api.jungeun.dto.UserBusinessListDTO;
import com.jakdang.labs.api.jungeun.repository.BusinessManLjeRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserBusinessListSvc {
    private final BusinessManLjeRepo businessManRepo;

    public ResponseDTO<List<UserBusinessListDTO>> getAvailableGrades(Integer user_index) {
        // 1. 내 business_grade_index 구하기
        Integer myGradeIndex = businessManRepo.findBusinessGradeIndexByUserIndex(user_index);
    
        // 2. 내 등급보다 높은 등급의 index, name 리스트 구하기
        List<Object[]> gradeList = businessManRepo.findGradeIndexAndNameGreaterThan(myGradeIndex);
    
        // 3. DTO로 변환
        List<UserBusinessListDTO> dtoList = gradeList.stream()
            .map(arr -> UserBusinessListDTO.builder()
                .gradeIndex((Integer) arr[0])
                .gradeName((String) arr[1])
                .build())
            .toList();
    
        return ResponseDTO.createSuccessResponse("선택 가능한 등급 리스트", dtoList);
    }

    public ResponseDTO<List<UserBusinessFilteredListDTO>> getFilteredBusinessList(Integer business_grade_index){
        List<Object[]> resultList = businessManRepo.findBusinessListWithBossEmail(business_grade_index);

        List<UserBusinessFilteredListDTO> dtoList = resultList.stream().map(arr -> 
        UserBusinessFilteredListDTO.builder()
                .userEmail((String) arr[0])
                .userName((String) arr[1])
                .gradeName((String) arr[2])
                .bossEmail((String) arr[3])
                .totalCm(arr[4] != null ? ((Number) arr[4]).longValue() : 0L)
                .storeCount(arr[5] != null ? ((Number) arr[5]).longValue() : 0L)
                .build()
        ).toList();

        return ResponseDTO.createSuccessResponse("선택 등급에 따른 사업자 리스트 불러오기 성공", dtoList);
    }
}
