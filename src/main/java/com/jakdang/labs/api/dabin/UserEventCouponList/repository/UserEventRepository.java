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
            SELECT t1.event_master_index, t1.evnet_master_name, t1.event_master_contidion,
                   SUM(t3.coupon_price) as total_coupon_price, t4.store_address, t4.store_name,
                   t1.event_master_count
            FROM event_master t1
            INNER JOIN event_detail t2 ON t1.event_master_index = t2.event_master_index
            INNER JOIN coupon t3 ON t2.event_coupon_index = t3.coupon_index 
            INNER JOIN store t4 ON t1.event_master_userIndex = t4.user_index
            INNER JOIN user_tesseris t7 ON t4.user_index = t7.user_index
            WHERE t1.event_master_count > 0 AND t3.coupon_issuance_status_index = 1
            GROUP BY t1.event_master_index, t1.evnet_master_name, t1.event_master_contidion, 
                     t4.store_address, t4.store_name, t1.event_master_count
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
                rs.getInt("event_master_count")
            };
        });
    }
    
    public List<Object[]> findEndedEvents() {
        String sql = """
            SELECT t1.event_master_index, t1.evnet_master_name, t1.event_master_contidion,
                   0 as total_coupon_price, t4.store_address, t4.store_name,
                   t1.event_master_count
            FROM event_master t1
            INNER JOIN event_detail t2 ON t1.event_master_index = t2.event_master_index
            INNER JOIN coupon t3 ON t2.event_coupon_index = t3.coupon_index
            INNER JOIN store t4 ON t1.event_master_userIndex = t4.user_index
            INNER JOIN user_tesseris t7 ON t4.user_index = t7.user_index
            WHERE t1.event_master_count = 0
            GROUP BY t1.event_master_index, t1.evnet_master_name, t1.event_master_contidion, 
                     t4.store_address, t4.store_name, t1.event_master_count
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
                rs.getInt("event_master_count")
            };
        });
    }
} 