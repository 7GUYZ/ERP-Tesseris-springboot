package com.chilgayz.tesseris.store.repository;

import com.chilgayz.tesseris.store.entity.StoreCategory;

import org.springframework.data.jpa.repository.JpaRepository;


public interface StoreCategoryRepository extends JpaRepository<StoreCategory, Integer> {

    
}

