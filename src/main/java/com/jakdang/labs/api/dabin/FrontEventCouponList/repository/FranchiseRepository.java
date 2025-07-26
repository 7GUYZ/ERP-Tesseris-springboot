package com.jakdang.labs.api.dabin.FrontEventCouponList.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.jakdang.labs.entity.Store;

import java.util.List;

@Repository
public interface FranchiseRepository extends JpaRepository<Store, Integer> {
    
    /**
     * 특정 가맹점 정보 조회
     */
    @Query(value = """
        SELECT DISTINCT t1.store_index, t1.store_name, t1.store_phone, t1.store_address, t1.store_detail_address,
               t2.store_category_name, t1.store_pos1, t1.store_pos2,
               (t3.user_cmp_init * 2) - (t3.user_cmp_deposit - t3.user_cmp_withdrawal) as userCmAmount,
               CONCAT(TRUNCATE(((t3.user_cmp_init * 2) - (t3.user_cmp_deposit + t3.user_cmp_withdrawal)) / 10000, 0), '만') as userCmUse,
               t5.store_image
        FROM store t1
        INNER JOIN store_category t2 ON t1.store_category_index = t2.store_category_index
        INNER JOIN user_cm t3 ON t1.user_index = t3.user_cm_index
        LEFT JOIN store_image t5 ON t1.store_index = t5.store_user_index AND t5.store_main_image_status = 'T'
        INNER JOIN user_tesseris t6 ON t1.user_index = t6.user_index
        WHERE t6.user_role_index = 3 AND t1.store_request_status_index = 2
        AND t1.store_index = :storeIndex
    """, nativeQuery = true)
    List<Object[]> findFranchiseInfoByStoreIndex(@Param("storeIndex") Integer storeIndex);
    
    /**
     * 주변 가맹점 검색 (위도/경도 기반)
     */
    @Query(value = """
        SELECT DISTINCT t1.store_index, t1.store_name, t1.store_phone, t1.store_address, t1.store_detail_address,
               t2.store_category_name, t1.store_pos1, t1.store_pos2,
               (t3.user_cmp_init * 2) - (t3.user_cmp_deposit - t3.user_cmp_withdrawal) as userCmAmount,
               CONCAT(TRUNCATE(((t3.user_cmp_init * 2) - (t3.user_cmp_deposit + t3.user_cmp_withdrawal)) / 10000, 0), '만') as userCmUse,
               t5.store_image,
               (6371 * acos(cos(radians(:latitude)) * cos(radians(CAST(t1.store_pos2 AS DECIMAL(10,8)))) * 
                cos(radians(CAST(t1.store_pos1 AS DECIMAL(10,8))) - radians(:longitude)) + 
                sin(radians(:latitude)) * sin(radians(CAST(t1.store_pos2 AS DECIMAL(10,8)))))) as distance
        FROM store t1
        INNER JOIN store_category t2 ON t1.store_category_index = t2.store_category_index
        INNER JOIN user_cm t3 ON t1.user_index = t3.user_cm_index
        LEFT JOIN store_image t5 ON t1.store_index = t5.store_user_index AND t5.store_main_image_status = 'T'
        INNER JOIN user_tesseris t6 ON t1.user_index = t6.user_index
        WHERE t6.user_role_index = 3 AND t1.store_request_status_index = 2
        AND t1.store_pos1 IS NOT NULL AND t1.store_pos2 IS NOT NULL
        AND (6371 * acos(cos(radians(:latitude)) * cos(radians(CAST(t1.store_pos2 AS DECIMAL(10,8)))) * 
             cos(radians(CAST(t1.store_pos1 AS DECIMAL(10,8))) - radians(:longitude)) + 
             sin(radians(:latitude)) * sin(radians(CAST(t1.store_pos2 AS DECIMAL(10,8)))))) <= :radius
        AND (:franType IS NULL OR t1.store_category_index = :franType)
        ORDER BY distance
    """, nativeQuery = true)
    List<Object[]> findNearbyFranchises(
        @Param("latitude") Double latitude,
        @Param("longitude") Double longitude,
        @Param("radius") Double radius,
        @Param("franType") Integer franType
    );
} 