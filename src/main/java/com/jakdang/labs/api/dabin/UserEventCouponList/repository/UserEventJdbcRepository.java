package com.jakdang.labs.api.dabin.UserEventCouponList.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserEventJdbcRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    /**
     * 사용자 이벤트 참여 횟수 조회
     */
    public Integer getUserEventAttendCount(Integer eventMasterIndex, Integer userIndex) {
        try {
            String sql = "SELECT COUNT(*) FROM event_attend WHERE event_master_index = ? AND event_attend_user = ?";
            Integer result = jdbcTemplate.queryForObject(sql, Integer.class, eventMasterIndex, userIndex);
            return result != null ? result : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * 쿠폰 다운로드 처리
     */
    public boolean downloadCoupon(Integer eventMasterIndex, Integer couponIndex, Integer userIndex) {
        try {
            log.info("쿠폰 다운로드 시작 - eventMasterIndex: {}, couponIndex: {}, userIndex: {}", 
                    eventMasterIndex, couponIndex, userIndex);
            
            // 1. 쿠폰 상태 확인 (이미 발급된 쿠폰인지 확인)
            String checkSql = """
                SELECT coupon_issuance_status_index, provided_user_index 
                FROM coupon 
                WHERE coupon_index = ?
            """;
            Map<String, Object> couponStatus = jdbcTemplate.queryForMap(checkSql, couponIndex);
            
            Integer currentStatus = (Integer) couponStatus.get("coupon_issuance_status_index");
            Object providedUser = couponStatus.get("provided_user_index");
            
            log.info("쿠폰 상태 확인 - currentStatus: {}, providedUser: {}", currentStatus, providedUser);
            
            // 이미 발급된 쿠폰이거나 다른 사용자가 받은 쿠폰인 경우
            if (currentStatus != 1 || providedUser != null) {
                log.warn("쿠폰이 이미 발급되었거나 사용할 수 없습니다. currentStatus: {}, providedUser: {}", 
                        currentStatus, providedUser);
                return false;
            }
            
            // 2. Event_Attend 테이블에 참여 기록 추가
            String attendSql = """
                INSERT INTO event_attend(event_master_index, event_attend_user)
                VALUES (?, ?)
            """;
            int attendResult = jdbcTemplate.update(attendSql, eventMasterIndex, userIndex);
            log.info("Event_Attend 삽입 결과: {}", attendResult);
            if (attendResult == 0) {
                log.error("Event_Attend 삽입 실패");
                return false;
            }
            
            // 3. Coupon 테이블에서 쿠폰 상태 업데이트
            String couponSql = """
                UPDATE coupon
                SET provided_user_index = ?,
                    coupon_issuance_status_index = 3,
                    coupon_provided_status_index = 1,
                    coupon_provided_time = NOW(),
                    coupon_limit_time = CONCAT(DATE_ADD(CURDATE(), INTERVAL coupon_limit DAY), ' 00:00:00')
                WHERE coupon_index = ? AND coupon_issuance_status_index = 1 AND provided_user_index IS NULL
            """;
            int couponResult = jdbcTemplate.update(couponSql, userIndex, couponIndex);
            log.info("Coupon 업데이트 결과: {}", couponResult);
            if (couponResult == 0) {
                log.error("Coupon 업데이트 실패");
                return false;
            }
            
            // 4. Event_Master 테이블에서 카운트 감소
            String eventSql = """
                UPDATE event_master
                SET event_master_count = event_master_count - 1
                WHERE event_master_index = ? AND event_master_count > 0
            """;
            int eventResult = jdbcTemplate.update(eventSql, eventMasterIndex);
            log.info("Event_Master 업데이트 결과: {}", eventResult);
            if (eventResult == 0) {
                log.error("Event_Master 업데이트 실패");
                return false;
            }
            
            log.info("쿠폰 다운로드 성공");
            return true;
        } catch (Exception e) {
            log.error("쿠폰 다운로드 중 오류 발생: {}", e.getMessage(), e);
            return false;
        }
    }
} 