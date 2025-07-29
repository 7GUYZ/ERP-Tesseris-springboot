package com.jakdang.labs.api.deokkyu.storeRegister.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.Value;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeocodingService {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kakao.api.key}")
    private String KAKAO_API_KEY;
    
    
    private static final String KAKAO_GEOCODING_URL = "https://dapi.kakao.com/v2/local/search/address.json";
    
    /**
     * 주소를 위도/경도로 변환
     * @param address 변환할 주소
     * @return [위도, 경도] 배열 (실패시 null)
     */
    public double[] getLatLng(String address) {
        try {
            if (address == null || address.trim().isEmpty()) {
                log.warn("주소가 비어있습니다.");
                return null;
            }
            
            String encodedAddress = URLEncoder.encode(address.trim(), StandardCharsets.UTF_8);
            String url = KAKAO_GEOCODING_URL + "?query=" + encodedAddress;
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + KAKAO_API_KEY);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode documents = rootNode.get("documents");
                
                if (documents != null && documents.size() > 0) {
                    JsonNode firstResult = documents.get(0);
                    double lat = firstResult.get("y").asDouble(); // 위도
                    double lng = firstResult.get("x").asDouble(); // 경도
                    
                    log.info("주소 '{}' 의 좌표: 위도={}, 경도={}", address, lat, lng);
                    return new double[]{lat, lng};
                }
            }
            
            log.warn("주소 '{}' 에 대한 좌표를 찾을 수 없습니다.", address);
            return null;
            
        } catch (Exception e) {
            log.error("지오코딩 실패: {}", address, e);
            return null;
        }
    }
    
    /**
     * 위도/경도를 문자열로 반환
     * @param address 변환할 주소
     * @return [위도문자열, 경도문자열] 배열 (실패시 빈 문자열)
     */
    public String[] getLatLngAsString(String address) {
        double[] latLng = getLatLng(address);
        if (latLng != null) {
            return new String[]{String.valueOf(latLng[0]), String.valueOf(latLng[1])};
        }
        return new String[]{"", ""};
    }
} 