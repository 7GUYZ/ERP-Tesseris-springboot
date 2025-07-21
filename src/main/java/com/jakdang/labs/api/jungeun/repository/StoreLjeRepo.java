package com.jakdang.labs.api.jungeun.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.Store;

@Repository
public interface StoreLjeRepo extends JpaRepository<Store, Integer> {

    @Query(value = """
        SELECT 
            t1.store_index, 
            t1.store_name, 
            t1.store_phone, 
            t1.store_address, 
            t2.store_category_name, 
            (t3.user_cm_deposit + t3.user_cm_withdrawal),
            t5.store_image,
            IF(
                t4.store_business_hours_index IS NULL, 
                4,
                IF(
                    t4.store_business_date IS NOT NULL 
                    AND CONCAT(',', t4.store_business_date, ',') LIKE CONCAT('%,', LOWER(DAYNAME(DATE_ADD(NOW(), INTERVAL 9 HOUR))), ',%'),
                    IF(
                        t4.store_rest_start_hour IS NOT NULL AND t4.store_rest_end_hour IS NOT NULL
                        AND TIME(DATE_ADD(NOW(), INTERVAL 9 HOUR)) BETWEEN 
                            STR_TO_DATE(t4.store_rest_start_hour, '%H:%i') AND STR_TO_DATE(t4.store_rest_end_hour, '%H:%i'),
                        3,
                        IF(
                            TIME(DATE_ADD(NOW(), INTERVAL 9 HOUR)) BETWEEN 
                                STR_TO_DATE(t4.store_start_business_hour, '%H:%i') AND STR_TO_DATE(t4.store_end_business_hour, '%H:%i'),
                            1,
                            0
                        )
                    ),
                    2
                )
            ) AS store_business_state
        FROM 
            store t1
        INNER JOIN store_category t2 
            ON t1.store_category_index = t2.store_category_index
        INNER JOIN user_cm t3 
            ON t1.user_index = t3.user_cm_index
        LEFT JOIN store_business_hours t4 
            ON t1.user_index = t4.store_user_index
        LEFT JOIN store_image t5 
            ON t1.user_index = t5.store_user_index 
            AND t5.store_main_image_status = 'T'
        INNER JOIN business_man bm 
            ON t1.business_man_user_index = bm.business_man_index
        WHERE 
            bm.user_index = :user_index
            AND t1.store_request_status_index = 2
            AND (:store_category_index = 0 OR t1.store_category_index = :store_category_index)
        """, nativeQuery = true)
        List<Object[]> findFilteredStoreListWithUserIndex(
            @Param("user_index") Integer user_index, 
            @Param("store_category_index") Integer store_category_index
        );
}
