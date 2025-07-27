package com.jakdang.labs.api.dabin.FrontEventCouponList.service;

import com.jakdang.labs.api.dabin.FrontEventCouponList.dto.CouponDetailResponse;
import com.jakdang.labs.api.dabin.FrontEventCouponList.repository.FrontEventCouponRepository;
import com.jakdang.labs.security.jwt.utils.JwtUtil;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.UserTesserisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CouponService {

    @Autowired
    @Qualifier("frontEventCouponJdbRepo")
    private FrontEventCouponRepository couponRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserTesserisRepository userTesserisRepository;

    public CouponDetailResponse getCouponDetail(Long couponIssuanceIndex, String authHeader) {
        log.info("🔍 CouponService.getCouponDetail 시작: couponIssuanceIndex={}", couponIssuanceIndex);
        try {
            // JWT 토큰에서 사용자 ID 추출
            String token = authHeader.replace("Bearer ", "");
            String userId = jwtUtil.getUserId(token);
            log.info("🔍 JWT에서 추출한 userId: {}", userId);
            
            // userId로 userIndex 조회
            var userTesseris = userTesserisRepository.findByUsersId_Id(userId)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다: " + userId));
            Integer currentUserIndex = userTesseris.getUserIndex();
            log.info("🔍 DB에서 조회한 userIndex: {}", currentUserIndex);
            
            // Repository를 사용하여 쿠폰 상세 정보 조회
            log.info("🔍 쿠폰 조회 시도: userIndex={}, couponIndex={}", currentUserIndex, couponIssuanceIndex);
            Optional<Object> result = couponRepository.findCouponDetailByUserIndexAndCouponIndex(
                currentUserIndex, 
                couponIssuanceIndex.intValue()
            );
            
            if (result.isEmpty()) {
                log.warn("⚠️ 쿠폰을 찾을 수 없음: userIndex={}, couponIndex={}", currentUserIndex, couponIssuanceIndex);
                return null;
            }
            
            Object[] row = (Object[]) result.get();
            log.info("🔍 조회된 쿠폰 데이터: {}", row);
            log.info("🔍 배열 길이: {}", row.length);
            
            // 각 요소별 로깅
            for (int i = 0; i < row.length; i++) {
                log.info("🔍 인덱스 {}: {} (타입: {})", i, row[i], row[i] != null ? row[i].getClass().getSimpleName() : "null");
            }
            
            // 배열 길이 확인
            if (row.length < 10) {
                log.error("❌ 쿼리 결과 배열 길이가 부족함: length={}", row.length);
                log.error("❌ 예상 길이: 10, 실제 길이: {}", row.length);
                return null;
            }
            
            return CouponDetailResponse.builder()
                .couponName((String) row[0])
                .couponPrice((Integer) row[1])
                .couponLimit((Integer) row[2])
                .couponIssuanceTime((java.time.LocalDateTime) row[3])
                .couponIssuanceStatus((String) row[4])
                .providedUserIndex(row[5] != null ? ((Integer) row[5]).longValue() : null)
                .couponProvidedStatus((String) row[6])
                .couponProvidedTime((java.time.LocalDateTime) row[7])
                .couponLimitTime((java.time.LocalDateTime) row[8])
                .userName((String) row[9])
                .build();
                
        } catch (Exception e) {
            log.error("❌ 쿠폰 상세보기 오류: ", e);
            return null;
        }
    }
} 