package com.jakdang.labs.api.taekjun.address.controller;

import com.jakdang.labs.api.taekjun.address.service.KakaoAddressService;
import com.jakdang.labs.api.common.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {
    
    private final KakaoAddressService kakaoAddressService;
    
    /**
     * 주소 검색 API
     */
    @GetMapping("/search")
    public ResponseDTO<?> searchAddress(@RequestParam String query) {
        try {
            log.info("주소 검색 요청 - query: {}", query);
            
            Map<String, Object> result = kakaoAddressService.searchAddress(query);
            
            if (result != null) {
                return ResponseDTO.createSuccessResponse("주소 검색 완료", result);
            } else {
                return ResponseDTO.createErrorResponse(404, "주소를 찾을 수 없습니다.");
            }
            
        } catch (Exception e) {
            log.error("주소 검색 중 오류 발생: {}", e.getMessage(), e);
            return ResponseDTO.createErrorResponse(500, "주소 검색 중 오류가 발생했습니다.");
        }
    }
    
    /**
     * 키워드 검색 API (상세 주소 검색용)
     */
    @GetMapping("/search/keyword")
    public ResponseDTO<?> searchKeyword(@RequestParam String query) {
        try {
            log.info("키워드 검색 요청 - query: {}", query);
            
            Map<String, Object> result = kakaoAddressService.searchKeyword(query);
            
            if (result != null) {
                return ResponseDTO.createSuccessResponse("키워드 검색 완료", result);
            } else {
                return ResponseDTO.createErrorResponse(404, "검색 결과를 찾을 수 없습니다.");
            }
            
        } catch (Exception e) {
            log.error("키워드 검색 중 오류 발생: {}", e.getMessage(), e);
            return ResponseDTO.createErrorResponse(500, "키워드 검색 중 오류가 발생했습니다.");
        }
    }
} 