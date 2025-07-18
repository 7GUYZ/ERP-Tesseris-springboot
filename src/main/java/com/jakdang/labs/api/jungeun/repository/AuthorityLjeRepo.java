package com.jakdang.labs.api.jungeun.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.AuthorityType;

import feign.Param;

@Repository
public interface AuthorityLjeRepo extends JpaRepository<AuthorityType, Integer> {
    @Query("SELECT at FROM AuthorityType at WHERE at.adminTypeIndex.adminTypeIndex = :adminTypeIndex")
   List<AuthorityType> findByAdminTypeIndex(@Param("adminTypeIndex") Integer adminTypeIndex);
}
