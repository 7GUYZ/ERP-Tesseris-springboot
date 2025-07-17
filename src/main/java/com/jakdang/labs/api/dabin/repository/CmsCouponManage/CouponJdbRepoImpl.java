package com.jakdang.labs.api.dabin.repository.CmsCouponManage;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.jakdang.labs.api.dabin.dto.CmsCouponManage.CouponSearchRequestDto;
import com.jakdang.labs.api.dabin.dto.CmsCouponManage.CouponSearchResponseDto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
public class CouponJdbRepoImpl implements CouponJdbRepoCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<CouponSearchResponseDto> searchCoupons(CouponSearchRequestDto dto) {
        // 빈 문자열 파라미터를 null로 변환
        if (dto.getCouponName() != null && dto.getCouponName().isBlank()) {
            dto.setCouponName(null);
        }
        if (dto.getIssuanceUserId() != null && dto.getIssuanceUserId().isBlank()) {
            dto.setIssuanceUserId(null);
        }
        if (dto.getProvidedUserId() != null && dto.getProvidedUserId().isBlank()) {
            dto.setProvidedUserId(null);
        }
        String jpql = "SELECT new com.jakdang.labs.api.dabin.dto.CouponSearchResponseDto(" +
        "c.couponIndex, c.couponName, c.couponPrice, c.couponLimit, " +
        "iu.usersId.id, iur.userRoleKorNm, pu.usersId.id, pur.userRoleKorNm, " +
        "cis.couponIssuanceStatus, cps.couponProvidedStatus, " +
        "c.couponIssuanceTime, c.couponProvidedTime, c.couponLimitTime) " +
        "FROM Coupon c " +
        "LEFT JOIN c.issuanceUser iu " +
        "LEFT JOIN c.providedUser pu " +
        "LEFT JOIN iu.usersId uiu " +                      // UserEntity
        "LEFT JOIN pu.usersId upu " +                      // UserEntity
        "LEFT JOIN UserRole iur ON iu.userRoleIndex = iur.userRoleIndex " +
        "LEFT JOIN UserRole pur ON pu.userRoleIndex = pur.userRoleIndex " +
        "LEFT JOIN CouponIssuanceStatus cis ON c.couponIssuanceStatusIndex = cis.couponIssuanceStatusIndex " +
        "LEFT JOIN CouponProvidedStatus cps ON c.couponProvidedStatusIndex = cps.couponProvidedStatusIndex " +
        "WHERE (:couponName IS NULL OR c.couponName LIKE :couponName) " +
        "AND (:couponPrice IS NULL OR c.couponPrice = :couponPrice) " +
        "AND (:issuanceUserId IS NULL OR uiu.id LIKE :issuanceUserId) " +
        "AND (:providedUserId IS NULL OR upu.id LIKE :providedUserId) " +
        "AND (:issuanceStatusIndex IS NULL OR c.couponIssuanceStatusIndex = :issuanceStatusIndex) " +
        "AND (:providedStatusIndex IS NULL OR c.couponProvidedStatusIndex = :providedStatusIndex) " +
        "AND (:issuanceStart IS NULL OR c.couponIssuanceTime >= :issuanceStart) " +
        "AND (:issuanceEnd IS NULL OR c.couponIssuanceTime <= :issuanceEnd) " +
        "AND (:providedStart IS NULL OR c.couponProvidedTime >= :providedStart) " +
        "AND (:providedEnd IS NULL OR c.couponProvidedTime <= :providedEnd) " +
        "AND (:limitStart IS NULL OR c.couponLimitTime >= :limitStart) " +
        "AND (:limitEnd IS NULL OR c.couponLimitTime <= :limitEnd) " +
        "ORDER BY c.couponIssuanceTime DESC";
    

        TypedQuery<CouponSearchResponseDto> query = em.createQuery(jpql, CouponSearchResponseDto.class);
        query.setParameter("couponName", dto.getCouponName() == null ? null : "%" + dto.getCouponName() + "%");
        query.setParameter("couponPrice", dto.getCouponPrice());
        query.setParameter("issuanceUserId", dto.getIssuanceUserId() == null ? null : "%" + dto.getIssuanceUserId() + "%");
        query.setParameter("providedUserId", dto.getProvidedUserId() == null || dto.getProvidedUserId().isEmpty() ? null : "%" + dto.getProvidedUserId() + "%");
        query.setParameter("issuanceStatusIndex", dto.getIssuanceStatusIndex());
        query.setParameter("providedStatusIndex", dto.getProvidedStatusIndex());
        query.setParameter("issuanceStart", dto.getIssuanceStart());
        query.setParameter("issuanceEnd", dto.getIssuanceEnd());
        query.setParameter("providedStart", dto.getProvidedStart());
        query.setParameter("providedEnd", dto.getProvidedEnd());
        query.setParameter("limitStart", dto.getLimitStart());
        query.setParameter("limitEnd", dto.getLimitEnd());

        return query.getResultList();
    }
} 
