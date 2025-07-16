package com.jakdang.labs.api.jihun.memberaccount.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.jakdang.labs.entity.UserRole;

@Repository
public interface AjhUserRoleRepository extends JpaRepository<UserRole, Long> {
} 