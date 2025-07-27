package com.jakdang.labs.api.dabin.FrontEventCouponList.service;

import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.dabin.FrontEventCouponList.dto.EventDetailResponse;
import com.jakdang.labs.api.dabin.FrontEventCouponList.dto.EventListResponse;
import com.jakdang.labs.api.dabin.FrontEventCouponList.repository.EventListDetailRepository;
import com.jakdang.labs.api.dabin.FrontEventCouponList.repository.EventListRepository;
import com.jakdang.labs.entity.EventMaster;
import com.jakdang.labs.api.dabin.FrontEventCouponRegistration.repository.EventMasterRepository;
import com.jakdang.labs.security.jwt.utils.JwtUtil;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.UserTesserisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventListService {
    
    private final EventListRepository eventListRepository;
    private final EventListDetailRepository eventListDetailRepository;
    private final EventMasterRepository eventMasterRepository;
    private final JwtUtil jwtUtil;
    private final UserTesserisRepository userTesserisRepository;
    
    /**
     * 진행중인 이벤트 목록 조회
     */
    public ResponseDTO<List<EventListResponse>> getActiveEvents() {
        try {
            List<Object[]> results = eventListRepository.findActiveEvents();
            
            List<EventListResponse> events = results.stream()
                .map(row -> EventListResponse.builder()
                    .eventMasterIndex(((Number) row[0]).intValue())
                    .eventMasterName((String) row[1])
                    .eventMasterCondition((String) row[2])
                    .totalCouponPrice(((Number) row[3]).longValue())
                    .storeAddress((String) row[4])
                    .storeName((String) row[5])
                    .eventMasterCount(((Number) row[6]).intValue())
                    .build())
                .collect(Collectors.toList());
            
            return ResponseDTO.<List<EventListResponse>>createSuccessResponse("진행중인 이벤트 목록 조회 성공", events);
        } catch (Exception e) {
            log.error("진행중인 이벤트 목록 조회 실패: {}", e.getMessage(), e);
            return (ResponseDTO<List<EventListResponse>>) ResponseDTO.createErrorResponse(500, "진행중인 이벤트 목록 조회에 실패했습니다.");
        }
    }
    
    /**
     * 종료된 이벤트 목록 조회
     */
    public ResponseDTO<List<EventListResponse>> getEndedEvents() {
        try {
            List<Object[]> results = eventListRepository.findEndedEvents();
            
            List<EventListResponse> events = results.stream()
                .map(row -> EventListResponse.builder()
                    .eventMasterIndex(((Number) row[0]).intValue())
                    .eventMasterName((String) row[1])
                    .eventMasterCondition((String) row[2])
                    .totalCouponPrice(0L) // 종료된 이벤트는 0
                    .storeAddress((String) row[4])
                    .storeName((String) row[5])
                    .eventMasterCount(((Number) row[6]).intValue())
                    .build())
                .collect(Collectors.toList());
            
            return ResponseDTO.<List<EventListResponse>>createSuccessResponse("종료된 이벤트 목록 조회 성공", events);
        } catch (Exception e) {
            log.error("종료된 이벤트 목록 조회 실패: {}", e.getMessage(), e);
            return (ResponseDTO<List<EventListResponse>>) ResponseDTO.createErrorResponse(500, "종료된 이벤트 목록 조회에 실패했습니다.");
        }
    }
    
    /**
     * 이벤트 상세 정보 조회
     */
    public ResponseDTO<EventDetailResponse> getEventDetail(Integer eventMasterIndex) {
        try {
            // 이벤트 마스터 정보 조회
            EventMaster eventMaster = eventMasterRepository.findById(eventMasterIndex)
                .orElseThrow(() -> new RuntimeException("이벤트를 찾을 수 없습니다: " + eventMasterIndex));
            
            // 이벤트가 종료되었는지 확인
            if (eventMaster.getEventMasterCount() == 0) {
                return (ResponseDTO<EventDetailResponse>) ResponseDTO.createErrorResponse(400, "종료된 이벤트입니다.");
            }
            
            // 가맹점 정보 조회
            List<Object[]> storeResults = eventListDetailRepository.findStoreInfoByEventMasterUserIndex(
                eventMaster.getEventMasterUserIndex()
            );
            
            if (storeResults.isEmpty()) {
                return (ResponseDTO<EventDetailResponse>) ResponseDTO.createErrorResponse(404, "가맹점 정보를 찾을 수 없습니다.");
            }
            
            Object[] storeRow = storeResults.get(0);
            
            // 쿠폰 정보 조회
            List<Object[]> couponResults = eventListDetailRepository.findEventCoupons(
                eventMaster.getEventMasterUserIndex(), eventMasterIndex
            );
            
            if (couponResults.isEmpty()) {
                return (ResponseDTO<EventDetailResponse>) ResponseDTO.createErrorResponse(404, "쿠폰 정보를 찾을 수 없습니다.");
            }
            
            Object[] couponRow = couponResults.get(0);
            
            EventDetailResponse response = EventDetailResponse.builder()
                .storeIndex(((Number) storeRow[0]).intValue())
                .storeName((String) storeRow[1])
                .storePhone((String) storeRow[2])
                .storeAddress((String) storeRow[3])
                .storeCategoryName((String) storeRow[4])
                .userCmUse((String) storeRow[6])
                .storeImage((String) storeRow[7])
                .storeBusinessState(storeRow[8] != null ? storeRow[8].toString() : null)
                .storeTransactionStatus(storeRow[9] != null ? storeRow[9].toString() : null)
                .couponIndex(((Number) couponRow[0]).longValue())
                .couponName((String) couponRow[1])
                .couponPrice(((Number) couponRow[2]).intValue())
                .couponIssuanceStatus((String) couponRow[3])
                .couponIssuanceTime((LocalDateTime) couponRow[4])
                .couponLimit(((Number) couponRow[5]).intValue())
                .couponLimitTime((LocalDateTime) couponRow[6])
                .build();
            
            return ResponseDTO.<EventDetailResponse>createSuccessResponse("이벤트 상세 정보 조회 성공", response);
        } catch (Exception e) {
            log.error("이벤트 상세 정보 조회 실패: {}", e.getMessage(), e);
            return (ResponseDTO<EventDetailResponse>) ResponseDTO.createErrorResponse(500, "이벤트 상세 정보 조회에 실패했습니다.");
        }
    }

    /**
     * 쿠폰 다운로드 (사용자용)
     */
    @Transactional
    public ResponseDTO<String> downloadCoupon(Integer eventMasterIndex, Integer couponIndex, String authHeader) {
        try {
            // JWT 토큰에서 사용자 ID 추출
            String token = authHeader.replace("Bearer ", "");
            String userId = jwtUtil.getUserId(token);
            log.info("🔍 JWT에서 추출한 userId: {}", userId);
            
            // userId로 userIndex 조회
            var userTesseris = userTesserisRepository.findByUsersId_Id(userId)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다: " + userId));
            Integer userIndex = userTesseris.getUserIndex();
            log.info("🔍 DB에서 조회한 userIndex: {}", userIndex);
            
            // 1. 이벤트 상태 확인
            EventMaster eventMaster = eventMasterRepository.findById(eventMasterIndex)
                .orElseThrow(() -> new RuntimeException("이벤트를 찾을 수 없습니다: " + eventMasterIndex));
            
            if (eventMaster.getEventMasterCount() <= 0) {
                return (ResponseDTO<String>) ResponseDTO.createErrorResponse(400, "이미 종료된 이벤트입니다.");
            }
            
            // 2. 사용자 참여 횟수 확인 (임시로 생략)
            // TODO: Event_Attend 테이블에서 사용자 참여 횟수 조회 로직 추가
            
            // 3. 쿠폰 다운로드 처리 (임시로 성공 응답)
            // TODO: 실제 쿠폰 다운로드 로직 구현
            // - Event_Attend 테이블에 참여 기록 추가
            // - Coupon 테이블에서 쿠폰 상태 업데이트
            // - Event_Master 테이블에서 카운트 감소
            
            return ResponseDTO.<String>createSuccessResponse("쿠폰을 얻으셨습니다.", "success");
        } catch (Exception e) {
            log.error("쿠폰 다운로드 실패: {}", e.getMessage(), e);
            return (ResponseDTO<String>) ResponseDTO.createErrorResponse(500, "쿠폰 다운로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
} 