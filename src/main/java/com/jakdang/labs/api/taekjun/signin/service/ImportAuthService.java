package com.jakdang.labs.api.taekjun.signin.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportAuthService {
    
    private final RestTemplate restTemplate;
    
    // 아임포트 본인인증 API 설정
    private static final String IMPORT_API_URL = "https://api.iamport.kr";
    private static final String CHANNEL_KEY = "channel-key-67513beb-e9a5-40cb-aaff-3bab502d85b9";
    
    /**
     * 아임포트 본인인증 OTP 요청
     */
    public String generateAuthToken(String name, String phone, String birth, String genderDigit, String carrier) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // 아임포트는 일반적으로 API Key를 헤더에 포함
            headers.set("Authorization", CHANNEL_KEY);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("name", name);
            requestBody.put("phone", phone);
            requestBody.put("birth", birth);
            requestBody.put("gender_digit", genderDigit);
            requestBody.put("carrier", carrier);
            requestBody.put("verification_method", "SMS");
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            log.info("아임포트 API 요청 URL: {}", IMPORT_API_URL + "/certifications/otp/request");
            log.info("아임포트 API 요청 헤더: {}", headers);
            log.info("아임포트 API 요청 본문: {}", requestBody);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                IMPORT_API_URL + "/certifications/otp/request", request, Map.class);
            
            log.info("아임포트 API 응답 상태: {}", response.getStatusCode());
            log.info("아임포트 API 응답 내용: {}", response.getBody());
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                // 아임포트 응답에서 imp_uid 반환
                return (String) responseBody.get("imp_uid");
            }
            
        } catch (Exception e) {
            log.error("아임포트 인증 OTP 요청 실패: ", e);
            log.error("API URL: {}", IMPORT_API_URL);
            log.error("Channel Key: {}", CHANNEL_KEY);
        }
        
        return null;
    }
    
    /**
     * 아임포트 본인인증 OTP 완료
     */
    public boolean verifyAuthResult(String impUid, String otp) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + CHANNEL_KEY);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("imp_uid", impUid);
            requestBody.put("otp", otp);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                IMPORT_API_URL + "/certifications/otp/complete", request, Map.class);
            
            log.info("아임포트 OTP 완료 응답: {}", response.getBody());
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                String resultCode = (String) responseBody.get("code");
                return "0".equals(resultCode); // 성공 코드
            }
            
        } catch (Exception e) {
            log.error("아임포트 OTP 완료 실패: ", e);
        }
        
        return false;
    }
    
    /**
     * 아임포트 본인인증 상태 조회
     */
    public Map<String, Object> getAuthStatus(String impUid) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + CHANNEL_KEY);
            
            ResponseEntity<Map> response = restTemplate.getForEntity(
                IMPORT_API_URL + "/certifications/" + impUid, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            
        } catch (Exception e) {
            log.error("아임포트 인증 상태 조회 실패: ", e);
        }
        
        return null;
    }
    
    /**
     * 아임포트 Access Token 획득 (필요시)
     */
    private String getImportAccessToken() {
        // 아임포트는 channelKey로 인증하므로 별도 토큰 불필요
        return "IMPORT_CHANNEL_KEY_AUTH";
    }
} 