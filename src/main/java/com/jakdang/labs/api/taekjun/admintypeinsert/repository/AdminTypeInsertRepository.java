package com.jakdang.labs.api.taekjun.admintypeinsert.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.adminType;

@Repository
public interface AdminTypeInsertRepository extends JpaRepository<adminType, Integer> {

    // adminTypeOrder로 정렬해서 모든 adminType 조회
    List<adminType> findAllByOrderByAdminTypeOrderAsc();

    // 특정 order 이상의 adminType들 조회
    List<adminType> findByAdminTypeOrderGreaterThanEqualOrderByAdminTypeOrderAsc(Integer adminTypeOrder);

    // adminTypeOrder 값을 1씩 증가
    @Modifying
    @Query("UPDATE adminType a SET a.adminTypeOrder = a.adminTypeOrder + 1 WHERE a.adminTypeOrder >= :startOrder")
    void incrementAdminTypeOrderFromPosition(@Param("startOrder") Integer startOrder);

    // 특정 범위의 adminTypeOrder 값을 1씩 증가
    @Modifying
    @Query("UPDATE adminType a SET a.adminTypeOrder = a.adminTypeOrder + 1 WHERE a.adminTypeOrder >= :startOrder AND a.adminTypeOrder <= :endOrder")
    void incrementAdminTypeOrderBetween(@Param("startOrder") Integer startOrder, @Param("endOrder") Integer endOrder);

    // 특정 범위의 adminTypeOrder 값을 1씩 감소
    @Modifying
    @Query("UPDATE adminType a SET a.adminTypeOrder = a.adminTypeOrder - 1 WHERE a.adminTypeOrder >= :startOrder AND a.adminTypeOrder <= :endOrder")
    void decrementAdminTypeOrderBetween(@Param("startOrder") Integer startOrder, @Param("endOrder") Integer endOrder);

    // 특정 위치 이후의 adminTypeOrder 값을 1씩 감소
    @Modifying
    @Query("UPDATE adminType a SET a.adminTypeOrder = a.adminTypeOrder - 1 WHERE a.adminTypeOrder >= :startOrder")
    void decrementAdminTypeOrderFromPosition(@Param("startOrder") Integer startOrder);

    // 최대 adminTypeOrder 값 조회
    @Query("SELECT MAX(a.adminTypeOrder) FROM adminType a")
    Optional<Integer> findMaxAdminTypeOrder();

    // 최대 adminTypeIndex 값 조회
    @Query("SELECT MAX(a.adminTypeIndex) FROM adminType a")
    Optional<Integer> findMaxAdminTypeIndex();

    // 특정 adminTypeIndex와 관련된 모든 권한 기능 삭제
    @Modifying
    @Query(value = "DELETE FROM authority_type WHERE admin_type_index = :adminTypeIndex", nativeQuery = true)
    void deleteAllAuthoritiesByAdminTypeIndex(@Param("adminTypeIndex") Integer adminTypeIndex);
} 