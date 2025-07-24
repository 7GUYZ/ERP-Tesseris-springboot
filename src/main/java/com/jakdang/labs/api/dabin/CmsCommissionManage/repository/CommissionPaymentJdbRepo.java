package com.jakdang.labs.api.dabin.CmsCommissionManage.repository;

import java.util.List;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.api.dabin.CmsCommissionManage.dto.CommissionPaymentResponse;
import com.jakdang.labs.entity.TemporaryRegularMaster;

@Repository("commissionPaymentRepository")
public interface CommissionPaymentJdbRepo extends JpaRepository<TemporaryRegularMaster, Integer> {
    
    @Query("""
        SELECT new com.jakdang.labs.api.dabin.CmsCommissionManage.dto.CommissionPaymentResponse(
            ut.usersId.id,
            ut.userIndex,
            us.name,
            us.phone,
            t1.temporaryStoreMasterTransactionName,
            t1.temporaryStoreMasterChargeTime,
            t1.temporaryStoreCmValue,
            t1.temporaryStoreCashValue,
            t1.temporaryStoreMasterIndex,
            t4.temporaryStoreDetailIndex,
            t4.userIndex.userIndex,
            t4.temporaryRegularCashValue,
            COALESCE(t4.description, '정회원'),
            t4.paymentStatus,
            t5.advanceMsg,
            t6.usersId.id,
            us2.name,
            us2.phone,
            t7.userRoleKorNm,
            COALESCE(t6.userBankNumber, ''),
            COALESCE(t8.userBankName, ''),
            COALESCE(t6.userJumin, ''),
            COALESCE(t6.userBankHolder, '')
        )
        FROM TemporaryRegularMaster t1
        JOIN t1.storeUserIndex ut
        JOIN ut.usersId us
        JOIN TemporaryRegularDetail t4 ON t1.temporaryStoreMasterIndex = t4.temporaryRegularMasterIndex
        JOIN RegularPayment t5 ON t1.temporaryStoreMasterTransactionName = t5.trxId
        JOIN t4.userIndex t6
        JOIN t6.usersId us2
        JOIN UserRole t7 ON t6.userRoleIndex = t7.userRoleIndex
        LEFT JOIN t6.userBank t8
        WHERE ut.userRoleIndex > 0
        AND (:userId IS NULL OR :userId = '' OR ut.usersId.id LIKE CONCAT('%', :userId, '%'))
        AND (:userName IS NULL OR :userName = '' OR us.name LIKE CONCAT('%', :userName, '%'))
        AND (:userPhone IS NULL OR :userPhone = '' OR us.phone LIKE CONCAT('%', :userPhone, '%'))
        AND (:chargeTimeStart IS NULL OR t1.temporaryStoreMasterChargeTime >= :chargeTimeStart)
        AND (:chargeTimeEnd IS NULL OR t1.temporaryStoreMasterChargeTime <= :chargeTimeEnd)
        AND (:transactionName IS NULL OR :transactionName = '' OR t1.temporaryStoreMasterTransactionName = :transactionName)
        AND (:suggestionUserId IS NULL OR :suggestionUserId = '' OR t6.usersId.id LIKE CONCAT('%', :suggestionUserId, '%'))
        AND (:suggestionUserName IS NULL OR :suggestionUserName = '' OR us2.name LIKE CONCAT('%', :suggestionUserName, '%'))
        AND (:userRoleIndex IS NULL OR t6.userRoleIndex = :userRoleIndex)
        AND (:paymentStatus IS NULL OR :paymentStatus = '' OR t4.paymentStatus = :paymentStatus)
        AND (:description IS NULL OR :description = '' OR t4.description = :description)
        ORDER BY t1.temporaryStoreMasterChargeTime DESC
    """)
    List<CommissionPaymentResponse> searchCommissionPayments(
        @Param("userId") String userId,
        @Param("userName") String userName,
        @Param("userPhone") String userPhone,
        @Param("chargeTimeStart") LocalDateTime chargeTimeStart,
        @Param("chargeTimeEnd") LocalDateTime chargeTimeEnd,
        @Param("transactionName") String transactionName,
        @Param("suggestionUserId") String suggestionUserId,
        @Param("suggestionUserName") String suggestionUserName,
        @Param("userRoleIndex") Integer userRoleIndex,
        @Param("paymentStatus") String paymentStatus,
        @Param("description") String description
    );
    
    @Query("""
        SELECT CASE 
            WHEN t4.userRoleIndex = 1 THEN false
            WHEN t4.userBankNumber IS NULL OR t4.userBankNumber = '' THEN false
            WHEN t4.userBank.userBankIndex = 0 THEN false
            WHEN t4.userJumin IS NULL OR t4.userJumin = '' THEN false
            ELSE true
        END
        FROM TemporaryRegularDetail t1
        JOIN t1.userIndex t4
        WHERE t1.temporaryStoreDetailIndex = :detailIndex
    """)
    Boolean validatePaymentEligibility(@Param("detailIndex") Integer detailIndex);
    
    @Query("""
        SELECT t4.userRoleIndex, t4.userBankNumber, 
               CASE WHEN t4.userBank IS NULL THEN 0 ELSE t4.userBank.userBankIndex END, 
               t4.userJumin
        FROM TemporaryRegularDetail t1
        JOIN t1.userIndex t4
        WHERE t1.temporaryStoreDetailIndex = :detailIndex
    """)
    Object[] getPaymentEligibilityDetails(@Param("detailIndex") Integer detailIndex);
    
    @Modifying
    @Query("UPDATE TemporaryRegularDetail t SET t.paymentStatus = :paymentStatus WHERE t.temporaryStoreDetailIndex = :detailIndex")
    void updatePaymentStatus(@Param("detailIndex") Integer detailIndex, @Param("paymentStatus") String paymentStatus);
} 