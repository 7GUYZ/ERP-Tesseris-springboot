package com.jakdang.labs.api.jungeun.service;

import org.springframework.stereotype.Service;

import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.jungeun.dto.BrokerageFeeDTO;
import com.jakdang.labs.api.jungeun.repository.BusinessManLjeRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrokerageFeeSvc {
    private final BusinessManLjeRepo businessManRepo;

    public ResponseDTO<?> getBrokerageFee(Integer user_index){
        Object result = businessManRepo.getBrokerageFee(user_index);
        if(result == null){
            return ResponseDTO.createErrorResponse(500, "중개수수료 조회 실패");
        }
        
        try {
            log.info("Result type: {}, Value: {}", result.getClass().getName(), result);
            Object[] resultArray = (Object[]) result;
            
            // 2차원 배열 처리
            Object[] dataArray = (Object[]) resultArray[0];
            
            // 각 요소의 타입 확인
            for(int i = 0; i < dataArray.length; i++) {
                log.info("Index {}: Type={}, Value={}", i, 
                    dataArray[i] != null ? dataArray[i].getClass().getName() : "null", 
                    dataArray[i]);
            }
            
            BrokerageFeeDTO dto = BrokerageFeeDTO.builder()
            .cmValueTotalSum(dataArray[0] == null ? null : convertToInteger(dataArray[0]))
            .cmValueChargeSum(dataArray[1] == null ? null : convertToInteger(dataArray[1]))
            .cmValueWaitSum(dataArray[2] == null ? null : convertToInteger(dataArray[2]))
            .cmValueYesSum(dataArray[3] == null ? null : convertToInteger(dataArray[3]))
            .build();
            return ResponseDTO.createSuccessResponse("중개수수료 조회 성공", dto);
        } catch (Exception e) {
            log.error("중개수수료 데이터 변환 중 오류: {}", e.getMessage());
            return ResponseDTO.createErrorResponse(500, "중개수수료 데이터 처리 실패");
        }
    }
    
    private Integer convertToInteger(Object obj) {
        if (obj == null) return null;
        
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        } else if (obj instanceof String) {
            return Integer.parseInt((String) obj);
        } else {
            log.warn("Unexpected type: {} for value: {}", obj.getClass().getName(), obj);
            return null;
        }
    }
}
