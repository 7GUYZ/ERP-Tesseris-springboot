package com.jakdang.labs.api.taekjun.customermanagement.repository;

import com.jakdang.labs.entity.StoreCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerManagementJtjRepo extends JpaRepository<StoreCustomer, Integer> {
    
    @Query("SELECT sc FROM StoreCustomer sc " +
           "WHERE sc.storeStoreUserIndex = :storeUserIndex " +
           "AND (:member = '' OR sc.storeCustomerStatus = :member)")
    List<StoreCustomer> findCustomersByStoreAndFilters(
        @Param("storeUserIndex") String storeUserIndex,
        @Param("member") String member
     );
    
    @Query("SELECT sc FROM StoreCustomer sc " +
           "WHERE sc.storeCustomerIndex IN :customerIndexes")
    List<StoreCustomer> findByCustomerIndexes(@Param("customerIndexes") List<Integer> customerIndexes);
    
    @Query("SELECT sc FROM StoreCustomer sc " +
           "WHERE sc.storeStoreUserIndex = :storeUserIndex")
    List<StoreCustomer> findByStoreUserIndex(@Param("storeUserIndex") String storeUserIndex);
    
    // user_index로 고객 정보 조회 (추천인처럼 매핑)
    @Query("SELECT sc FROM StoreCustomer sc " +
           "WHERE sc.storeCustomerUserIndex = :userIndex")
    Optional<StoreCustomer> findByStoreCustomerUserIndex(@Param("userIndex") String userIndex);
    
    // user_index로 고객 상태 조회
    @Query("SELECT sc.storeCustomerStatus FROM StoreCustomer sc " +
           "WHERE sc.storeCustomerUserIndex = :userIndex")
    Optional<String> findCustomerStatusByUserIndex(@Param("userIndex") String userIndex);
    
    // 특정 가맹점의 고객 중 user_index로 조회
    @Query("SELECT sc FROM StoreCustomer sc " +
           "WHERE sc.storeStoreUserIndex = :storeUserIndex " +
           "AND sc.storeCustomerUserIndex = :customerUserIndex")
    Optional<StoreCustomer> findByStoreAndCustomerUserIndex(
        @Param("storeUserIndex") String storeUserIndex,
        @Param("customerUserIndex") String customerUserIndex
    );
    
    // 내 가맹점의 고객 목록 조회 (가맹점명+고객명+상태)
    @Query("SELECT sc.storeCustomerIndex, sc.storeCustomerStatus, " +
           "s.storeName as storeName, " +
           "u.name as customerName, " +
           "u.phone as customerPhone, " +
           "u.email as customerEmail " +
           "FROM StoreCustomer sc " +
           "JOIN Store s ON s.userIndex.userIndex = CAST(sc.storeStoreUserIndex AS integer) " +
           "JOIN UserTesseris ut ON ut.userIndex = CAST(sc.storeCustomerUserIndex AS integer) " +
           "JOIN ut.usersId u " +
           "WHERE sc.storeStoreUserIndex = :storeUserIndex " +
           "AND (:member IS NULL OR :member = '' OR :member = '전체' OR sc.storeCustomerStatus = :member) " +
           "AND (:phone IS NULL OR :phone = '' OR u.phone LIKE CONCAT('%', :phone, '%'))")
    List<Object[]> findMyCustomersWithInfo(
        @Param("storeUserIndex") String storeUserIndex,
        @Param("member") String member,
        @Param("phone") String phone
    );
    
    // 내 가맹점의 특정 고객 조회
    @Query("SELECT sc.storeCustomerIndex, sc.storeCustomerStatus, " +
           "s.storeName as storeName, " +
           "u.name as customerName, " +
           "u.phone as customerPhone, " +
           "u.email as customerEmail " +
           "FROM StoreCustomer sc " +
           "JOIN Store s ON s.userIndex.userIndex = CAST(sc.storeStoreUserIndex AS integer) " +
           "JOIN UserTesseris ut ON ut.userIndex = CAST(sc.storeCustomerUserIndex AS integer) " +
           "JOIN ut.usersId u " +
           "WHERE sc.storeStoreUserIndex = :storeUserIndex " +
           "AND sc.storeCustomerUserIndex = :customerUserIndex")
    Optional<Object[]> findMyCustomerByUserIndex(
        @Param("storeUserIndex") String storeUserIndex,
        @Param("customerUserIndex") String customerUserIndex
    );
} 