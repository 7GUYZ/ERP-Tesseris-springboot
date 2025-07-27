package com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreOperation.service;

import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreBasicInfo.repository.StoreInfoJdbRepo;
import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreOperation.dto.BusinessHoursDto;
import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreOperation.dto.StoreOperationInfoResponse;
import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreOperation.dto.StoreOperationUpdateRequest;
import com.jakdang.labs.api.dabin.FrontStoreInfoMenu.StoreOperation.repository.StoreBusinessHoursJdbRepo;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.UserTesserisRepository;
import com.jakdang.labs.entity.Store;
import com.jakdang.labs.entity.StoreBusinessHours;
import com.jakdang.labs.entity.UserTesseris;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreOperationService {
    
    private final StoreInfoJdbRepo storeInfoRepository;
    private final StoreBusinessHoursJdbRepo storeBusinessHoursRepository;
    private final UserTesserisRepository userTesserisRepository;
    
    // JWT 기반 가맹점 운영정보 조회
    public Map<String, Object> getStoreOperationInfoByUserId(String userId) {
        System.out.println("🔍 [Service] JWT getStoreOperationInfoByUserId 시작 - userId: " + userId);
        Map<String, Object> result = new HashMap<>();
        
        try {
            // userId로 UserTesseris 조회
            UserTesseris userTesseris = userTesserisRepository.findByUsersId_Id(userId)
                .orElseThrow(() -> new RuntimeException("UserTesseris not found for userId: " + userId));
            
            Integer userIndex = userTesseris.getUserIndex();
            System.out.println("🔍 [Service] JWT userIndex 조회: " + userIndex);
            
            // 기존 메서드 호출
            return getStoreOperationInfo(userIndex);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "운영정보 조회 중 오류가 발생했습니다: " + e.getMessage());
            return result;
        }
    }
    
    // JWT 기반 가맹점 운영정보 수정
    @Transactional
    public Map<String, Object> updateStoreOperationInfoByUserId(String userId, StoreOperationUpdateRequest request) {
        System.out.println("🔍 [Service] JWT updateStoreOperationInfoByUserId 시작 - userId: " + userId);
        Map<String, Object> result = new HashMap<>();
        
        try {
            // userId로 UserTesseris 조회
            UserTesseris userTesseris = userTesserisRepository.findByUsersId_Id(userId)
                .orElseThrow(() -> new RuntimeException("UserTesseris not found for userId: " + userId));
            
            Integer userIndex = userTesseris.getUserIndex();
            System.out.println("🔍 [Service] JWT userIndex 조회: " + userIndex);
            
            // 기존 메서드 호출
            return updateStoreOperationInfo(userIndex, request);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "운영정보 수정 중 오류가 발생했습니다: " + e.getMessage());
            return result;
        }
    }
    
    public Map<String, Object> getStoreOperationInfo(Integer userIndex) {
        System.out.println("🔍 [Service] getStoreOperationInfo 시작 - userIndex: " + userIndex);
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 가맹점 정보 조회
            System.out.println("🔍 [Service] Store 정보 조회 시작");
            Optional<Store> storeOpt = storeInfoRepository.findByUserIndex(userIndex);
            System.out.println("🔍 [Service] Store 조회 결과: " + storeOpt.isPresent());
            
            if (!storeOpt.isPresent()) {
                System.out.println("❌ [Service] Store 정보 없음 - userIndex: " + userIndex);
                result.put("success", false);
                result.put("message", "가맹점 정보를 찾을 수 없습니다.");
                return result;
            }
            
            Store store = storeOpt.get();
            System.out.println("✅ [Service] Store 정보 조회 성공 - storeIndex: " + store.getStoreIndex());
            
            // 영업시간 정보 조회
            System.out.println("🔍 [Service] 영업시간 정보 조회 시작");
            List<StoreBusinessHours> businessHoursList = storeBusinessHoursRepository.findByUserIndex(userIndex);
            System.out.println("🔍 [Service] 영업시간 조회 결과 개수: " + businessHoursList.size());
            
            // 조회된 영업시간 상세 정보 출력
            for (StoreBusinessHours hours : businessHoursList) {
                System.out.println("🔍 [Service] 조회된 영업시간 - 인덱스: " + hours.getStoreBusinessHoursIndex() + 
                                 ", 시작시간: " + hours.getStoreStartBusinessHour() + 
                                 ", 종료시간: " + hours.getStoreEndBusinessHour());
            }
            
            List<BusinessHoursDto> businessHoursDtos = businessHoursList.stream()
                .map(this::convertToBusinessHoursDto)
                .collect(Collectors.toList());
            
            // 응답 데이터 생성
            System.out.println("🔍 [Service] 응답 데이터 생성 시작");
            StoreOperationInfoResponse response = new StoreOperationInfoResponse(
                businessHoursDtos,
                store.getStoreHolidayStatus(),
                store.getStoreRegularClosingInterval(),
                store.getStoreRegularClosingWeek(),
                store.getStoreTemporaryClosingDate(),
                store.getStoreTemporaryClosingComment()
            );
            
            System.out.println("✅ [Service] 응답 데이터 생성 완료");
            System.out.println("✅ [Service] holidayStatus: " + store.getStoreHolidayStatus());
            System.out.println("✅ [Service] businessHours 개수: " + businessHoursDtos.size());
            
            result.put("success", true);
            result.put("data", response);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "운영정보 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return result;
    }
    
    @Transactional
    public Map<String, Object> updateStoreOperationInfo(Integer userIndex, StoreOperationUpdateRequest request) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 가맹점 정보 조회
            Optional<Store> storeOpt = storeInfoRepository.findByUserIndex(userIndex);
            if (!storeOpt.isPresent()) {
                result.put("success", false);
                result.put("message", "가맹점 정보를 찾을 수 없습니다.");
                return result;
            }
            
            Store store = storeOpt.get();
            
            // 삭제할 영업시간 처리
            if (request.getRemoveList() != null && !request.getRemoveList().isEmpty()) {
                System.out.println("🔍 [Service] 삭제할 영업시간 인덱스들: " + request.getRemoveList());
                for (Integer index : request.getRemoveList()) {
                    System.out.println("🔍 [Service] 영업시간 삭제 중 - 인덱스: " + index);
                    Optional<StoreBusinessHours> toDelete = storeBusinessHoursRepository.findById(index);
                    if (toDelete.isPresent()) {
                        System.out.println("✅ [Service] 삭제할 영업시간 찾음: " + toDelete.get().getStoreBusinessHoursIndex());
                        storeBusinessHoursRepository.deleteById(index);
                        System.out.println("✅ [Service] 영업시간 삭제 완료 - 인덱스: " + index);
                    } else {
                        System.out.println("❌ [Service] 삭제할 영업시간을 찾을 수 없음 - 인덱스: " + index);
                    }
                }
            } else {
                System.out.println("🔍 [Service] 삭제할 영업시간 없음");
            }
            
            // 영업시간 정보 업데이트/추가
            if (request.getBusinessHours() != null) {
                for (BusinessHoursDto dto : request.getBusinessHours()) {
                    if (dto.getStoreBusinessHoursIndex() != null) {
                        // 기존 영업시간 업데이트
                        Optional<StoreBusinessHours> existingOpt = storeBusinessHoursRepository.findById(dto.getStoreBusinessHoursIndex());
                        if (existingOpt.isPresent()) {
                            StoreBusinessHours existing = existingOpt.get();
                            updateBusinessHoursFromDto(existing, dto);
                            storeBusinessHoursRepository.save(existing);
                        }
                    } else {
                        // 새 영업시간 추가
                        StoreBusinessHours newBusinessHours = new StoreBusinessHours();
                        newBusinessHours.setStoreUserIndex(store);
                        updateBusinessHoursFromDto(newBusinessHours, dto);
                        storeBusinessHoursRepository.save(newBusinessHours);
                    }
                }
            }
            
            // 가맹점 운영정보 업데이트
            store.setStoreHolidayStatus(request.getHolidayStatus());
            store.setStoreRegularClosingInterval(request.getRegularClosingInterval());
            store.setStoreRegularClosingWeek(request.getRegularClosingWeek());
            store.setStoreTemporaryClosingDate(request.getTemporaryClosingDate());
            store.setStoreTemporaryClosingComment(request.getTemporaryClosingComment());
            
            storeInfoRepository.save(store);
            
            result.put("success", true);
            result.put("message", "운영정보가 성공적으로 수정되었습니다.");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "운영정보 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return result;
    }
    
    private BusinessHoursDto convertToBusinessHoursDto(StoreBusinessHours entity) {
        List<String> businessDays = Arrays.asList(entity.getStoreBusinessDate().split(","));
        return new BusinessHoursDto(
            entity.getStoreBusinessHoursIndex(),
            entity.getStoreStartBusinessHour(),
            entity.getStoreEndBusinessHour(),
            entity.getStoreRestStatus(),
            entity.getStoreRestStartHour(),
            entity.getStoreRestEndHour(),
            businessDays
        );
    }
    
    private void updateBusinessHoursFromDto(StoreBusinessHours entity, BusinessHoursDto dto) {
        entity.setStoreStartBusinessHour(dto.getWorkStartTime());
        entity.setStoreEndBusinessHour(dto.getWorkEndTime());
        entity.setStoreRestStatus(dto.getRestTime());
        entity.setStoreRestStartHour(dto.getRestStartTime());
        entity.setStoreRestEndHour(dto.getRestEndTime());
        
        // 요일 정보를 콤마로 구분된 문자열로 변환
        String businessDate = String.join(",", dto.getBusinessDays());
        entity.setStoreBusinessDate(businessDate);
    }
} 