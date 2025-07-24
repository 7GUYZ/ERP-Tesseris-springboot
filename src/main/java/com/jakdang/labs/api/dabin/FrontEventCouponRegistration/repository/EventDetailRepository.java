package com.jakdang.labs.api.dabin.FrontEventCouponRegistration.repository;

import com.jakdang.labs.entity.EventDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventDetailRepository extends JpaRepository<EventDetail, Integer> {
} 