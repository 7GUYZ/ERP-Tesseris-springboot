package com.jakdang.labs.api.taekjun.storelist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.Store;

@Repository
public interface AllStoreJtjRepo extends JpaRepository<Store, Integer> {

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
            LEFT JOIN store_category t2 ON t1.store_category_index = t2.store_category_index
            LEFT JOIN user_cm t3 ON t1.user_index = t3.user_cm_index
            LEFT JOIN store_business_hours t4 ON t1.user_index = t4.store_user_index
            LEFT JOIN store_image t5 ON t1.user_index = t5.store_user_index AND t5.store_main_image_status = 'T'
            LEFT JOIN business_man bm ON t1.business_man_user_index = bm.business_man_index
            WHERE
                t1.store_request_status_index = 2
                AND (:store_category_index = 0 OR t1.store_category_index = :store_category_index)
            """, nativeQuery = true)
    List<Object[]> findFilteredStoreListWithUserIndex(
            @Param("store_category_index") Integer store_category_index);

    // 가맹점 상세보기
    @Query(value = """
                SELECT
                    s.store_index,
                    s.store_name,
                    s.store_phone,
                    s.store_address,
                    s.store_detail_address,
                    s.store_site,
                    s.store_memo,
                    c.store_category_name,
                    u.user_cm_deposit + u.user_cm_withdrawal,
                    GROUP_CONCAT(
                        i.store_image 
                        ORDER BY i.store_main_image_status DESC, i.store_image_index ASC
                        SEPARATOR ','
                    ) AS store_images,
                    -- 영업 상태 계산 (아래는 예시, 실제 로직에 맞게 수정)
                    IF(
                        h.store_business_hours_index IS NULL,
                        4,
                        IF(
                            h.store_business_date IS NOT NULL
                            AND CONCAT(',', h.store_business_date, ',') LIKE CONCAT('%,', LOWER(DAYNAME(DATE_ADD(NOW(), INTERVAL 9 HOUR))), ',%'),
                            IF(
                                h.store_rest_start_hour IS NOT NULL AND h.store_rest_end_hour IS NOT NULL
                                AND TIME(DATE_ADD(NOW(), INTERVAL 9 HOUR)) BETWEEN
                                    STR_TO_DATE(h.store_rest_start_hour, '%H:%i') AND STR_TO_DATE(h.store_rest_end_hour, '%H:%i'),
                                3,
                                IF(
                                    TIME(DATE_ADD(NOW(), INTERVAL 9 HOUR)) BETWEEN
                                        STR_TO_DATE(h.store_start_business_hour, '%H:%i') AND STR_TO_DATE(h.store_end_business_hour, '%H:%i'),
                                    1,
                                    0
                                )
                            ),
                            2
                        )
                    ),
                    h.store_business_date,
                    CONCAT(h.store_start_business_hour, '~', h.store_end_business_hour) AS store_business_hour,
                    CONCAT(h.store_rest_start_hour, '~', h.store_rest_end_hour) AS store_rest_hour
                FROM store s
                INNER JOIN store_category c ON s.store_category_index = c.store_category_index
                INNER JOIN user_cm u ON s.user_index = u.user_cm_index
                LEFT JOIN store_business_hours h ON s.user_index = h.store_user_index
                LEFT JOIN store_image i ON s.user_index = i.store_user_index
                WHERE s.store_index = :store_index
                GROUP BY s.store_index, s.store_name, s.store_phone, s.store_address, 
                         s.store_detail_address, s.store_site, s.store_memo, c.store_category_name,
                         u.user_cm_deposit, u.user_cm_withdrawal, h.store_business_hours_index,
                         h.store_business_date, h.store_start_business_hour, h.store_end_business_hour,
                         h.store_rest_start_hour, h.store_rest_end_hour
            """, nativeQuery = true)
    Object findStoreDetailByStoreIndex(@Param("store_index") Integer store_index);
} 