package com.jakdang.labs.api.deokkyu.businessman.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.jakdang.labs.api.deokkyu.businessman.dto.BusinessmanListDto;
import com.jakdang.labs.api.deokkyu.businessman.dto.BusinessmanSearchDto;
import com.jakdang.labs.api.deokkyu.businessman.dto.OrgChartDto;
import com.jakdang.labs.api.deokkyu.businessman.repository.TemporaryStoreMasterhdkRepo;
import com.jakdang.labs.api.deokkyu.businessman.repository.TemporaryStoreDetailhdkRepo;
import com.jakdang.labs.api.deokkyu.businessman.repository.StoreBusinessmanhdkRepo;
import com.jakdang.labs.api.deokkyu.businessman.repository.BusinessManBusinessmanhdkRepo;
import com.jakdang.labs.entity.TemporaryStoreMaster;
import com.jakdang.labs.entity.TemporaryStoreDetail;
import com.jakdang.labs.entity.Store;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.entity.BusinessMan;
import com.jakdang.labs.api.auth.entity.UserEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@RequiredArgsConstructor
@Service
public class BusinessmanService {
    
    private final TemporaryStoreMasterhdkRepo temporaryStoreMasterRepository;
    private final TemporaryStoreDetailhdkRepo temporaryStoreDetailRepository;
    private final StoreBusinessmanhdkRepo storeRepository;
    private final BusinessManBusinessmanhdkRepo businessmanRepository;
    
