package com.jakdang.labs.api.deokkyu.store.repository;

import com.jakdang.labs.entity.StoreCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HDKStoreCustomerRepository extends JpaRepository<StoreCustomer, Long> {
    List<StoreCustomer> findByStoreStoreUserIndex(String storeStoreUserIndex);
} 