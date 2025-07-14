package com.jakdang.labs.api.cmsAccessLog.repository;

import com.jakdang.labs.api.cmsAccessLog.entity.AdminType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface AdminTypeRepository extends JpaRepository<AdminType, Long> {
} 