    public List<BusinessmanListDto> getAllowanceListDtos(BusinessmanSearchDto filter) {
        try {
            System.out.println("=== BusinessmanService.getAllowanceListDtos 시작 ===");
            System.out.println("Input filter: " + filter);
            
            // 날짜 파싱 - YYYY-MM-DD 형태의 문자열을 LocalDateTime으로 변환
            LocalDateTime distributionTimeStart = null;
            LocalDateTime distributionTimeEnd = null;
        
        if (filter.getTemporaryStoreMasterChargeTimeStart() != null && !filter.getTemporaryStoreMasterChargeTimeStart().isBlank()) {
            distributionTimeStart = LocalDateTime.parse(filter.getTemporaryStoreMasterChargeTimeStart() + " 00:00:00", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        
        if (filter.getTemporaryStoreMasterChargeTimeEnd() != null && !filter.getTemporaryStoreMasterChargeTimeEnd().isBlank()) {
            distributionTimeEnd = LocalDateTime.parse(filter.getTemporaryStoreMasterChargeTimeEnd() + " 23:59:59", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        
        // Repository에서 모든 데이터 조회
        System.out.println("Repository 조회 시작...");
        List<TemporaryStoreMaster> masterList = temporaryStoreMasterRepository.findAllByOrderByTemporaryStoreMasterIndexDesc();
        System.out.println("전체 Master 데이터 " + masterList.size() + "개 조회됨");
        
        List<BusinessmanListDto> result = new ArrayList<>();
        
        for (TemporaryStoreMaster master : masterList) {
            System.out.println("Processing master: " + master.getTemporaryStoreMasterIndex());
            
            // ==================== Master 레벨 필터링 ====================
            // temporary_store_master.store_user_index는 이미 Store 엔티티로 매핑됨
            Store store = master.getStoreUserIndex();
            System.out.println("Store: " + (store != null ? store.getStoreName() : "null"));
            
            // 가맹점명 필터
            if (filter.getStoreName() != null && !filter.getStoreName().isBlank()) {
                if (store == null || store.getStoreName() == null || !store.getStoreName().contains(filter.getStoreName())) {
                    continue;
                }
            }
            
            // 분배시간 범위 필터 (distribution_time 기준으로 필터링)
            if (distributionTimeStart != null && master.getTemporaryStoreMasterDistributionTime() != null) {
                if (master.getTemporaryStoreMasterDistributionTime().isBefore(distributionTimeStart)) {
                    continue;
                }
            }
            
            if (distributionTimeEnd != null && master.getTemporaryStoreMasterDistributionTime() != null) {
                if (master.getTemporaryStoreMasterDistributionTime().isAfter(distributionTimeEnd)) {
                    continue;
                }
            }
            
            // 해당 마스터에 연결된 디테일 조회
            List<TemporaryStoreDetail> detailList = temporaryStoreDetailRepository.findByTemporaryStoreMasterIndex(master);
            System.out.println("Detail 개수: " + detailList.size());
            
            for (TemporaryStoreDetail detail : detailList) {
                
                // ==================== 사업자 정보 추출 ====================
                // temporary_store_detail.user_index → user_tesseris.user_index → users_id → users 테이블
                UserTesseris businessUser = detail.getUserIndex();
                String businessUserId = null;      // users.id
                String businessUserName = null;    // users.name  
                String businessUserPhone = null;   // users.phone
                
                if (businessUser != null && businessUser.getUsersId() != null) {
                    UserEntity businessUserEntity = businessUser.getUsersId();
                    businessUserId = businessUserEntity.getId();        // BusinessmanListDto.businessUserId
                    businessUserName = businessUserEntity.getName();    // BusinessmanListDto.businessUserName
                    businessUserPhone = businessUserEntity.getPhone();  // BusinessmanListDto.businessUserPhone
                }
                
                // ==================== 가맹점 정보 추출 ====================
                // temporary_store_master.store_user_index → store.store_index → store 테이블 조회
                // store.user_index → user_tesseris.user_index → users_id → users 테이블
                String storeName = null;       // store.store_name
                String storeUserId = null;     // users.id (가맹점 회원 ID)
                String storeUserName = null;   // users.name (가맹점 회원 이름)
                
                if (store != null) {
                    // store.store_name 
                    storeName = store.getStoreName();  // BusinessmanListDto.storeName
                    
                    // store.user_index → user_tesseris → users
                    UserTesseris storeUser = store.getUserIndex();
                    if (storeUser != null && storeUser.getUsersId() != null) {
                        UserEntity storeUserEntity = storeUser.getUsersId();
                        storeUserId = storeUserEntity.getId();      // BusinessmanListDto.storeUserId
                        storeUserName = storeUserEntity.getName();  // BusinessmanListDto.storeUserName
                    }
                }
                
                // 추가 필터링 검사
                // 가맹점 ID 필터
                if (filter.getUserId() != null && !filter.getUserId().isBlank()) {
                    if (storeUserId == null || !storeUserId.contains(filter.getUserId())) {
                        continue;
                    }
                }
                
                // 가맹점 회원 이름 필터
                if (filter.getUserName() != null && !filter.getUserName().isBlank()) {
                    if (storeUserName == null || !storeUserName.contains(filter.getUserName())) {
                        continue;
                    }
                }
                
                // 사업자 ID 필터
                if (filter.getBusinessUserId() != null && !filter.getBusinessUserId().isBlank()) {
                    if (businessUserId == null || !businessUserId.equals(filter.getBusinessUserId())) {
                        continue;
                    }
                }
                
                // 사업자 이름 필터
                if (filter.getBusinessUserName() != null && !filter.getBusinessUserName().isBlank()) {
                    if (businessUserName == null || !businessUserName.equals(filter.getBusinessUserName())) {
                        continue;
                    }
                }
                
                // 사업자 등급 필터
                if (filter.getBusinessGradeName() != null && !filter.getBusinessGradeName().isBlank() && !filter.getBusinessGradeName().equals("전체")) {
                    if (detail.getBusinessGradeName() == null || !detail.getBusinessGradeName().equals(filter.getBusinessGradeName())) {
                        continue;
                    }
                }
                
                // 담당 구역 필터
                if (filter.getBusinessAreaIndex() != null && !filter.getBusinessAreaIndex().isBlank()) {
                    if (detail.getBusinessAreaName() == null || !detail.getBusinessAreaName().contains(filter.getBusinessAreaIndex())) {
                        continue;
                    }
                }
                
                // 사업자 핸드폰 번호 필터
                if (filter.getBusinessUserPhone() != null && !filter.getBusinessUserPhone().isBlank()) {
                    if (businessUserPhone == null || !businessUserPhone.contains(filter.getBusinessUserPhone())) {
                        continue;
                    }
                }
                
                // ==================== 분배시간 정보 ====================
                // temporary_store_master.temporary_store_master_distribution_time
                String distributionTime = null;  // BusinessmanListDto.temporaryStoreMasterDistributionTime
                if (master.getTemporaryStoreMasterDistributionTime() != null) {
                    distributionTime = master.getTemporaryStoreMasterDistributionTime().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }
                
                // ==================== temporary_store_detail 테이블 정보 ====================
                // temporary_store_detail.business_grade_name → BusinessmanListDto.businessGradeName
                // temporary_store_detail.business_area_name → BusinessmanListDto.businessAreaName
                // temporary_store_detail.temporary_store_cm_value → BusinessmanListDto.temporaryStoreCmValue
                // temporary_store_detail.temporary_store_cash_value → BusinessmanListDto.temporaryStoreCashValue
                // temporaryStoreCmValue + temporaryStoreCashValue → BusinessmanListDto.temporaryStoreTotalValue
                
                Integer cmValue = detail.getTemporaryStoreCmValue() != null ? detail.getTemporaryStoreCmValue().intValue() : 0;
                Integer cashValue = detail.getTemporaryStoreCashValue() != null ? detail.getTemporaryStoreCashValue().intValue() : 0;
                Integer totalValue = cmValue + cashValue;
                
                // ==================== DTO 생성 ====================
                BusinessmanListDto dto = BusinessmanListDto.builder()
                    .businessUserId(businessUserId)                                    // users.id (사업자)
                    .businessUserName(businessUserName)                                // users.name (사업자)
                    .businessGradeName(detail.getBusinessGradeName())                  // temporary_store_detail.business_grade_name
                    .businessUserPhone(businessUserPhone)                              // users.phone (사업자)
                    .businessAreaName(detail.getBusinessAreaName())                    // temporary_store_detail.business_area_name
                    .storeName(storeName)                                              // store.store_name
                    .storeUserId(storeUserId)                                          // users.id (가맹점)
                    .storeUserName(storeUserName)                                      // users.name (가맹점)
                    .temporaryStoreMasterDistributionTime(distributionTime)            // temporary_store_master.temporary_store_master_distribution_time
                    .temporaryStoreCmValue(cmValue)                                    // temporary_store_detail.temporary_store_cm_value
                    .temporaryStoreCashValue(cashValue)                                // temporary_store_detail.temporary_store_cash_value
                    .temporaryStoreTotalValue(totalValue)                              // cmValue + cashValue
                    .build();
                
                result.add(dto);
            }
        }
        
            System.out.println("=== BusinessmanService 완료, 총 " + result.size() + "개 조회 ===");
            return result;
            
        } catch (Exception e) {
            System.err.println("=== BusinessmanService 에러 발생 ===");
            e.printStackTrace();
            throw e;
        }
    }

    public List<OrgChartDto> getOrgChartListDtos() {
        try {
            System.out.println("=== BusinessmanService.getOrgChartListDtos 시작 ===");
            
            // 모든 사업자 조회
            List<BusinessMan> allBusinessmen = businessmanRepository.findAll();
            System.out.println("전체 사업자 데이터 " + allBusinessmen.size() + "개 조회됨");
            
            List<OrgChartDto> result = new ArrayList<>();
            
            for (BusinessMan businessman : allBusinessmen) {
                System.out.println("Processing businessman: " + businessman.getBusinessManIndex());
                
                // ==================== 사업자 기본 정보 추출 ====================
                UserTesseris businessUser = businessman.getUserIndex();
                String businessManId = null;
                String businessManName = null;
                
                if (businessUser != null && businessUser.getUsersId() != null) {
                    UserEntity businessUserEntity = businessUser.getUsersId();
                    businessManId = businessUserEntity.getId();
                    businessManName = businessUserEntity.getName();
                }
                
                // ==================== 등급 정보 추출 ====================
                String businessGradeName = null;
                if (businessman.getBusinessGrade() != null) {
                    businessGradeName = businessman.getBusinessGrade().getBusinessGradeName();
                }
                
                // ==================== 지역 정보 추출 ====================
                String businessAreaName = null;
                if (businessman.getBusinessArea() != null) {
                    businessAreaName = businessman.getBusinessArea().getBusinessAreaName();
                }
                
                // ==================== 상사 정보 추출 ====================
                String bossUserIndex = null;
                if (businessman.getBossUserIndex() != null) {
                    // boss_user_index로 user_tesseris에서 users_id 얻기
                    UserTesseris bossUser = businessmanRepository.findUserTesserisByUserIndex(businessman.getBossUserIndex());
                    if (bossUser != null && bossUser.getUsersId() != null) {
                        bossUserIndex = bossUser.getUsersId().getId();
                    }
                }
                
                // ==================== 현재 총 매장 수 계산 ====================
                Integer currentTotalStore = 0;
                if (businessUser != null) {
                    currentTotalStore = storeRepository.countByBusinessManUserIndex(businessUser.getUserIndex());
                }
                
                // ==================== 하위 사업자들의 총 매장 수 계산 ====================
                Integer totalStore = currentTotalStore;
                totalStore += calculateSubordinateTotalStore(businessUser != null ? businessUser.getUserIndex() : null);
                
                // ==================== 수당 계산 ====================
                Double allowance = 0.0;
                if (businessUser != null) {
                    List<TemporaryStoreDetail> details = temporaryStoreDetailRepository.findByUserIndex(businessUser);
                    for (TemporaryStoreDetail detail : details) {
                        // CM 값만 더함
                        if (detail.getTemporaryStoreCmValue() != null) {
                            allowance += detail.getTemporaryStoreCmValue();
                        }
                    }
                }
                
                // ==================== DTO 생성 ====================
                OrgChartDto dto = OrgChartDto.builder()
                    .businessManId(businessManId)
                    .businessManName(businessManName)
                    .businessGradeName(businessGradeName)
                    .businessAreaName(businessAreaName)
                    .bossUserIndex(bossUserIndex)
                    .currentTotalStore(currentTotalStore)
                    .totalStore(totalStore)
                    .allowance(allowance)
                    .build();
                
                result.add(dto);
            }
            
            System.out.println("=== BusinessmanService.getOrgChartListDtos 완료, 총 " + result.size() + "개 조회 ===");
            return result;
            
        } catch (Exception e) {
            System.err.println("=== BusinessmanService.getOrgChartListDtos 에러 발생 ===");
            e.printStackTrace();
            throw e;
        }
    }
    
    // 재귀적으로 하위 사업자들의 총 매장 수를 계산하는 메서드
    private Integer calculateSubordinateTotalStore(Integer bossUserIndex) {
        if (bossUserIndex == null) {
            return 0;
        }
        
        List<BusinessMan> subordinates = businessmanRepository.findByBossUserIndex(bossUserIndex);
        Integer totalSubordinateStores = 0;
        
        for (BusinessMan subordinate : subordinates) {
            UserTesseris subordinateUser = subordinate.getUserIndex();
            if (subordinateUser != null) {
                // 하위 사업자의 현재 매장 수
                Integer subordinateCurrentStoreCount = storeRepository.countByBusinessManUserIndex(subordinateUser.getUserIndex());
                totalSubordinateStores += subordinateCurrentStoreCount;
                
                // 하위 사업자의 하위 사업자들의 매장 수 (재귀 호출)
                totalSubordinateStores += calculateSubordinateTotalStore(subordinateUser.getUserIndex());
            }
        }
        
        return totalSubordinateStores;
    }
}
