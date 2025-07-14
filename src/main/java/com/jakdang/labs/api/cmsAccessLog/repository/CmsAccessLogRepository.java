package com.jakdang.labs.api.cmsAccessLog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CmsAccessLogRepository extends JpaRepository<com.jakdang.labs.api.cmsAccessLog.entity.CmsAccessLog, Integer> {
    
    @Query(value = """
        SELECT
            t2.user_id as userId,
            t2.user_name as userName,
            t1.cms_access_user_value as cmsAccessUserValue,
            t4.admin_type_name as adminTypeName,
            t1.cms_access_user_ip as cmsAccessUserIp,
            t1.cms_access_user_time as cmsAccessUserTime
        FROM Cms_Access_Log t1
        INNER JOIN User t2 ON t1.cms_access_user_index = t2.user_index
        INNER JOIN Admin t3 ON t2.user_index = t3.user_index
        INNER JOIN Admin_Type t4 ON t3.admin_type_index = t4.admin_type_index
        WHERE (:userId = '' OR t2.user_id LIKE CONCAT('%', :userId, '%'))
          AND (:userName = '' OR t2.user_name LIKE CONCAT('%', :userName, '%'))
          AND (:cmsAccessUserIp = '' OR t1.cms_access_user_ip LIKE CONCAT('%', :cmsAccessUserIp, '%'))
          AND (:adminTypeIndex = '0' OR t3.admin_type_index = :adminTypeIndex)
          AND (:cmsAccessUserTimeStart = '' OR t1.cms_access_user_time >= :cmsAccessUserTimeStart)
          AND (:cmsAccessUserTimeEnd = '' OR t1.cms_access_user_time <= :cmsAccessUserTimeEnd)
        ORDER BY t1.cms_access_user_time DESC
        """, nativeQuery = true)
    List<Object[]> searchCmsAccessLogs(
        @Param("userId") String userId,
        @Param("userName") String userName,
        @Param("cmsAccessUserIp") String cmsAccessUserIp,
        @Param("adminTypeIndex") String adminTypeIndex,
        @Param("cmsAccessUserTimeStart") String cmsAccessUserTimeStart,
        @Param("cmsAccessUserTimeEnd") String cmsAccessUserTimeEnd
    );
} 