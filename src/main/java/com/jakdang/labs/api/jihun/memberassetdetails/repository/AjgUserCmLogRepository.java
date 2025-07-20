package com.jakdang.labs.api.jihun.memberassetdetails.repository;

import com.jakdang.labs.entity.UserCmLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface AjgUserCmLogRepository extends JpaRepository<UserCmLog, Integer> {
} 