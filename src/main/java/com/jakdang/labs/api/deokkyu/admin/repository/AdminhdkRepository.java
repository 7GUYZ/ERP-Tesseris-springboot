package com.jakdang.labs.api.deokkyu.admin.repository;

import com.jakdang.labs.api.deokkyu.admin.dto.AdminListResponseDto;
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
           "CAST(ut.userIndex AS string), COALESCE(u.email, ''), COALESCE(u.name, ''), COALESCE(u.phone, ''), " +
           "COALESCE(at.adminTypeName, ''), a.adminRegistrationDate) " +
           "FROM Admin a " +
           "JOIN a.userIndex ut " +
           "JOIN ut.usersId u " +
           "JOIN a.adminTypeIndex at " +
           "WHERE (:adminUserEmail IS NULL OR :adminUserEmail = '' OR u.email LIKE CONCAT('%', :adminUserEmail, '%')) " +
           "AND (:adminUserName IS NULL OR :adminUserName = '' OR u.name LIKE CONCAT('%', :adminUserName, '%')) " +
           "AND (:adminUserPhone IS NULL OR :adminUserPhone = '' OR u.phone LIKE CONCAT('%', :adminUserPhone, '%')) " +
           "AND (:adminTypeName IS NULL OR :adminTypeName = '' OR at.adminTypeName LIKE CONCAT('%', :adminTypeName, '%')) " +
           "AND (:startDate IS NULL OR DATE(a.adminRegistrationDate) >= :startDate) " +
           "AND (:endDate IS NULL OR DATE(a.adminRegistrationDate) <= :endDate) " +
           "ORDER BY a.adminRegistrationDate DESC")
    List<AdminListResponseDto> findAdminListWithFilters(
            @Param("adminUserEmail") String adminUserEmail,
            @Param("adminUserName") String adminUserName,
            @Param("adminUserPhone") String adminUserPhone,
            @Param("adminTypeName") String adminTypeName,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * 전체 관리자 리스트 조회 (필터 없음)
     */
    @Query("SELECT new com.jakdang.labs.api.deokkyu.admin.dto.AdminListResponseDto(" +
           "CAST(ut.userIndex AS string), COALESCE(u.email, ''), COALESCE(u.name, ''), COALESCE(u.phone, ''), " +
           "COALESCE(at.adminTypeName, ''), a.adminRegistrationDate) " +
           "FROM Admin a " +
           "JOIN a.userIndex ut " +
           "JOIN ut.usersId u " +
           "JOIN a.adminTypeIndex at " +
           "ORDER BY a.adminRegistrationDate DESC")
    List<AdminListResponseDto> findAllAdminList();
    
    /**
     * 디버깅용: Admin 테이블만 조회
     */
    @Query("SELECT a FROM Admin a")
    List<Admin> findAllAdmins();
} 