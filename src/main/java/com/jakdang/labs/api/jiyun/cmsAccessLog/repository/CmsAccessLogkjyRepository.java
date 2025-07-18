package com.jakdang.labs.api.jiyun.cmsAccessLog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.CmsAccessLog;

import java.util.List;

@Repository
public interface CmsAccessLogkjyRepository extends JpaRepository<CmsAccessLog, Integer> {
    
    @Query(value = """
        SELECT
            t2.users_id as userId,
            t5.name as userName,
            t1.cms_access_user_value as cmsAccessUserValue,
            t4.admin_type_name as adminTypeName,
            t1.cms_access_user_ip as cmsAccessUserIp,
            t1.cms_access_user_time as cmsAccessUserTime
        FROM cms_access_log t1
        INNER JOIN user_tesseris t2 ON t1.cms_access_user_index = t2.user_index
        INNER JOIN admin t3 ON t2.user_index = t3.user_index
        INNER JOIN admin_type t4 ON t3.admin_type_index = t4.admin_type_index
        INNER JOIN users t5 ON t2.users_id = t5.id
        WHERE (:userId = '' OR t2.users_id LIKE CONCAT('%', :userId, '%'))
          AND (:userName = '' OR t5.name LIKE CONCAT('%', :userName, '%'))
          AND (:cmsAccessUserIp = '' OR t1.cms_access_user_ip LIKE CONCAT('%', :cmsAccessUserIp, '%'))
          AND (:adminTypeIndex = '0' OR t3.admin_type_index = :adminTypeIndex)
          AND (:cmsAccessUserTimeStart IS NULL OR t1.cms_access_user_time >= :cmsAccessUserTimeStart)
          AND (:cmsAccessUserTimeEnd IS NULL OR t1.cms_access_user_time <= :cmsAccessUserTimeEnd)
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