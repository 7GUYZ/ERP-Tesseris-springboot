package com.jakdang.labs.api.jihun.memberassetdetails.repository;

import com.jakdang.labs.entity.UserCmLogValueType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AjgUserCmLogValueTypeRepository extends JpaRepository<UserCmLogValueType, Integer> {
    Optional<UserCmLogValueType> findByUserCmLogValueTypeName(String valueTypeName);
} 