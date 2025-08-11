package com.jakdang.labs.api.jihun.memberassetdetails.repository;

import com.jakdang.labs.entity.UserCmLogTransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AjgUserCmLogTransactionTypeRepository extends JpaRepository<UserCmLogTransactionType, Integer> {
    Optional<UserCmLogTransactionType> findByUserCmLogTransactionTypeIndex(Integer transactionTypeIndex);
} 