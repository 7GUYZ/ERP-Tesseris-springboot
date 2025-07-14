package com.jakdang.labs.api.updateLog.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.api.updateLog.dto.UpdateUserLogResponseDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

@Repository
public class UpdateUserLogRepositoryImpl implements UpdateUserLogRepositoryCustom {
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
        String jpql = "SELECT new com.jakdang.labs.api.updateLog.dto.UpdateUserLogResponseDTO(" +
            "t1.updateUser.userId, t1.updateUser.userRole.userRoleKorNm, " +
            "t1.inflictUser.userId, t1.inflictUser.userRole.userRoleKorNm, " +
            "t1.updateDataValue, t1.updateBeforeData, t1.updateAfterData, t1.updateUserLogUpdateTime) " +
            "FROM UpdateUserLog t1 " +
            "WHERE (:updateUserId IS NULL OR t1.updateUser.userId LIKE :updateUserId) " +
            "AND (:inflictUserId IS NULL OR t1.inflictUser.userId LIKE :inflictUserId) " +
            "AND (:updateDataValue IS NULL OR t1.updateDataValue LIKE :updateDataValue) " +
            "AND (:updateUserLogUpdateTimeStart IS NULL OR t1.updateUserLogUpdateTime >= :updateUserLogUpdateTimeStart) " +
            "AND (:updateUserLogUpdateTimeEnd IS NULL OR t1.updateUserLogUpdateTime <= :updateUserLogUpdateTimeEnd) " +
            "ORDER BY t1.updateUserLogUpdateTime DESC";

        TypedQuery<UpdateUserLogResponseDTO> query = em.createQuery(jpql, UpdateUserLogResponseDTO.class);
        query.setParameter("updateUserId", updateUserId.isEmpty() ? null : "%" + updateUserId + "%");
        query.setParameter("inflictUserId", inflictUserId.isEmpty() ? null : "%" + inflictUserId + "%");
        query.setParameter("updateDataValue", updateDataValue.isEmpty() ? null : "%" + updateDataValue + "%");
        query.setParameter("updateUserLogUpdateTimeStart", updateUserLogUpdateTimeStart);
        query.setParameter("updateUserLogUpdateTimeEnd", updateUserLogUpdateTimeEnd);

        return query.getResultList();
    }
}