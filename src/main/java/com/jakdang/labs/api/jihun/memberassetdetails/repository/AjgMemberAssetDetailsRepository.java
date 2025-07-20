package com.jakdang.labs.api.jihun.memberassetdetails.repository;

import com.jakdang.labs.entity.UserCmLog;
import com.jakdang.labs.entity.UserCmLogPayment;
import com.jakdang.labs.entity.UserCmLogTransactionType;
import com.jakdang.labs.entity.UserCmLogValueType;
import com.jakdang.labs.entity.UserTesseris;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AjgMemberAssetDetailsRepository extends JpaRepository<UserTesseris, Integer> {
    
    // 회원 조회 (users_id로)
    Optional<UserTesseris> findByUsersId_Id(String userId);
    
    @Query(value = """
        SELECT
            u.user_index as userIndex,
            us.id as userId,
            us.name as userName,
            us.phone as userPhone,
            us.email as userEmail,
            r.user_role_kor_nm as userRoleKorNm,
            s.store_name as storeName,
            (c.user_cm_deposit - c.user_cm_withdrawal) as userCmCurrent,
            (c.user_cmp_deposit - c.user_cmp_withdrawal) as userCmpCurrent,
            (c.user_cash_deposit - c.user_cash_withdrawal) as userCashCurrent,
            us.created_at as userCreateTime,
            t4.user_bank_name as userBankName,
            u.user_bank_number as userBankNumber,
            u.user_bank_holder as userBankHolder,
            u.user_jumin as userJumin,
            t6_us.id as suggestionUserId,
            t6_us.name as suggestionUserName,
            FORMAT(IFNULL(t7.total_cash, 0), '###,###.##') as temporaryStoreCashValue
        FROM user_tesseris u
        LEFT JOIN users us ON u.users_id = us.id
        LEFT JOIN user_cm c ON u.user_index = c.user_cm_index
        LEFT JOIN user_role r ON u.user_role_index = r.user_role_index
        LEFT JOIN store s ON u.user_index = s.user_index
        LEFT JOIN user_bank t4 ON u.user_bank_index = t4.user_bank_index
        LEFT JOIN suggestion_user t5 ON u.user_index = t5.recommendation_user_index
        LEFT JOIN user_tesseris t6 ON t5.suggestion_user_index = t6.user_index
        LEFT JOIN users t6_us ON t6.users_id = t6_us.id
        LEFT JOIN (
            SELECT store_user_index, SUM(temporary_store_cash_value) AS total_cash
            FROM temporary_regular_master
            GROUP BY store_user_index
        ) t7 ON u.user_index = t7.store_user_index
        WHERE (:userEmail IS NULL OR SUBSTRING_INDEX(us.email, '@', 1) LIKE CONCAT('%', :userEmail, '%'))
        AND (:userName IS NULL OR us.name LIKE CONCAT('%', :userName, '%'))
        AND (:userPhone IS NULL OR us.phone LIKE CONCAT('%', :userPhone, '%'))
        AND (:userRoleIndex IS NULL OR u.user_role_index = :userRoleIndex)
        """, nativeQuery = true)
    Page<Object[]> findMemberAssetDetails(
        @Param("userEmail") String userEmail,
        @Param("userName") String userName,
        @Param("userPhone") String userPhone,
        @Param("userRoleIndex") Integer userRoleIndex,
        Pageable pageable
    );
    
    @Query(value = """
        SELECT 
            user_role_index as roleIndex,
            user_role_kor_nm as roleName
        FROM user_role 
        ORDER BY user_role_index
        """, nativeQuery = true)
    List<Object[]> findUserRoles();
    
    @Query(value = """
        INSERT INTO user_cm (user_cm_index, user_cm_deposit, user_cm_withdrawal, user_cmp_deposit, user_cmp_withdrawal, user_cash_deposit, user_cash_withdrawal)
        SELECT user_index, :amount, 0, 0, 0, 0, 0
        FROM user_tesseris 
        WHERE users_id = :userId
        AND NOT EXISTS (
            SELECT 1 FROM user_cm WHERE user_cm_index = (
                SELECT user_index FROM user_tesseris WHERE users_id = :userId
            )
        )
        """, nativeQuery = true)
    @Modifying
    @Transactional
    int insertCmDeposit(@Param("userId") String userId, @Param("amount") Integer amount);
    
    @Query(value = """
        UPDATE user_cm 
        SET user_cm_deposit = user_cm_deposit + :amount
        WHERE user_cm_index = (
            SELECT user_index FROM user_tesseris WHERE users_id = :userId
        )
        """, nativeQuery = true)
    @Modifying
    @Transactional
    int updateCmDeposit(@Param("userId") String userId, @Param("amount") Integer amount);
    
    @Query(value = """
        INSERT INTO user_cm (user_cm_index, user_cm_deposit, user_cm_withdrawal, user_cmp_deposit, user_cmp_withdrawal, user_cash_deposit, user_cash_withdrawal)
        SELECT user_index, 0, :amount, 0, 0, 0, 0
        FROM user_tesseris 
        WHERE users_id = :userId
        AND NOT EXISTS (
            SELECT 1 FROM user_cm WHERE user_cm_index = (
                SELECT user_index FROM user_tesseris WHERE users_id = :userId
            )
        )
        """, nativeQuery = true)
    @Modifying
    @Transactional
    int insertCmWithdrawal(@Param("userId") String userId, @Param("amount") Integer amount);
    
    @Query(value = """
        UPDATE user_cm 
        SET user_cm_withdrawal = user_cm_withdrawal + :amount
        WHERE user_cm_index = (
            SELECT user_index FROM user_tesseris WHERE users_id = :userId
        )
        """, nativeQuery = true)
    @Modifying
    @Transactional
    int updateCmWithdrawal(@Param("userId") String userId, @Param("amount") Integer amount);
} 