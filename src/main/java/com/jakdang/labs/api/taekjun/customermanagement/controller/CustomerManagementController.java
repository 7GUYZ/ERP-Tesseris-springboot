package com.jakdang.labs.api.taekjun.customermanagement.controller;

import com.jakdang.labs.api.taekjun.customermanagement.dto.CustomerListResponseDTO;
import com.jakdang.labs.api.taekjun.customermanagement.dto.CustomerUpdateDTO;
import com.jakdang.labs.api.taekjun.customermanagement.service.CustomerManagementService;
import com.jakdang.labs.entity.StoreCustomer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/customer-management")
@RequiredArgsConstructor
@Slf4j
public class CustomerManagementController {
    
    private final CustomerManagementService customerManagementService;
    
    /**
     * 고객 목록 조회
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getCustomerList(
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String member,
            @RequestParam(defaultValue = "1") Integer storeUserIndex) {
        
        log.info("고객 목록 조회 요청 - phone: {}, member: {}, storeUserIndex: {}", phone, member, storeUserIndex);
        
        try {
            List<CustomerListResponseDTO> customers = customerManagementService.getCustomerList(storeUserIndex, phone, member);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("customers", customers);
            response.put("message", "고객 목록을 성공적으로 조회했습니다.");
            
            log.info("고객 목록 조회 완료 - 조회된 고객 수: {}", customers.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("고객 목록 조회 중 오류 발생", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "고객 목록을 불러오는데 실패했습니다.");
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * user_index로 고객 정보 조회 (추천인처럼 매핑)
     */
    @GetMapping("/customer-by-user-index")
    public ResponseEntity<Map<String, Object>> getCustomerByUserIndex(@RequestParam String userIndex) {
        log.info("user_index로 고객 정보 조회 요청 - userIndex: {}", userIndex);
        
        try {
            Optional<StoreCustomer> customer = customerManagementService.getCustomerByUserIndex(userIndex);
            
            Map<String, Object> response = new HashMap<>();
            
            if (customer.isPresent()) {
                StoreCustomer customerData = customer.get();
                response.put("success", true);
                response.put("customer", Map.of(
                    "storeCustomerIndex", customerData.getStoreCustomerIndex(),
                    "storeStoreUserIndex", customerData.getStoreStoreUserIndex(),
                    "storeCustomerUserIndex", customerData.getStoreCustomerUserIndex(),
                    "storeCustomerStatus", customerData.getStoreCustomerStatus(),
                    "storeCustomerInsertDate", customerData.getStoreCustomerInsertDate()
                ));
                response.put("message", "고객 정보를 성공적으로 조회했습니다.");
                log.info("고객 정보 조회 완료");
            } else {
                response.put("success", false);
                response.put("message", "해당 user_index의 고객 정보가 없습니다.");
                log.info("고객 정보 없음");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("고객 정보 조회 중 오류 발생", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "고객 정보 조회 중 오류가 발생했습니다.");
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * user_index로 고객 상태 조회
     */
    @GetMapping("/customer-status")
    public ResponseEntity<Map<String, Object>> getCustomerStatusByUserIndex(@RequestParam String userIndex) {
        log.info("user_index로 고객 상태 조회 요청 - userIndex: {}", userIndex);
        
        try {
            String status = customerManagementService.getCustomerStatusByUserIndex(userIndex);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("status", status);
            response.put("userIndex", userIndex);
            response.put("message", "고객 상태를 성공적으로 조회했습니다.");
            
            log.info("고객 상태 조회 완료 - status: {}", status);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("고객 상태 조회 중 오류 발생", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "고객 상태 조회 중 오류가 발생했습니다.");
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 특정 가맹점의 고객 중 user_index로 조회
     */
    @GetMapping("/store-customer")
    public ResponseEntity<Map<String, Object>> getCustomerByStoreAndUserIndex(
            @RequestParam String storeUserIndex,
            @RequestParam String customerUserIndex) {
        log.info("가맹점 고객 조회 요청 - storeUserIndex: {}, customerUserIndex: {}", storeUserIndex, customerUserIndex);
        
        try {
            Optional<StoreCustomer> customer = customerManagementService.getCustomerByStoreAndUserIndex(storeUserIndex, customerUserIndex);
            
            Map<String, Object> response = new HashMap<>();
            
            if (customer.isPresent()) {
                StoreCustomer customerData = customer.get();
                response.put("success", true);
                response.put("customer", Map.of(
                    "storeCustomerIndex", customerData.getStoreCustomerIndex(),
                    "storeStoreUserIndex", customerData.getStoreStoreUserIndex(),
                    "storeCustomerUserIndex", customerData.getStoreCustomerUserIndex(),
                    "storeCustomerStatus", customerData.getStoreCustomerStatus(),
                    "storeCustomerInsertDate", customerData.getStoreCustomerInsertDate()
                ));
                response.put("message", "가맹점 고객 정보를 성공적으로 조회했습니다.");
                log.info("가맹점 고객 정보 조회 완료");
            } else {
                response.put("success", false);
                response.put("message", "해당 가맹점의 고객 정보가 없습니다.");
                log.info("가맹점 고객 정보 없음");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("가맹점 고객 정보 조회 중 오류 발생", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "가맹점 고객 정보 조회 중 오류가 발생했습니다.");
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 고객 상태 변경 (단골/일반 등록)
     */
    @PutMapping("/update-status")
    public ResponseEntity<Map<String, Object>> updateCustomerStatus(@RequestBody CustomerUpdateDTO updateDTO) {
        
        log.info("고객 상태 변경 요청 - customerIndexes: {}, status: {}", updateDTO.getCustomerIndexes(), updateDTO.getStatus());
        
        try {
            boolean success = customerManagementService.updateCustomerStatus(updateDTO);
            
            Map<String, Object> response = new HashMap<>();
            
            if (success) {
                response.put("success", true);
                response.put("message", updateDTO.getCustomerIndexes().size() + "명의 고객 상태를 변경했습니다.");
                log.info("고객 상태 변경 완료");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "변경할 고객을 찾을 수 없습니다.");
                log.warn("고객 상태 변경 실패 - 고객을 찾을 수 없음");
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            log.error("고객 상태 변경 중 오류 발생", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "고객 상태 변경 중 오류가 발생했습니다.");
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 쿠폰 선물
     */
    @PostMapping("/gift-coupon")
    public ResponseEntity<Map<String, Object>> giftCoupon(@RequestBody Map<String, List<Integer>> request) {
        
        List<Integer> customerIndexes = request.get("customerIndexes");
        log.info("쿠폰 선물 요청 - customerIndexes: {}", customerIndexes);
        
        try {
            boolean success = customerManagementService.giftCoupon(customerIndexes);
            
            Map<String, Object> response = new HashMap<>();
            
            if (success) {
                response.put("success", true);
                response.put("message", customerIndexes.size() + "명의 고객에게 쿠폰을 선물했습니다.");
                response.put("redirectUrl", "/qr-pin"); // QR 핀 페이지로 리다이렉트
                log.info("쿠폰 선물 완료");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "쿠폰을 선물할 고객을 찾을 수 없습니다.");
                log.warn("쿠폰 선물 실패 - 고객을 찾을 수 없음");
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            log.error("쿠폰 선물 중 오류 발생", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "쿠폰 선물 중 오류가 발생했습니다.");
            
            return ResponseEntity.badRequest().body(response);
        }
    }
} 