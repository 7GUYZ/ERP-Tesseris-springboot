package com.jakdang.labs.api.jiyun.updateLog.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.api.jiyun.updateLog.dto.UpdateUserLogResponseDTO;

import jakarta.persistence.EntityManager;

@Repository
public class UpdateUserLogkjyRepositoryImpl implements UpdateUserLogRepositoryCustom {
    @Autowired
    private EntityManager em;

    @Override
    public List<UpdateUserLogResponseDTO> searchLogs(
        String updateUserId,
        String inflictUserId,
        String updateDataValue,
        LocalDateTime updateUserLogUpdateTimeStart,
        LocalDateTime updateUserLogUpdateTimeEnd
    ) {
        // Native Query로 변경 (JPQL 대신)
        String sql = """
            SELECT 
                t5.email as updateUserId,
                t4.user_role_kor_nm as updateUserRoleNm1,
                t7.email as inflictUserId,
                t6.user_role_kor_nm as updateUserRoleNm2,
                t1.update_data_value as updateDataValue,
                t1.update_before_data as updateBeforeData,
                t1.update_after_data as updateAfterData,
                t1.update_user_log_update_time as updateUserLogUpdateTime
            FROM update_user_log t1
            LEFT JOIN user_tesseris t2 ON t1.update_user_index = t2.user_index
            LEFT JOIN user_tesseris t3 ON t1.inflict_user_index = t3.user_index
            LEFT JOIN user_role t4 ON t2.user_role_index = t4.user_role_index
            LEFT JOIN user_role t6 ON t3.user_role_index = t6.user_role_index
            LEFT JOIN users t5 ON t2.users_id = t5.id
            LEFT JOIN users t7 ON t3.users_id = t7.id
            WHERE (:updateUserId IS NULL OR t5.email LIKE :updateUserId)
              AND (:inflictUserId IS NULL OR t7.email LIKE :inflictUserId)
              AND (:updateDataValue IS NULL OR t1.update_data_value LIKE :updateDataValue)
              AND (:updateUserLogUpdateTimeStart IS NULL OR t1.update_user_log_update_time >= :updateUserLogUpdateTimeStart)
              AND (:updateUserLogUpdateTimeEnd IS NULL OR t1.update_user_log_update_time <= :updateUserLogUpdateTimeEnd)
            ORDER BY t1.update_user_log_update_time DESC
            """;

        @SuppressWarnings("unchecked")
        List<Object[]> results = em.createNativeQuery(sql)
            .setParameter("updateUserId", updateUserId.isEmpty() ? null : "%" + updateUserId + "%")
            .setParameter("inflictUserId", inflictUserId.isEmpty() ? null : "%" + inflictUserId + "%")
            .setParameter("updateDataValue", updateDataValue.isEmpty() ? null : "%" + updateDataValue + "%")
            .setParameter("updateUserLogUpdateTimeStart", updateUserLogUpdateTimeStart)
            .setParameter("updateUserLogUpdateTimeEnd", updateUserLogUpdateTimeEnd)
            .getResultList();

        return results.stream()
            .map(row -> new UpdateUserLogResponseDTO(
                (String) row[0], // updateUserId
                (String) row[1], // updateUserRoleNm1
                (String) row[2], // inflictUserId
                (String) row[3], // updateUserRoleNm2
                (String) row[4], // updateDataValue
                (String) row[5], // updateBeforeData
                (String) row[6], // updateAfterData
                row[7] == null ? null : ((Timestamp) row[7]).toLocalDateTime() // updateUserLogUpdateTime
            ))
            .toList();
    }
} 