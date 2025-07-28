package com.jakdang.labs.api.jungeun.service;

import org.springframework.stereotype.Service;

import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.jungeun.repository.BusinessManLjeRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrokerageFeeSvc {
    private final BusinessManLjeRepo businessManRepo;

    // public ResponseDTO<?> getBrokerageFee(Integer user_index){
    //     Object[] result = (Object[]) businessManRepo.getBrokerageFee(user_index);
    //     if(result == null){
    //         return ResponseDTO.createErrorResponse(500, "중개수수료 조회 실패");
    //     }
    //     BrokerageFeeDTO dto = BrokerageFeeDTO.builder()
    //     Integer businessGradeIndex = businessManRepo.findBusinessGradeIndexByUserIndex(user_index);
    //     return ResponseDTO.createSuccessResponse("중개수수료 조회 성공", businessGradeIndex);
    // }
}
