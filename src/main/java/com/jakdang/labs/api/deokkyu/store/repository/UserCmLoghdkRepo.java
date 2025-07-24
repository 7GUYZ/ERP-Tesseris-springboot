package com.jakdang.labs.api.deokkyu.store.repository;

import com.jakdang.labs.entity.UserCmLog;
import com.jakdang.labs.entity.UserTesseris;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface UserCmLoghdkRepo extends JpaRepository<UserCmLog, Integer> {
    
    /**
     * 특정 사용자가 거래 상대방(event_party)으로 참여한 거래 내역 조회
     * @param userTesseris 조회할 사용자의 UserTesseris 객체
     * @return 해당 사용자의 거래 내역 리스트
     */
    List<UserCmLog> findByUserIndexEventPartyOrderByUserCmLogCreateTimeDesc(UserTesseris userTesseris);
    
    /**
     * 특정 사용자 인덱스로 거래 상대방(event_party) 거래 내역 조회
     * @param userIndex 사용자 인덱스
     * @return 해당 사용자의 거래 내역 리스트
     */
    @Query("SELECT ucl FROM UserCmLog ucl WHERE ucl.userIndexEventParty.userIndex = :userIndex ORDER BY ucl.userCmLogCreateTime DESC")
    List<UserCmLog> findByUserIndexEventPartyUserIndexOrderByUserCmLogCreateTimeDesc(@Param("userIndex") Integer userIndex);
} 