package com.jakdang.labs.api.deokkyu.admin.repository;

import com.jakdang.labs.api.deokkyu.admin.dto.AdminListResponseDto;
import com.jakdang.labs.api.deokkyu.admin.dto.ChatAdminListResponseDto;
import com.jakdang.labs.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AdminhdkRepository extends JpaRepository<Admin, Integer> {

    /**
     * 관리자 리스트 조회 (필터 조건 포함)
     */
    @Query("SELECT new com.jakdang.labs.api.deokkyu.admin.dto.AdminListResponseDto(" +
           "COALESCE(u.email, ''), COALESCE(u.name, ''), COALESCE(u.phone, ''), " +
           "COALESCE(at.adminTypeName, ''), COALESCE(a.adminRankName, ''), a.adminRegistrationDate) " +
           "FROM Admin a " +
           "LEFT JOIN a.userIndex ut " +
           "LEFT JOIN ut.usersId u " +
           "LEFT JOIN a.adminTypeIndex at " +
           "WHERE (:adminUserEmail IS NULL OR :adminUserEmail = '' OR u.email LIKE CONCAT('%', :adminUserEmail, '%')) " +
           "AND (:adminUserName IS NULL OR :adminUserName = '' OR u.name LIKE CONCAT('%', :adminUserName, '%')) " +
           "AND (:adminUserPhone IS NULL OR :adminUserPhone = '' OR u.phone LIKE CONCAT('%', :adminUserPhone, '%')) " +
           "AND (:adminTypeName IS NULL OR :adminTypeName = '' OR at.adminTypeName LIKE CONCAT('%', :adminTypeName, '%')) " +
           "AND (:adminRankName IS NULL OR :adminRankName = '' OR a.adminRankName LIKE CONCAT('%', :adminRankName, '%')) " +
           "AND (:startDate IS NULL OR DATE(a.adminRegistrationDate) >= :startDate) " +
           "AND (:endDate IS NULL OR DATE(a.adminRegistrationDate) <= :endDate) " +
           "ORDER BY a.adminRegistrationDate DESC")
    List<AdminListResponseDto> findAdminListWithFilters(
            @Param("adminUserEmail") String adminUserEmail,
            @Param("adminUserName") String adminUserName,
            @Param("adminUserPhone") String adminUserPhone,
            @Param("adminTypeName") String adminTypeName,
            @Param("adminRankName") String adminRankName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * 전체 관리자 리스트 조회 (필터 없음)
     */
    @Query("SELECT new com.jakdang.labs.api.deokkyu.admin.dto.AdminListResponseDto(" +
           "COALESCE(u.email, ''), COALESCE(u.name, ''), COALESCE(u.phone, ''), " +
           "COALESCE(at.adminTypeName, ''), COALESCE(a.adminRankName, ''), a.adminRegistrationDate) " +
           "FROM Admin a " +
           "LEFT JOIN a.userIndex ut " +
           "LEFT JOIN ut.usersId u " +
           "LEFT JOIN a.adminTypeIndex at " +
           "ORDER BY a.adminRegistrationDate DESC")
    List<AdminListResponseDto> findAllAdminList();

    /**
     * 채팅용 관리자 리스트 조회
     * adminName: user_index -> user_tesseris -> users -> name
     * adminUserIndex: admin.user_index
     * adminTypeName: admin_type_index -> admin_type.admin_type_name
     * adminRankName: admin.admin_rank_name
     */
    @Query("SELECT new com.jakdang.labs.api.deokkyu.admin.dto.ChatAdminListResponseDto(" +
           "COALESCE(u.name, ''), " +
           "ut.userIndex, " +
           "COALESCE(at.adminTypeName, ''), " +
           "COALESCE(a.adminRankName, '')) " +
           "FROM Admin a " +
           "LEFT JOIN a.userIndex ut " +
           "LEFT JOIN ut.usersId u " +
           "LEFT JOIN a.adminTypeIndex at " +
           "ORDER BY a.adminRegistrationDate DESC")
    List<ChatAdminListResponseDto> findAllChatAdminList();
} 