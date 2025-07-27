package com.jakdang.labs.api.dabin.FrontEventCouponRegistration.repository;

import com.jakdang.labs.entity.EventMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

@Repository
public interface EventMasterRepository extends JpaRepository<EventMaster, Integer> {
    
    @Query("SELECT COALESCE(MAX(e.eventMasterNum), 0) + 1 FROM EventMaster e")
    Integer getNextEventMasterNum();
    

} 