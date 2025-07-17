package com.jakdang.labs.api.taekjun.Permissionsettings.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.AuthorityType;

@Repository
public interface AuthorityTypeRepository extends JpaRepository<AuthorityType, Integer> {
    // 필요하다면 커스텀 메서드 추가 가능
} 