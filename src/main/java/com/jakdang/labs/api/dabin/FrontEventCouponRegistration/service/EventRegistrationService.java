package com.jakdang.labs.api.dabin.FrontEventCouponRegistration.service;

import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.dabin.CmsCommissionManage.repository.CouponForEventRepository;
import com.jakdang.labs.api.dabin.CmsCouponManage.repository.CouponJdbRepo;
import com.jakdang.labs.api.dabin.FrontEventCouponRegistration.dto.CouponForEventResponse;
import com.jakdang.labs.api.dabin.FrontEventCouponRegistration.dto.EventRegistrationRequest;
import com.jakdang.labs.api.dabin.FrontEventCouponRegistration.repository.EventDetailRepository;
import com.jakdang.labs.api.dabin.FrontEventCouponRegistration.repository.EventMasterRepository;
import com.jakdang.labs.entity.Coupon;
import com.jakdang.labs.entity.EventDetail;
import com.jakdang.labs.entity.EventMaster;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventRegistrationService {
    
    private final EventMasterRepository eventMasterRepository;
    private final EventDetailRepository eventDetailRepository;
    private final CouponForEventRepository couponForEventRepository;
    private final CouponJdbRepo couponJdbRepo;
    
    /**
     * 사용 가능한 쿠폰 목록 조회
     */
    public ResponseDTO<List<CouponForEventResponse>> getAvailableCoupons(Long userIndex, Integer minPrice) {
        try {
            log.info("쿠폰 조회 요청: userIndex={}, minPrice={}", userIndex, minPrice);
            List<Object[]> results = couponForEventRepository.findAvailableCouponsForEvent(
                userIndex, minPrice != null ? minPrice : 0
            );
            log.info("쿼리 결과 개수: {}", results.size());
            
            // 쿼리 결과 상세 로깅
            for (int i = 0; i < results.size(); i++) {
                Object[] row = results.get(i);
                log.info("Row {}: couponIndex={}, couponName={}, couponPrice={}, status={}, issuanceTime={}, limit={}, limitTime={}", 
                    i, row[0], row[1], row[2], row[3], row[4], row[5], row[6]);
            }
            
            List<CouponForEventResponse> coupons = results.stream()
                .map(row -> {
                    CouponForEventResponse coupon = CouponForEventResponse.builder()
                        .couponIndex(((Number) row[0]).longValue())
                        .couponName((String) row[1])
                        .couponPrice((Integer) row[2])
                        .couponIssuanceStatus((String) row[3])
                        .couponIssuanceTime((LocalDateTime) row[4])
                        .couponLimit((Integer) row[5])
                        .couponLimitTime((LocalDateTime) row[6])
                        .build();
                    
                    // couponLimitTime 값 로깅
                    log.info("쿠폰 {} - couponLimitTime: {}", coupon.getCouponName(), coupon.getCouponLimitTime());
                    
                    return coupon;
                })
                .collect(Collectors.toList());
            
            log.info("변환된 쿠폰 목록: {}", coupons.stream().map(c -> c.getCouponPrice()).collect(Collectors.toList()));
            
            return ResponseDTO.<List<CouponForEventResponse>>createSuccessResponse("쿠폰 목록 조회 성공", coupons);
        } catch (Exception e) {
            log.error("쿠폰 목록 조회 실패: {}", e.getMessage(), e);
            return (ResponseDTO<List<CouponForEventResponse>>) ResponseDTO.createErrorResponse(500, "쿠폰 목록 조회에 실패했습니다.");
        }
    }
    
    /**
     * 이벤트 등록
     */
    @Transactional
    public ResponseDTO<String> registerEvent(EventRegistrationRequest request, Long userIndex) {
        try {
            // 유효성 검사
            if (request.getEventName() == null || request.getEventName().trim().isEmpty()) {
                return (ResponseDTO<String>) ResponseDTO.createErrorResponse(400, "이벤트 이름을 입력해주세요.");
            }
            
            if (request.getCouponIssuanceIndexList() == null || request.getCouponIssuanceIndexList().isEmpty()) {
                return (ResponseDTO<String>) ResponseDTO.createErrorResponse(400, "쿠폰을 선택해주세요.");
            }
            
            // 중복된 이벤트 이름 체크 (트랜잭션 시작 전에 미리 체크)
            String eventName = request.getEventName().trim();
            boolean exists = eventMasterRepository.existsByEventMasterName(eventName);
            if (exists) {
                return (ResponseDTO<String>) ResponseDTO.createErrorResponse(400, "이미 존재하는 이벤트 이름입니다. 다른 이름을 사용해주세요.");
            }
            
            // 1. 이벤트 마스터 생성
            Integer nextEventNum = eventMasterRepository.getNextEventMasterNum();
            
            EventMaster eventMaster = new EventMaster();
            eventMaster.setEventMasterName(eventName);
            eventMaster.setEventMasterCondition(request.getEventCondition());
            eventMaster.setEventMasterLimit(request.getEventDownLimit());
            eventMaster.setEventMasterCount(request.getCouponIssuanceIndexList().size());
            eventMaster.setEventMasterUserIndex(userIndex.intValue());
            eventMaster.setEventMasterNum(nextEventNum);
            
            EventMaster savedEventMaster = eventMasterRepository.save(eventMaster);
            
            // 2. 이벤트 상세 정보 생성
            for (Long couponIndex : request.getCouponIssuanceIndexList()) {
                // Coupon 엔티티 조회
                Coupon coupon = couponJdbRepo.findById(couponIndex.intValue())
                    .orElseThrow(() -> new RuntimeException("쿠폰을 찾을 수 없습니다: " + couponIndex));
                
                EventDetail eventDetail = new EventDetail();
                eventDetail.setEventMaster(savedEventMaster);
                eventDetail.setEventCoupon(coupon);
                
                eventDetailRepository.save(eventDetail);
            }
            
            return ResponseDTO.<String>createSuccessResponse("이벤트 등록 완료", "이벤트가 성공적으로 등록되었습니다.");
            
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // 데이터베이스 제약 조건 위반 (중복 키 등) - 백업 처리
            log.error("이벤트 등록 실패 (제약 조건 위반): {}", e.getMessage(), e);
            if (e.getMessage().contains("Duplicate entry") || e.getMessage().contains("evnet_master_name")) {
                return (ResponseDTO<String>) ResponseDTO.createErrorResponse(400, "이미 존재하는 이벤트 이름입니다. 다른 이름을 사용해주세요.");
            }
            return (ResponseDTO<String>) ResponseDTO.createErrorResponse(400, "이벤트 등록에 실패했습니다. 입력 정보를 확인해주세요.");
        } catch (Exception e) {
            log.error("이벤트 등록 실패: {}", e.getMessage(), e);
            return (ResponseDTO<String>) ResponseDTO.createErrorResponse(500, "이벤트 등록에 실패했습니다.");
        }
    }
    

} 