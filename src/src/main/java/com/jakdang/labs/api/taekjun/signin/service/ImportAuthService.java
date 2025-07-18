package com.jakdang.labs.api.taekjun.signin.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;

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
    
    // 환경 변수에서 API 키를 가져오도록 수정
    @Value("${iamport.api.key:4455178272056543}")
    private String iamportApiKey;
    
    @Value("${iamport.api.secret:fk2aWqHrtsSIFdTfDnyA6hm3esat0q1NBNXCGXfCGUIxFNgb8l5lrlVRpOl2QSk7HIsa3GMaMynDbP9X}")
    private String iamportApiSecret;
    
    /**
     * 아임포트 본인인증 OTP 요청
     */
    public String generateAuthToken(String name, String phone, String birth, String genderDigit, String carrier) {
        try {
            // 먼저 Access Token을 발급받습니다
            String accessToken = getImportAccessToken();
            if (accessToken == null) {
                log.error("아임포트 Access Token 발급 실패");
                return null;
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);
            
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
            log.error("API Key: {}", iamportApiKey);
        }
        
        return null;
    }
    
    /**
     * 아임포트 본인인증 OTP 완료
     */
    public boolean verifyAuthResult(String impUid, String otp) {
        try {
            // 먼저 Access Token을 발급받습니다
            String accessToken = getImportAccessToken();
            if (accessToken == null) {
                log.error("아임포트 Access Token 발급 실패");
                return false;
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);
            
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
            // 먼저 Access Token을 발급받습니다
            String accessToken = getImportAccessToken();
            if (accessToken == null) {
                log.error("아임포트 Access Token 발급 실패");
                return null;
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);
            
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
     * 아임포트 Access Token 획득
     */
    private String getImportAccessToken() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("imp_key", iamportApiKey);
            requestBody.put("imp_secret", iamportApiSecret);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            log.info("아임포트 Access Token 요청 URL: {}", IMPORT_API_URL + "/users/getToken");
            log.info("아임포트 Access Token 요청 본문: {}", requestBody);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                IMPORT_API_URL + "/users/getToken", request, Map.class);
            
            log.info("아임포트 Access Token 응답: {}", response.getBody());
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                Map<String, Object> responseData = (Map<String, Object>) responseBody.get("response");
                if (responseData != null) {
                    return (String) responseData.get("access_token");
                }
            }
            
        } catch (Exception e) {
            log.error("아임포트 Access Token 발급 실패: ", e);
        }
        
        return null;
    }
} 