package com.jakdang.labs.api.dabin.UserEventCouponList.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserEventRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    public List<Object[]> findActiveEvents() {
        String sql = """
            SELECT 
                t1.event_master_index, 
                t1.evnet_master_name, 
                t1.event_master_contidion,
                SUM(t3.coupon_price) as total_coupon_price, 
                t4.store_address, 
                t4.store_name,
                t1.event_master_count, 
                t1.event_master_limit
            FROM event_master t1
            INNER JOIN event_detail t2 ON t1.event_master_index = t2.event_master_index
            INNER JOIN coupon t3 ON t2.event_coupon_index = t3.coupon_index 
                AND t3.coupon_issuance_status_index = 1
            INNER JOIN store t4 ON t1.event_master_userIndex = t4.user_index
            INNER JOIN user_tesseris t5 ON t4.user_index = t5.user_index
            WHERE t1.event_master_count > 0 
                AND t4.store_request_status_index = 2
                AND t5.user_role_index = 3
            GROUP BY 
                t1.event_master_index, 
                t1.evnet_master_name, 
                t1.event_master_contidion, 
                t4.store_address, 
                t4.store_name, 
                t1.event_master_count, 
                t1.event_master_limit
            ORDER BY t1.event_master_index DESC
        """;
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new Object[]{
                rs.getInt("event_master_index"),
                rs.getString("evnet_master_name"),
                rs.getString("event_master_contidion"),
                rs.getLong("total_coupon_price"),
                rs.getString("store_address"),
                rs.getString("store_name"),
                rs.getInt("event_master_count"),
                rs.getInt("event_master_limit")
            };
        });
    }
    
    public List<Object[]> findEndedEvents() {
        String sql = """
            SELECT 
                t1.event_master_index, 
                t1.evnet_master_name, 
                t1.event_master_contidion,
                0 as total_coupon_price, 
                t4.store_address, 
                t4.store_name,
                t1.event_master_count, 
                t1.event_master_limit
            FROM event_master t1
            INNER JOIN event_detail t2 ON t1.event_master_index = t2.event_master_index
            INNER JOIN coupon t3 ON t2.event_coupon_index = t3.coupon_index
            INNER JOIN store t4 ON t1.event_master_userIndex = t4.user_index
            INNER JOIN user_tesseris t5 ON t4.user_index = t5.user_index
            WHERE t1.event_master_count = 0
                AND t4.store_request_status_index = 2
                AND t5.user_role_index = 3
            GROUP BY 
                t1.event_master_index, 
                t1.evnet_master_name, 
                t1.event_master_contidion, 
                t4.store_address, 
                t4.store_name, 
                t1.event_master_count, 
                t1.event_master_limit
            ORDER BY t1.event_master_index DESC
        """;
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new Object[]{
                rs.getInt("event_master_index"),
                rs.getString("evnet_master_name"),
                rs.getString("event_master_contidion"),
                rs.getLong("total_coupon_price"),
                rs.getString("store_address"),
                rs.getString("store_name"),
                rs.getInt("event_master_count"),
                rs.getInt("event_master_limit")
            };
        });
    }
    
    /**
     * 특정 사용자의 특정 이벤트 참여 횟수 조회 (PHP 코드와 동일한 로직)
     */
    public Integer getUserEventAttendCount(Integer eventMasterIndex, Integer userIndex) {
        String sql = """
            SELECT COUNT(*) as count 
            FROM event_attend 
            WHERE event_master_index = ? AND event_attend_user = ?
        """;
        
        return jdbcTemplate.queryForObject(sql, Integer.class, eventMasterIndex, userIndex);
    }
} 