package com.jakdang.labs.api.taekjun.signin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.UserCmLogValueType;

@Repository
public interface UserCmLogValueTypeRepository extends JpaRepository<UserCmLogValueType, Integer> {
    
    @Query("SELECT u FROM UserCmLogValueType u WHERE u.userCmLogValueTypeName = :valueTypeName")
    Optional<UserCmLogValueType> findByValueTypeName(@Param("valueTypeName") String valueTypeName);
} 