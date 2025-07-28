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
import java.util.ArrayList;

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
     * 내 가맹점의 고객 목록 조회 (가맹점명+고객명+상태)
     */
    @GetMapping("/my-customers")
    public ResponseEntity<Map<String, Object>> getMyCustomers(
            @RequestParam String storeUserIndex) {
        
        log.info("내 가맹점 고객 목록 조회 요청 - storeUserIndex: {}", storeUserIndex);
        
        try {
            List<Map<String, Object>> customers = customerManagementService.getMyCustomers(storeUserIndex);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("customers", customers);
            response.put("message", "내 가맹점 고객 목록을 성공적으로 조회했습니다.");
            
            log.info("내 가맹점 고객 목록 조회 완료 - 조회된 고객 수: {}", customers.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("내 가맹점 고객 목록 조회 중 오류 발생", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "내 가맹점 고객 목록을 불러오는데 실패했습니다.");
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 내 가맹점의 특정 고객 조회
     */
    @GetMapping("/my-customer")
    public ResponseEntity<Map<String, Object>> getMyCustomerByUserIndex(
            @RequestParam String storeUserIndex,
            @RequestParam String customerUserIndex) {
        log.info("내 가맹점 특정 고객 조회 요청 - storeUserIndex: {}, customerUserIndex: {}", storeUserIndex, customerUserIndex);
        
        try {
            Optional<Map<String, Object>> customer = customerManagementService.getMyCustomerByUserIndex(storeUserIndex, customerUserIndex);
            
            Map<String, Object> response = new HashMap<>();
            
            if (customer.isPresent()) {
                response.put("success", true);
                response.put("customer", customer.get());
                response.put("message", "내 가맹점 고객 정보를 성공적으로 조회했습니다.");
                log.info("내 가맹점 고객 정보 조회 완료");
            } else {
                response.put("success", false);
                response.put("message", "해당 고객이 내 가맹점에 없습니다.");
                log.info("내 가맹점에 해당 고객 없음");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("내 가맹점 고객 정보 조회 중 오류 발생", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "내 가맹점 고객 정보 조회 중 오류가 발생했습니다.");
            
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
     * 현재 보유 CM 조회
     */
    @GetMapping("/current-cm")
    public ResponseEntity<Map<String, Object>> getCurrentCm(@RequestParam String storeUserIndex) {
        log.info("현재 보유 CM 조회 요청 - storeUserIndex: {}", storeUserIndex);
        
        try {
            Integer userCmIndex = Integer.parseInt(storeUserIndex);
            Integer currentCm = customerManagementService.getCurrentCm(userCmIndex);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("currentCm", currentCm);
            response.put("storeUserIndex", storeUserIndex);
            response.put("message", "현재 보유 CM을 성공적으로 조회했습니다.");
            
            log.info("현재 보유 CM 조회 완료 - currentCm: {}", currentCm);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("현재 보유 CM 조회 중 오류 발생", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "현재 보유 CM 조회 중 오류가 발생했습니다.");
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 쿠폰 선물
     */
    @PostMapping("/gift-coupon")
    public ResponseEntity<Map<String, Object>> giftCoupon(@RequestBody Map<String, Object> request) {
        
        // 요청 데이터 검증
        if (request == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "요청 데이터가 없습니다.");
            return ResponseEntity.badRequest().body(response);
        }
        
        // customerIndexes를 올바르게 처리
        Object customerIndexesObj = request.get("customerIndexes");
        List<Integer> customerIndexes = new ArrayList<>();
        
        if (customerIndexesObj instanceof List) {
            List<?> list = (List<?>) customerIndexesObj;
            for (Object item : list) {
                if (item instanceof Integer) {
                    customerIndexes.add((Integer) item);
                } else if (item instanceof Number) {
                    customerIndexes.add(((Number) item).intValue());
                }
            }
        }
        
        String storeUserIndex = (String) request.get("storeUserIndex");
        Integer couponPrice = (Integer) request.get("couponPrice");
        Integer couponLimit = (Integer) request.get("couponLimit");
        String couponName = (String) request.get("couponName");
        String pinCode = (String) request.get("pinCode");
        
        // 필수 데이터 검증
        if (customerIndexes.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "선택된 고객이 없습니다.");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (storeUserIndex == null || storeUserIndex.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "가맹점 정보가 없습니다.");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (couponPrice == null || couponPrice <= 0) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "쿠폰 금액을 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (couponLimit == null || couponLimit < 1 || couponLimit > 90) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "쿠폰 기한은 1일~90일 사이로 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (couponName == null || couponName.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "쿠폰 이름을 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }
        
        // 핀번호 검증
        if (pinCode == null || pinCode.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "핀번호를 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }
        
        // 핀번호 형식 검증 (6자리 숫자)
        if (!pinCode.matches("\\d{6}")) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "핀번호는 6자리 숫자로 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }
        
        log.info("쿠폰 선물 요청 - customerIndexes: {}, storeUserIndex: {}, couponPrice: {}, couponLimit: {}, couponName: {}, pinCode: {}", 
                customerIndexes, storeUserIndex, couponPrice, couponLimit, couponName, pinCode);
        
        try {
            boolean success = customerManagementService.giftCoupon(customerIndexes, storeUserIndex, couponPrice, couponLimit, couponName, pinCode);
            
            Map<String, Object> response = new HashMap<>();
            
            if (success) {
                response.put("success", true);
                response.put("message", customerIndexes.size() + "명의 고객에게 쿠폰을 성공적으로 발급했습니다.");
                log.info("쿠폰 선물 완료");
                // 쿠폰 선물 알림 전송
                try{
                    customerManagementService.sendCouponAlarm(customerIndexes, storeUserIndex, couponName);
                }catch(Exception e){
                    log.error("쿠폰 선물 알림 전송 실패: {}", e.getMessage());
                    // 알림 전송 실패해도 실패해도 DB 저장은 성공으로 처리
                }
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "보유 CM이 부족하거나 쿠폰을 선물할 고객을 찾을 수 없습니다.");
                log.warn("쿠폰 선물 실패");
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