package com.jakdang.labs.api.dabin.AdvertisementManagement.repository;

import com.jakdang.labs.entity.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Integer> {
    
    @Query("""
        SELECT a.advertisementIndex, u.usersId.email as userId, 
               a.advertisementPhoto, a.advertisementUrl, a.advertisementCreateTime
        FROM Advertisement a
        INNER JOIN a.userIndex u
        ORDER BY a.advertisementCreateTime DESC
    """)
    List<Object[]> findAllAdvertisementsWithUserInfo();
    
    @Query("""
        SELECT a.advertisementIndex, u.usersId.email as userId, 
               a.advertisementPhoto, a.advertisementUrl, a.advertisementCreateTime
        FROM Advertisement a
        INNER JOIN a.userIndex u
        WHERE a.advertisementIndex = :advertisementIndex
    """)
    List<Object[]> findAdvertisementWithUserInfo(@Param("advertisementIndex") Integer advertisementIndex);
} 