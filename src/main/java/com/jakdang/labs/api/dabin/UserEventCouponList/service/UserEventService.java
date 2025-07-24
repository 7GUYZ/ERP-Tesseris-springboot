package com.jakdang.labs.api.dabin.UserEventCouponList.service;

import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.dabin.UserEventCouponList.dto.UserEventDetailDto;
import com.jakdang.labs.api.dabin.UserEventCouponList.dto.UserEventListDto;
import com.jakdang.labs.api.dabin.UserEventCouponList.repository.UserEventRepository;
import com.jakdang.labs.api.dabin.UserEventCouponList.repository.UserEventDetailRepository;
import com.jakdang.labs.api.dabin.UserEventCouponList.repository.UserEventJdbcRepository;
import com.jakdang.labs.entity.EventMaster;
import com.jakdang.labs.api.dabin.FrontEventCouponRegistration.repository.EventMasterRepository;
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
public class UserEventService {
    
    private final UserEventRepository userEventRepository;
    private final UserEventDetailRepository userEventDetailRepository;
    private final UserEventJdbcRepository userEventJdbcRepository;
    private final EventMasterRepository eventMasterRepository;
    
    /**
     * 진행중인 이벤트 목록 조회
     */
    public ResponseDTO<List<UserEventListDto>> getActiveEvents() {
        try {
            List<Object[]> results = userEventRepository.findActiveEvents();
            
            List<UserEventListDto> events = results.stream()
                .map(row -> UserEventListDto.builder()
                    .eventMasterIndex(((Number) row[0]).intValue())
                    .eventMasterName((String) row[1])
                    .eventMasterCondition((String) row[2])
                    .totalCouponPrice(((Number) row[3]).longValue())
                    .storeAddress((String) row[4])
                    .storeName((String) row[5])
                    .eventMasterCount(((Number) row[6]).intValue())
                    .build())
                .collect(Collectors.toList());
            
            return ResponseDTO.<List<UserEventListDto>>createSuccessResponse("진행중인 이벤트 목록 조회 성공", events);
        } catch (Exception e) {
            log.error("진행중인 이벤트 목록 조회 실패: {}", e.getMessage(), e);
            return (ResponseDTO<List<UserEventListDto>>) ResponseDTO.createErrorResponse(500, "진행중인 이벤트 목록 조회에 실패했습니다.");
        }
    }
    
    /**
     * 종료된 이벤트 목록 조회
     */
    public ResponseDTO<List<UserEventListDto>> getEndedEvents() {
        try {
            List<Object[]> results = userEventRepository.findEndedEvents();
            
            List<UserEventListDto> events = results.stream()
                .map(row -> UserEventListDto.builder()
                    .eventMasterIndex(((Number) row[0]).intValue())
                    .eventMasterName((String) row[1])
                    .eventMasterCondition((String) row[2])
                    .totalCouponPrice(0L) // 종료된 이벤트는 0
                    .storeAddress((String) row[4])
                    .storeName((String) row[5])
                    .eventMasterCount(((Number) row[6]).intValue())
                    .build())
                .collect(Collectors.toList());
            
            return ResponseDTO.<List<UserEventListDto>>createSuccessResponse("종료된 이벤트 목록 조회 성공", events);
        } catch (Exception e) {
            log.error("종료된 이벤트 목록 조회 실패: {}", e.getMessage(), e);
            return (ResponseDTO<List<UserEventListDto>>) ResponseDTO.createErrorResponse(500, "종료된 이벤트 목록 조회에 실패했습니다.");
        }
    }
    
