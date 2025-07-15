package com.jakdang.labs.api.deokkyu.store.repository;

import com.jakdang.labs.api.entity.StoreCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StoreCustomerRepository extends JpaRepository<StoreCustomer, Long> {
    List<StoreCustomer> findByStoreStoreUserIndex(Integer storeStoreUserIndex);
} 