package com.jakdang.labs.api.taekjun.address.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAddressService {
    
    private final RestTemplate restTemplate;
    
    @Value("${kakao.api.key}")
    private String kakaoApiKey;
    
    private static final String KAKAO_ADDRESS_API_URL = "https://dapi.kakao.com/v2/local/search/address.json";
    
    /**
     * 카카오 주소 검색 API 호출
     */
    public Map<String, Object> searchAddress(String query) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoApiKey);
            
            String url = KAKAO_ADDRESS_API_URL + "?query=" + query;
            
            log.info("카카오 주소 검색 요청 - query: {}", query);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                new HttpEntity<>(headers), 
                Map.class
            );
            
            log.info("카카오 주소 검색 응답 상태: {}", response.getStatusCode());
            log.info("카카오 주소 검색 응답 내용: {}", response.getBody());
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            
            return null;
            
        } catch (Exception e) {
            log.error("카카오 주소 검색 중 오류 발생: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 카카오 키워드 검색 API 호출 (상세 주소 검색용)
     */
    public Map<String, Object> searchKeyword(String query) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoApiKey);
            
            String url = "https://dapi.kakao.com/v2/local/search/keyword.json?query=" + query;
            
            log.info("카카오 키워드 검색 요청 - query: {}", query);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                new HttpEntity<>(headers), 
                Map.class
            );
            
            log.info("카카오 키워드 검색 응답 상태: {}", response.getStatusCode());
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            
            return null;
            
        } catch (Exception e) {
            log.error("카카오 키워드 검색 중 오류 발생: {}", e.getMessage(), e);
            return null;
        }
    }
} 