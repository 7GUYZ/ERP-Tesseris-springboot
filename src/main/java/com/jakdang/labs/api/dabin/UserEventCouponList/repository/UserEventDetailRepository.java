package com.jakdang.labs.api.dabin.UserEventCouponList.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserEventDetailRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    public List<Object[]> findStoreInfoByEventMasterUserIndex(Integer eventMasterUserIndex) {
        String sql = """
            SELECT DISTINCT t1.store_index, t1.store_name, t1.store_phone, t1.store_address,
                   t2.store_category_name, (t3.user_cmp_init * 2) - (t3.user_cmp_deposit - t3.user_cmp_withdrawal),
                   CONCAT(TRUNCATE(((t3.user_cmp_init * 2) - (t3.user_cmp_deposit + t3.user_cmp_withdrawal)) / 10000, 0), '만') as user_cm_use,
                   t5.store_image, 
                   CASE 
                       WHEN t4.store_business_date IS NOT NULL THEN 
                           CASE WHEN TIME_FORMAT(NOW(), '%H:%i') BETWEEN t4.store_start_business_hour AND t4.store_end_business_hour THEN '1' ELSE '0' END
                       ELSE '2' 
                   END as store_business_state,
                   t1.store_transaction_status
            FROM store t1
            INNER JOIN store_category t2 ON t1.store_category_index = t2.store_category_index
            INNER JOIN user_cm t3 ON t1.user_index = t3.user_cm_index
            LEFT JOIN store_business_hours t4 ON t1.store_index = t4.store_user_index 
            AND t4.store_business_date LIKE CONCAT('%', LOWER(DAYNAME(NOW())), '%')
            LEFT JOIN store_image t5 ON t1.store_index = t5.store_user_index AND t5.store_main_image_status = 'T'
            INNER JOIN user_tesseris t6 ON t1.user_index = t6.user_index
            WHERE t6.user_role_index = 3 AND t1.store_request_status_index = 2
            AND t1.user_index = ?
        """;
        
        log.info("Executing SQL: {}", sql);
        log.info("Parameter: eventMasterUserIndex = {}", eventMasterUserIndex);
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new Object[]{
                rs.getInt("store_index"),
                rs.getString("store_name"),
                rs.getString("store_phone"),
                rs.getString("store_address"),
                rs.getString("store_category_name"),
                rs.getString("user_cm_use"),
                rs.getString("store_image"),
                rs.getString("store_business_state"),
                rs.getString("store_transaction_status")
            };
        }, eventMasterUserIndex);
    }
    
    public List<Object[]> findEventCoupons(Integer eventMasterUserIndex, Integer eventMasterIndex) {
        String sql = """
            SELECT t1.coupon_index, t1.coupon_name, t1.coupon_price, t2.coupon_issuance_status,
                   t1.coupon_issuance_time, t1.coupon_limit, t1.coupon_limit_time
            FROM coupon t1
            INNER JOIN coupon_issuance_status t2 ON t1.coupon_issuance_status_index = t2.coupon_issuance_status_index
            WHERE t1.issuance_user_index = ?
            AND t1.coupon_issuance_status_index = 1
            AND t1.coupon_index IN (
                SELECT ed.event_coupon_index 
                FROM event_detail ed 
                WHERE ed.event_master_index = ?
            )
            ORDER BY t1.coupon_issuance_time DESC, t1.coupon_index DESC
        """;
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            return new Object[]{
                rs.getLong("coupon_index"),
                rs.getString("coupon_name"),
                rs.getInt("coupon_price"),
                rs.getString("coupon_issuance_status"),
                rs.getTimestamp("coupon_issuance_time") != null ? rs.getTimestamp("coupon_issuance_time").toLocalDateTime() : null,
                rs.getInt("coupon_limit"),
                rs.getTimestamp("coupon_limit_time") != null ? rs.getTimestamp("coupon_limit_time").toLocalDateTime() : null
            };
        }, eventMasterUserIndex, eventMasterIndex);
    }
} 