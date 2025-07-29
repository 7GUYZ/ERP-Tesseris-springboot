package com.jakdang.labs.api.deokkyu.store.repository;
import com.jakdang.labs.entity.Store;
import com.jakdang.labs.entity.UserTesseris;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface StorehdkRepo extends JpaRepository<Store, Integer> {

    Store findFirstByUserIndex(UserTesseris userTesseris);
    List<Store> findByUserIndex(UserTesseris userTesseris);
}

