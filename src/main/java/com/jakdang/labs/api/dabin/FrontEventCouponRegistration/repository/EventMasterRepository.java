package com.jakdang.labs.api.dabin.FrontEventCouponRegistration.repository;

import com.jakdang.labs.entity.EventMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

@Repository
public interface EventMasterRepository extends JpaRepository<EventMaster, Integer> {
    
    @Query("SELECT COALESCE(MAX(e.eventMasterNum), 0) + 1 FROM EventMaster e")
    Integer getNextEventMasterNum();
    
    // 중복된 이벤트 이름 체크 (컬럼명 오타 고려)
    @Query("SELECT COUNT(e) > 0 FROM EventMaster e WHERE e.eventMasterName = :eventName")
    boolean existsByEventMasterName(@Param("eventName") String eventName);
    
} 