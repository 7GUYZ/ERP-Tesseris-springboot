package com.jakdang.labs.api.jiyun.cmsAccessLog.repository;

import com.jakdang.labs.entity.adminType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface AdminTypekjyRepository extends JpaRepository<adminType, Integer> {
} 