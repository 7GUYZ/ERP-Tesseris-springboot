package com.jakdang.labs.api.deokkyu.storeRegister.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakdang.labs.api.deokkyu.storeRegister.dto.StoreRegisterRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreRegisterService {
    
    private final ObjectMapper objectMapper;
    
    /**
     * 가맹점 신청 등록
     * @param storeData 가맹점 신청 데이터 (JSON 문자열)
     * @param files 첨부 파일들
     * @return 등록 결과
     */
    @Transactional
    public Map<String, Object> registerStore(String storeData, MultipartFile[] files) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("가맹점 신청 등록 처리 시작");
            
            // JSON 문자열을 DTO로 변환
            StoreRegisterRequestDto storeRegisterDto = objectMapper.readValue(storeData, StoreRegisterRequestDto.class);
            
            // 기본 정보 설정
            storeRegisterDto.setStatus("PENDING"); // 대기 상태로 설정
            storeRegisterDto.setCreatedAt(LocalDateTime.now());
            
            log.info("변환된 가맹점 신청 데이터: {}", storeRegisterDto);
            
            // 첨부 파일 처리
            if (files != null && files.length > 0) {
                log.info("첨부 파일 개수: {}", files.length);
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        log.info("파일명: {}, 크기: {} bytes", file.getOriginalFilename(), file.getSize());
                        // TODO: 파일 저장 로직 구현 필요
                        // FileService나 FileController를 활용하여 파일 저장 처리
                    }
                }
            }
            
            // TODO: 데이터베이스에 가맹점 신청 정보 저장
            // storeRegisterRepository.save(storeRegisterEntity);
            
            response.put("success", true);
            response.put("message", "가맹점 신청이 성공적으로 등록되었습니다");
            response.put("storeId", "TEMP_STORE_ID"); // 임시 ID, 실제로는 저장된 엔티티의 ID
            response.put("status", storeRegisterDto.getStatus());
            
            log.info("가맹점 신청 등록 완료");
            return response;
            
        } catch (Exception e) {
            log.error("가맹점 신청 등록 실패: {}", e.getMessage());
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }
    }
} 