package com.jakdang.labs.api.taekjun.businessmanlist.repository;

import com.jakdang.labs.entity.BusinessMan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessmanListJtjRepo extends JpaRepository<BusinessMan, Integer> {
    @Query(value = """
        SELECT bm
        FROM BusinessMan bm
        JOIN FETCH bm.userIndex u
        JOIN FETCH u.usersId ue
        JOIN FETCH bm.businessGrade bg
        JOIN FETCH bm.businessArea ba
        WHERE u.userRoleIndex = 2
          AND (:email IS NULL OR ue.email LIKE %:email%)
          AND (:userName IS NULL OR ue.name LIKE %:userName%)
          AND (:userPhone IS NULL OR ue.phone LIKE %:userPhone%)
          AND (:businessGradeName IS NULL OR bg.businessGradeName LIKE %:businessGradeName%)
          AND (:bossEmail IS NULL OR (SELECT bu.usersId.email FROM UserTesseris bu WHERE bu.userIndex = bm.bossUserIndex) LIKE %:bossEmail%)
          AND (:businessAreaName IS NULL OR ba.businessAreaName LIKE %:businessAreaName%)
          AND (:businessAreaLevel IS NULL OR ba.businessAreaLevel = :businessAreaLevel)
          AND (:businessManDistributionFlag IS NULL OR bm.businessManDistributionFlag = CASE WHEN :businessManDistributionFlag = '정상' THEN true WHEN :businessManDistributionFlag = '정지' THEN false ELSE bm.businessManDistributionFlag END)
        ORDER BY bm.businessManIndex DESC
    """)
    List<BusinessMan> searchBusinessManList(
            @Param("email") String email,
            @Param("userName") String userName,
            @Param("userPhone") String userPhone,
            @Param("businessGradeName") String businessGradeName,
            @Param("bossEmail") String bossEmail,
            @Param("businessAreaName") String businessAreaName,
            @Param("businessAreaLevel") Integer businessAreaLevel,
            @Param("businessManDistributionFlag") String businessManDistributionFlag
    );
} 