package com.jakdang.labs.api.dabin.FrontEventCouponList.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

import com.jakdang.labs.api.dabin.FrontEventCouponList.repository.EventListDetailRepository;
import com.jakdang.labs.api.dabin.FrontEventCouponList.repository.EventListRepository;
import com.jakdang.labs.api.dabin.FrontEventCouponRegistration.repository.EventMasterRepository;
import com.jakdang.labs.entity.EventMaster;

@Service
@RequiredArgsConstructor
public class StoreContactInfoService {
    private final EventMasterRepository eventMasterRepository;
    private final EventListDetailRepository eventListDetailRepository;

    /**
     * eventMasterIndex로 가맹점 전화, 주소, CM 정보 조회
     */
    public Map<String, Object> getStoreContactInfo(Integer eventMasterIndex) {
        // 1. 이벤트 마스터에서 userIndex 조회
        EventMaster eventMaster = eventMasterRepository.findById(eventMasterIndex)
            .orElse(null);
        if (eventMaster == null) return null;
        Integer userIndex = eventMaster.getEventMasterUserIndex();

        // 2. 가맹점 정보 조회 (전화, 주소, CM)
        var storeResults = eventListDetailRepository.findStoreInfoByEventMasterUserIndex(userIndex);
        if (storeResults == null || storeResults.isEmpty()) return null;
        Object[] storeRow = storeResults.get(0);

        Map<String, Object> result = new HashMap<>();
        result.put("storePhone", storeRow[2]); // 전화번호
        result.put("storeAddress", storeRow[3]); // 주소
        result.put("userCmUse", storeRow[6]); // 사용 가능 CM
        return result;
    }
} 