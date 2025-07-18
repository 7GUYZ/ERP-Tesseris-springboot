package com.jakdang.labs.api.deokkyu.store.repository;

import com.jakdang.labs.entity.StoreCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StoreCustomerhdkRepository extends JpaRepository<StoreCustomer, Long> {
    List<StoreCustomer> findByStoreStoreUserIndex(String storeStoreUserIndex); // 고객 수만 반환하려고
    List<StoreCustomer> findByStoreStoreUserIndexIn(List<Integer> storeUserIndexes); // 고객 전체 리스트트
} 