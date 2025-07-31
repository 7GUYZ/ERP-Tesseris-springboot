package com.jakdang.labs.api.jungeun.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.Advertisement;

@Repository
public interface AdvertisementLjeRepo extends JpaRepository<Advertisement, Integer>{
    
    @Query("SELECT a FROM Advertisement a ORDER BY a.advertisementIndex")
    List<Advertisement> findActiveAdvertisements();
}
