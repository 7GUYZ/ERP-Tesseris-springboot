package com.jakdang.labs.api.jihun.memberaccount.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.jakdang.labs.entity.UserCmLogValueType;

@Repository
public interface AjhUserCmLogValueTypeRepository extends JpaRepository<UserCmLogValueType, Long> {
} 