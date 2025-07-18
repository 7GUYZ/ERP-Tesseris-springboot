package com.jakdang.labs.api.deokkyu.store.repository;
import com.jakdang.labs.entity.Store;
import com.jakdang.labs.entity.UserTesseris;

import org.springframework.data.jpa.repository.JpaRepository;


public interface StorehdkRepository extends JpaRepository<Store, Integer> {

    Store findByUserIndex(UserTesseris userTesseris);
}

