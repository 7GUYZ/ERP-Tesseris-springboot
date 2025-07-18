package com.jakdang.labs.api.taekjun.Permissionsettings.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jakdang.labs.entity.adminType;



@Repository
public interface AdminTypeRepository extends JpaRepository<adminType, Integer> {
    Optional<adminType> findByAdminTypeIndex(Integer adminTypeIndex);
} 