package com.jakdang.labs.api.taekjun.customermanagement.service;

import com.jakdang.labs.api.taekjun.customermanagement.dto.CustomerListResponseDTO;
import com.jakdang.labs.api.taekjun.customermanagement.dto.CustomerUpdateDTO;
import com.jakdang.labs.entity.StoreCustomer;
import com.jakdang.labs.api.taekjun.customermanagement.repository.CustomerManagementJtjRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerManagementService {
    
    private final CustomerManagementJtjRepo customerManagementJtjRepo;
    
    /**
     * 고객 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CustomerListResponseDTO> getCustomerList(Integer storeUserIndex, String phone, String member) {
        log.info("고객 목록 조회 - storeUserIndex: {}, phone: {}, member: {}", storeUserIndex, phone, member);
        
        List<StoreCustomer> customers = customerManagementJtjRepo.findCustomersByStoreAndFilters(
            String.valueOf(storeUserIndex), 
            member != null ? member : ""
        );
        
        log.info("조회된 고객 수: {}", customers.size());
        
        return customers.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }
    
    /**
     * user_index로 고객 정보 조회 (추천인처럼 매핑)
     */
    @Transactional(readOnly = true)
    public Optional<StoreCustomer> getCustomerByUserIndex(String userIndex) {
        log.info("user_index로 고객 정보 조회 - userIndex: {}", userIndex);
        
        Optional<StoreCustomer> customer = customerManagementJtjRepo.findByStoreCustomerUserIndex(userIndex);
        
        if (customer.isPresent()) {
            log.info("고객 정보 조회 성공 - customerIndex: {}", customer.get().getStoreCustomerIndex());
        } else {
            log.info("해당 user_index의 고객 정보가 없습니다.");
        }
        
        return customer;
    }
    
    /**
     * user_index로 고객 상태 조회
     */
    @Transactional(readOnly = true)
    public String getCustomerStatusByUserIndex(String userIndex) {
        log.info("user_index로 고객 상태 조회 - userIndex: {}", userIndex);
        
        Optional<String> status = customerManagementJtjRepo.findCustomerStatusByUserIndex(userIndex);
        
        String result = status.orElse("정보없음");
        log.info("고객 상태: {}", result);
        
        return result;
    }
    
    /**
     * 특정 가맹점의 고객 중 user_index로 조회
     */
    @Transactional(readOnly = true)
    public Optional<StoreCustomer> getCustomerByStoreAndUserIndex(String storeUserIndex, String customerUserIndex) {
        log.info("가맹점 고객 조회 - storeUserIndex: {}, customerUserIndex: {}", storeUserIndex, customerUserIndex);
        
        Optional<StoreCustomer> customer = customerManagementJtjRepo.findByStoreAndCustomerUserIndex(storeUserIndex, customerUserIndex);
        
        if (customer.isPresent()) {
            log.info("가맹점 고객 정보 조회 성공 - customerIndex: {}", customer.get().getStoreCustomerIndex());
        } else {
            log.info("해당 가맹점의 고객 정보가 없습니다.");
        }
        
        return customer;
    }
    
    /**
     * 고객 상태 변경
     */
    @Transactional
    public boolean updateCustomerStatus(CustomerUpdateDTO updateDTO) {
        log.info("고객 상태 변경 - customerIndexes: {}, status: {}", updateDTO.getCustomerIndexes(), updateDTO.getStatus());
        
        List<StoreCustomer> customers = customerManagementJtjRepo.findByCustomerIndexes(updateDTO.getCustomerIndexes());
        
        if (customers.isEmpty()) {
            log.warn("변경할 고객을 찾을 수 없습니다.");
            return false;
        }
        
        customers.forEach(customer -> {
            customer.setStoreCustomerStatus(updateDTO.getStatus());
        });
        
        customerManagementJtjRepo.saveAll(customers);
        
        log.info("{}명의 고객 상태를 '{}'로 변경했습니다.", customers.size(), updateDTO.getStatus());
        return true;
    }
    
    /**
     * 쿠폰 선물 (QR 핀 페이지로 리다이렉트)
     */
    @Transactional
    public boolean giftCoupon(List<Integer> customerIndexes) {
        log.info("쿠폰 선물 - customerIndexes: {}", customerIndexes);
        
        List<StoreCustomer> customers = customerManagementJtjRepo.findByCustomerIndexes(customerIndexes);
        
        if (customers.isEmpty()) {
            log.warn("쿠폰을 선물할 고객을 찾을 수 없습니다.");
            return false;
        }
        
        // 여기서는 단순히 로그만 남기고, 실제 쿠폰 선물 로직은 QR 핀 페이지에서 처리
        log.info("{}명의 고객에게 쿠폰 선물 요청이 완료되었습니다.", customers.size());
        return true;
    }
    
    /**
     * Entity를 DTO로 변환
     */
    private CustomerListResponseDTO toDto(StoreCustomer customer) {
        // 기존 StoreCustomer 엔티티는 관계 매핑이 없으므로 
        // 실제 구현에서는 별도 쿼리로 사용자 정보를 조회해야 함
        // 현재는 임시 데이터로 처리
        
        String fullName = "고객" + customer.getStoreCustomerIndex(); // 임시
        String fullPhone = "010-1234-" + customer.getStoreCustomerIndex(); // 임시
        String fullEmail = "customer" + customer.getStoreCustomerIndex() + "@example.com"; // 임시
        
        // 이름 마스킹 (첫 2글자만 표시)
        String maskedName = fullName != null && fullName.length() > 2 
            ? fullName.substring(0, 2) + "*" 
            : fullName;
        
        // ID 마스킹 (이메일 기준)
        String maskedId = fullEmail != null && fullEmail.contains("@")
            ? fullEmail.substring(0, 3) + "*"
            : "***";
        
        // 전화번호 뒷 4자리
        String phoneLast4 = fullPhone != null && fullPhone.length() >= 4
            ? fullPhone.substring(fullPhone.length() - 4)
            : "";
        
        return CustomerListResponseDTO.builder()
            .storeCustomerIndex(customer.getStoreCustomerIndex())
            .maskedName(maskedName)
            .maskedId(maskedId)
            .fullPhone(fullPhone)
            .storeCustomerStatus(customer.getStoreCustomerStatus())
            .fullName(fullName)
            .fullEmail(fullEmail)
            .phoneLast4(phoneLast4)
            .build();
    }
} 