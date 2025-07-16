package com.jakdang.labs.api.jungeun.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.Admin;

@Repository
public interface AdminLjeRepo extends JpaRepository<Admin, Integer> {
    @Query("SELECT a FROM Admin a WHERE a.userIndex.userIndex = :userIndex")
    Optional<Admin> findByUserIndex(@Param("userIndex") Integer userIndex);
}
