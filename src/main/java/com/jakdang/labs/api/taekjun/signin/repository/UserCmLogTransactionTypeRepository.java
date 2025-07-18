package com.jakdang.labs.api.taekjun.signin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.UserCmLogTransactionType;

@Repository
public interface UserCmLogTransactionTypeRepository extends JpaRepository<UserCmLogTransactionType, Integer> {
    
    @Query("SELECT u FROM UserCmLogTransactionType u WHERE u.userCmLogTransactionTypeName = :transactionTypeName")
    Optional<UserCmLogTransactionType> findByTransactionTypeName(@Param("transactionTypeName") String transactionTypeName);
} 