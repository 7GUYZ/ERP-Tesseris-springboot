package com.jakdang.labs.api.deokkyu.store.repository;

import com.jakdang.labs.entity.StoreSubscriptionFee;
import com.jakdang.labs.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StoreSubscriptionFeehdkRepo extends JpaRepository<StoreSubscriptionFee, Integer> {
    List<StoreSubscriptionFee> findByStoreUserIndex(Store storeUserIndex);
} 