    /**
     * 이벤트 상세 정보 조회
     */
    public ResponseDTO<UserEventDetailDto> getEventDetail(Integer eventMasterIndex) {
        try {
            // 이벤트 마스터 정보 조회
            EventMaster eventMaster = eventMasterRepository.findById(eventMasterIndex)
                .orElseThrow(() -> new RuntimeException("이벤트를 찾을 수 없습니다: " + eventMasterIndex));
            
            // 이벤트가 종료되었는지 확인
            log.info("이벤트 상세 조회 - eventMasterIndex: {}, eventMasterCount: {}", 
                    eventMasterIndex, eventMaster.getEventMasterCount());
            
            if (eventMaster.getEventMasterCount() == 0) {
                log.warn("종료된 이벤트입니다. eventMasterIndex: {}", eventMasterIndex);
                return (ResponseDTO<UserEventDetailDto>) ResponseDTO.createErrorResponse(400, "종료된 이벤트입니다.");
            }
            
            // 가맹점 정보 조회
            List<Object[]> storeResults = userEventDetailRepository.findStoreInfoByEventMasterUserIndex(
                eventMaster.getEventMasterUserIndex()
            );
            
            if (storeResults.isEmpty()) {
                return (ResponseDTO<UserEventDetailDto>) ResponseDTO.createErrorResponse(404, "가맹점 정보를 찾을 수 없습니다.");
            }
            
            Object[] storeRow = storeResults.get(0);
            
            // 쿠폰 정보 조회
            List<Object[]> couponResults = userEventDetailRepository.findEventCoupons(
                eventMaster.getEventMasterUserIndex(), eventMasterIndex
            );
            
            if (couponResults.isEmpty()) {
                return (ResponseDTO<UserEventDetailDto>) ResponseDTO.createErrorResponse(404, "쿠폰 정보를 찾을 수 없습니다.");
            }
            
            Object[] couponRow = couponResults.get(0);
            
            UserEventDetailDto response = UserEventDetailDto.builder()
                .storeIndex(((Number) storeRow[0]).intValue())
                .storeName((String) storeRow[1])
                .storePhone((String) storeRow[2])
                .storeAddress((String) storeRow[3])
                .storeCategoryName((String) storeRow[4])
                .userCmUse((String) storeRow[5])
                .storeImage((String) storeRow[6])
                .storeBusinessState(storeRow[7] != null ? storeRow[7].toString() : null)
                .storeTransactionStatus(storeRow[8] != null ? storeRow[8].toString() : null)
                .couponIndex(((Number) couponRow[0]).longValue())
                .couponName((String) couponRow[1])
                .couponPrice(((Number) couponRow[2]).intValue())
                .couponIssuanceStatus((String) couponRow[3])
                .couponIssuanceTime(couponRow[4] != null ? (LocalDateTime) couponRow[4] : null)
                .couponLimit(((Number) couponRow[5]).intValue())
                .couponLimitTime(couponRow[6] != null ? (LocalDateTime) couponRow[6] : null)
                .build();
            
            return ResponseDTO.<UserEventDetailDto>createSuccessResponse("이벤트 상세 정보 조회 성공", response);
        } catch (Exception e) {
            log.error("이벤트 상세 정보 조회 실패: {}", e.getMessage(), e);
            return (ResponseDTO<UserEventDetailDto>) ResponseDTO.createErrorResponse(500, "이벤트 상세 정보 조회에 실패했습니다.");
        }
    }

    /**
     * 쿠폰 다운로드 (사용자용)
     */
    @Transactional
    public ResponseDTO<String> downloadCoupon(Integer eventMasterIndex, Integer couponIndex) {
        try {
            // TODO: 실제 사용자 인덱스는 세션에서 가져와야 함
            Integer userIndex = 1; // 임시로 1 설정
            
            log.info("쿠폰 다운로드 시작 - eventMasterIndex: {}, couponIndex: {}, userIndex: {}", 
                    eventMasterIndex, couponIndex, userIndex);
            
            // 1. 이벤트 상태 확인
            EventMaster eventMaster = eventMasterRepository.findById(eventMasterIndex)
                .orElseThrow(() -> new RuntimeException("이벤트를 찾을 수 없습니다: " + eventMasterIndex));
            
            log.info("이벤트 정보 - count: {}, limit: {}", eventMaster.getEventMasterCount(), eventMaster.getEventMasterLimit());
            
            if (eventMaster.getEventMasterCount() <= 0) {
                log.warn("이미 종료된 이벤트입니다. eventMasterIndex: {}", eventMasterIndex);
                return (ResponseDTO<String>) ResponseDTO.createErrorResponse(400, "이미 종료된 이벤트입니다.");
            }
            
            // 2. 사용자 참여 횟수 확인
            Integer attendCount = userEventJdbcRepository.getUserEventAttendCount(eventMasterIndex, userIndex);
            log.info("사용자 참여 횟수: {}", attendCount);
            
            if (attendCount >= eventMaster.getEventMasterLimit()) {
                log.warn("쿠폰 이벤트 참여 횟수를 초과했습니다. attendCount: {}, limit: {}", 
                        attendCount, eventMaster.getEventMasterLimit());
                return (ResponseDTO<String>) ResponseDTO.createErrorResponse(400, "쿠폰 이벤트 참여 횟수를 초과하셨습니다.");
            }
            
            // 3. 쿠폰 다운로드 처리
            boolean downloadSuccess = userEventJdbcRepository.downloadCoupon(eventMasterIndex, couponIndex, userIndex);
            log.info("쿠폰 다운로드 결과: {}", downloadSuccess);
            
            if (downloadSuccess) {
                log.info("쿠폰 다운로드 성공 - eventMasterIndex: {}, couponIndex: {}, userIndex: {}", 
                        eventMasterIndex, couponIndex, userIndex);
                return ResponseDTO.<String>createSuccessResponse("쿠폰을 얻으셨습니다.", "success");
            } else {
                log.error("쿠폰 다운로드 실패 - eventMasterIndex: {}, couponIndex: {}, userIndex: {}", 
                        eventMasterIndex, couponIndex, userIndex);
                return (ResponseDTO<String>) ResponseDTO.createErrorResponse(500, "쿠폰 다운로드에 실패했습니다.");
            }
            
        } catch (Exception e) {
            log.error("쿠폰 다운로드 실패: {}", e.getMessage(), e);
            return (ResponseDTO<String>) ResponseDTO.createErrorResponse(500, "쿠폰 다운로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}

 