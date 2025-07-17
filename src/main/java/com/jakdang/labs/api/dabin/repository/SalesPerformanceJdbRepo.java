package com.jakdang.labs.api.dabin.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.api.dabin.dto.SalesPerformanceSearchResponseDto;

@Repository("salesPerformanceRepository")
public interface SalesPerformanceJdbRepo extends JpaRepository<com.jakdang.labs.entity.BusinessMan, Integer> {
    
    @Query("""
        SELECT new com.jakdang.labs.api.dabin.dto.SalesPerformanceSearchResponseDto(
            bm.userIndex.usersId.id,
            bm.businessGrade.businessGradeName,
            bm.businessArea.businessAreaName,
            bm.userIndex.usersId.name,
            CASE WHEN bm.businessManDistributionFlag = true THEN '정상' ELSE '정지' END,
            s.userIndex.usersId.id,
            s.storeName,
            srs.storeRequestStatusName,
            CASE WHEN s.storeTransactionStatus = true THEN '정상' ELSE '정지' END,
            CASE WHEN uc.userCmpInit / 2 < s.storeSaveValue THEN 'CM락ON' ELSE 'CM락OFF' END,
            CASE WHEN uc.userCmpDeposit + uc.userCmpWithdrawal > uc.userCmpInit * 2 THEN '판매락 ON' ELSE '판매락 OFF' END,
            s.storeRegistrationDate
        )
        FROM BusinessMan bm
        JOIN bm.userIndex ut
        JOIN bm.businessGrade bg
        JOIN bm.businessArea ba
        JOIN Store s ON s.businessManUserIndex = ut.userIndex
        JOIN s.userIndex su
        JOIN StoreRequestStatus srs ON s.storeRequestStatusIndex = srs.storeRequestStatusIndex
        JOIN UserCm uc ON uc.userCmIndex = su.userIndex
    
        WHERE (:businessUserId IS NULL OR :businessUserId = '' OR ut.usersId.id LIKE CONCAT('%', :businessUserId, '%'))
        AND (:businessGradeIndex IS NULL OR :businessGradeIndex = 0 OR bg.businessGradeIndex = :businessGradeIndex)
        AND (:userName IS NULL OR :userName = '' OR ut.usersId.name LIKE CONCAT('%', :userName, '%'))
        AND (:businessManDistributionFlag IS NULL OR bm.businessManDistributionFlag = :businessManDistributionFlag)
        AND (:storeUserId IS NULL OR :storeUserId = '' OR su.usersId.id LIKE CONCAT('%', :storeUserId, '%'))
        AND (:storeName IS NULL OR :storeName = '' OR s.storeName LIKE CONCAT('%', :storeName, '%'))
        AND (:storeRequestStatusIndex IS NULL OR :storeRequestStatusIndex = 0 OR s.storeRequestStatusIndex = :storeRequestStatusIndex)
        AND (:storeTransactionStatus IS NULL OR s.storeTransactionStatus = :storeTransactionStatus)
    """)
    
    List<SalesPerformanceSearchResponseDto> searchSalesPerformance(
        @Param("businessUserId") String businessUserId,
        @Param("businessGradeIndex") Integer businessGradeIndex,
        @Param("userName") String userName,
        @Param("businessManDistributionFlag") Boolean businessManDistributionFlag,
        @Param("storeUserId") String storeUserId,
        @Param("storeName") String storeName,
        @Param("storeRequestStatusIndex") Integer storeRequestStatusIndex,
        @Param("storeTransactionStatus") Boolean storeTransactionStatus
    );
} 