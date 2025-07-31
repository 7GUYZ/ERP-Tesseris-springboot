package com.jakdang.labs.api.taekjun.customermanagement.service;

import com.jakdang.labs.api.taekjun.customermanagement.dto.CustomerListResponseDTO;
import com.jakdang.labs.api.taekjun.customermanagement.dto.CustomerUpdateDTO;
import com.jakdang.labs.api.taekjun.customermanagement.repository.CustomerManagementJtjRepo;
import com.jakdang.labs.api.taekjun.customermanagement.repository.CouponRepository;
import com.jakdang.labs.api.taekjun.Permissionsettings.repository.UserTesserisRepository;
import com.jakdang.labs.api.taekjun.signin.repository.UserCmRepository;
import com.jakdang.labs.api.taekjun.dashdord.repository.UserCmLogJtjRepo;
import com.jakdang.labs.entity.StoreCustomer;
import com.jakdang.labs.entity.Coupon;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.entity.UserCm;
import com.jakdang.labs.entity.UserCmLog;
import com.jakdang.labs.api.alarm.service.AlarmSvc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerManagementService {

    private final CustomerManagementJtjRepo customerManagementJtjRepo;
    private final CouponRepository couponRepository;
    private final UserTesserisRepository userTesserisRepository;
    private final UserCmRepository userCmRepository;
    private final UserCmLogJtjRepo userCmLogRepository;
    private final AlarmSvc alarmSvc;

    /**
     * 고객 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CustomerListResponseDTO> getCustomerList(Integer storeUserIndex, String phone, String member) {
        log.info("고객 목록 조회 - storeUserIndex: {}, phone: {}, member: {}", storeUserIndex, phone, member);

        List<StoreCustomer> customers = customerManagementJtjRepo.findCustomersByStoreAndFilters(
                String.valueOf(storeUserIndex),
                member != null ? member : "");

        log.info("조회된 고객 수: {}", customers.size());

        return customers.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 내 가맹점의 고객 목록 조회 (가맹점명+고객명+상태)
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMyCustomers(String storeUserIndex) {
        log.info("내 가맹점 고객 목록 조회 - storeUserIndex: {}", storeUserIndex);

        List<Object[]> results = customerManagementJtjRepo.findMyCustomersWithInfo(storeUserIndex);

        List<Map<String, Object>> customerList = results.stream()
                .map(this::toMyCustomerDto)
                .collect(Collectors.toList());

        log.info("조회된 내 고객 수: {}", customerList.size());

        return customerList;
    }

    /**
     * 내 가맹점의 특정 고객 조회
     */
    @Transactional(readOnly = true)
    public Optional<Map<String, Object>> getMyCustomerByUserIndex(String storeUserIndex, String customerUserIndex) {
        log.info("내 가맹점 특정 고객 조회 - storeUserIndex: {}, customerUserIndex: {}", storeUserIndex, customerUserIndex);

        Optional<Object[]> result = customerManagementJtjRepo.findMyCustomerByUserIndex(storeUserIndex,
                customerUserIndex);

        Optional<Map<String, Object>> customerInfo = result.map(this::toMyCustomerDto);

        if (customerInfo.isPresent()) {
            log.info("내 고객 정보 조회 성공");
        } else {
            log.info("해당 고객이 내 가맹점에 없습니다.");
        }

        return customerInfo;
    }

    /**
     * Object[]를 Map으로 변환 (내 고객 정보)
     */
    private Map<String, Object> toMyCustomerDto(Object[] result) {
        return Map.of(
                "storeCustomerIndex", result[0],
                "storeCustomerStatus", result[1],
                "storeName", result[2],
                "customerName", result[3],
                "customerPhone", result[4],
                "customerEmail", result[5]);
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

        Optional<StoreCustomer> customer = customerManagementJtjRepo.findByStoreAndCustomerUserIndex(storeUserIndex,
                customerUserIndex);

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
     * 쿠폰 선물 (즉시 쿠폰 발급)
     */
    @Transactional
    public boolean giftCoupon(List<Integer> customerIndexes, String storeUserIndex,
            Integer couponPrice, Integer couponLimit, String couponName, String pinCode) {
        log.info(
                "쿠폰 선물 - customerIndexes: {}, storeUserIndex: {}, couponPrice: {}, couponLimit: {}, couponName: {}, pinCode: {}",
                customerIndexes, storeUserIndex, couponPrice, couponLimit, couponName, pinCode);

        List<StoreCustomer> customers = customerManagementJtjRepo.findByCustomerIndexes(customerIndexes);

        if (customers.isEmpty()) {
            log.warn("쿠폰을 선물할 고객을 찾을 수 없습니다.");
            return false;
        }

        // 발급자(가맹점) 정보 조회
        Optional<UserTesseris> issuanceUser = userTesserisRepository.findByUserIndex(Integer.parseInt(storeUserIndex));
        if (issuanceUser.isEmpty()) {
            log.warn("발급자 정보를 찾을 수 없습니다. storeUserIndex: {}", storeUserIndex);
            return false;
        }

        // 핀번호 검증 (발급자의 핀번호와 일치하는지 확인)
        Integer userCmIndex = Integer.parseInt(storeUserIndex);
        Optional<UserCm> userCm = userCmRepository.findByUserCmIndex(userCmIndex);
        if (userCm.isEmpty() || userCm.get().getUserCmPincode() == null ||
                !userCm.get().getUserCmPincode().equals(pinCode)) {
            log.warn("핀번호가 일치하지 않습니다. storeUserIndex: {}", storeUserIndex);
            return false;
        }

        // 보유 CM 확인 (UserCm 테이블에서 조회)
        Integer currentCm = getUserCurrentCm(userCmIndex);
        
        // 필요 CM 계산 (쿠폰 금액 × 고객 수)
        Integer needCm = couponPrice * customers.size();
        
        log.info("현재 CM 잔액: {}, 필요 CM: {}, 고객 수: {}", currentCm, needCm, customers.size());
        
        // CM 잔액 확인
        if (currentCm < needCm) {
            log.warn("보유 CM이 부족합니다. 현재: {}, 필요: {}", currentCm, needCm);
            return false;
        }

        // CM 차감
        updateUserCm(userCmIndex, needCm);
        
        // CM 차감 로그 기록
        UserCmLog cmLog = new UserCmLog();
        cmLog.setUserCmLogValue(-needCm); // 차감이므로 음수
        cmLog.setUserCmLogTransactionTypeIndex(14); // 거래 타입 (14: 쿠폰)
        cmLog.setUserCmLogValueTypeIndex(2); // 화폐 단위 (2: CM)
        cmLog.setUserCmLogPaymentIndex(2); // 거래의 종류 (2: 출금)
        cmLog.setUserCmpLogPaymentIndex(null); // CMP 거래의 종류 (사용하지 않음)
        cmLog.setUserCmLogCreateTime(LocalDateTime.now()); // 거래 발생 시간
        cmLog.setUserCmLogReason("쿠폰 발급 - " + customers.size() + "명에게 " + couponName + " 발급"); // 거래에 대한 메모
        cmLog.setUserCmLogTransactionCancel(null); // 취소 (아님)
        cmLog.setUserCouponValue(0); // 쿠폰으로 사용된 금액 (발급이므로 0)
        
        // UserTesseris 객체 생성 - 거래 요청인 (가맹점)
        UserTesseris issuanceUserTesseris = new UserTesseris();
        issuanceUserTesseris.setUserIndex(userCmIndex);
        cmLog.setUserIndexEventTrigger(issuanceUserTesseris);
        
        // UserTesseris 객체 생성 - 거래 상대방 (시스템)
        UserTesseris systemTesseris = new UserTesseris();
        systemTesseris.setUserIndex(0); // 시스템 계정 (임시)
        cmLog.setUserIndexEventParty(systemTesseris);
        
        // CM 로그 저장
        userCmLogRepository.save(cmLog);
        
        log.info("CM 차감 로그 저장 완료 - 가맹점: {}, 차감액: {}, 고객 수: {}", userCmIndex, needCm, customers.size());

        // 각 고객에게 쿠폰 발급
        for (StoreCustomer customer : customers) {
            // 수령자(고객) 정보 조회
            Optional<UserTesseris> providedUser = userTesserisRepository.findByUserIndex(
                    Integer.parseInt(customer.getStoreCustomerUserIndex()));

            if (providedUser.isPresent()) {
                // 쿠폰 생성
                Coupon coupon = new Coupon();
                coupon.setIssuanceUser(issuanceUser.get()); // 발급자 (가맹점)
                coupon.setProvidedUser(providedUser.get()); // 수령자 (고객)
                coupon.setCouponPrice(couponPrice); // 쿠폰 금액
                coupon.setCouponLimit(couponLimit); // 사용 제한
                coupon.setCouponIssuanceStatusIndex(1); // 발급 상태 (1: 발급됨)
                coupon.setCouponProvidedStatusIndex(1); // 제공 상태 (1: 제공됨)
                coupon.setCouponName(couponName); // 쿠폰명
                coupon.setCouponIssuanceTime(LocalDateTime.now()); // 발급 시간
                coupon.setCouponProvidedTime(LocalDateTime.now()); // 제공 시간
                coupon.setCouponLimitTime(LocalDateTime.now().plusDays(couponLimit)); // 만료 시간
                coupon.setCouponCondition("고객 선물용 쿠폰입니다."); // 사용 조건

                // 쿠폰 저장
                couponRepository.save(coupon);

                log.info("고객 {}에게 쿠폰 발급 완료 - 쿠폰 인덱스: {}, 금액: {}",
                        customer.getStoreCustomerUserIndex(), coupon.getCouponIndex(), couponPrice);
            } else {
                log.warn("고객 정보를 찾을 수 없습니다. customerUserIndex: {}",
                        customer.getStoreCustomerUserIndex());
            }
        }

        // 쿠폰 발급 후 user_index 1번 계정에 쿠폰 금액 입금
        try {
            // user_index 1번 계정의 UserCm 조회
            Optional<UserCm> systemUserCm = userCmRepository.findByUserCmIndex(1);
            if (systemUserCm.isPresent()) {
                // user_index 1번 계정에 쿠폰 총 금액 입금
                Integer currentDeposit = systemUserCm.get().getUserCmDeposit() != null ? systemUserCm.get().getUserCmDeposit() : 0;
                systemUserCm.get().setUserCmDeposit(currentDeposit + needCm);
                userCmRepository.save(systemUserCm.get());
                
                // user_index 1번 계정 입금 로그 기록
                UserCmLog systemCmLog = new UserCmLog();
                systemCmLog.setUserCmLogValue(needCm); // 입금이므로 양수
                systemCmLog.setUserCmLogTransactionTypeIndex(15); // 거래 타입 (15: 쿠폰 발급 수익)
                systemCmLog.setUserCmLogValueTypeIndex(2); // 화폐 단위 (2: CM)
                systemCmLog.setUserCmLogPaymentIndex(1); // 거래의 종류 (1: 입금)
                systemCmLog.setUserCmpLogPaymentIndex(null); // CMP 거래의 종류 (사용하지 않음)
                systemCmLog.setUserCmLogCreateTime(LocalDateTime.now()); // 거래 발생 시간
                systemCmLog.setUserCmLogReason("쿠폰 발급 수익 - 가맹점: " + storeUserIndex + ", 쿠폰명: " + couponName + ", 총액: " + needCm + " CM"); // 거래에 대한 메모
                systemCmLog.setUserCmLogTransactionCancel(null); // 취소 (아님)
                systemCmLog.setUserCouponValue(0); // 쿠폰으로 사용된 금액 (입금이므로 0)
                
                // UserTesseris 객체 생성 - 거래 요청인 (시스템)
                UserTesseris systemTriggerTesseris = new UserTesseris();
                systemTriggerTesseris.setUserIndex(1);
                systemCmLog.setUserIndexEventTrigger(systemTriggerTesseris);
                
                // UserTesseris 객체 생성 - 거래 상대방 (가맹점)
                UserTesseris storePartyTesseris = new UserTesseris();
                storePartyTesseris.setUserIndex(Integer.parseInt(storeUserIndex));
                systemCmLog.setUserIndexEventParty(storePartyTesseris);
                
                // 시스템 CM 로그 저장
                userCmLogRepository.save(systemCmLog);
                
                log.info("user_index 1번 계정에 쿠폰 발급 수익 입금 완료 - 입금액: {}, 새로운 deposit: {}", 
                    needCm, systemUserCm.get().getUserCmDeposit());
            } else {
                log.warn("user_index 1번 계정의 UserCm 정보를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            log.error("user_index 1번 계정에 쿠폰 발급 수익 입금 중 오류 발생", e);
            // 쿠폰 발급은 성공했으므로 오류가 발생해도 실패로 처리하지 않음
        }
        
        log.info("{}명의 고객에게 쿠폰을 성공적으로 발급했습니다. 사용된 CM: {}", customers.size(), needCm);
        return true;
    }

    /**
     * 현재 보유 CM 조회 (공개 메서드)
     */
    @Transactional(readOnly = true)
    public Integer getCurrentCm(Integer userCmIndex) {
        return getUserCurrentCm(userCmIndex);
    }

    /**
     * 사용자 현재 CM 조회 (내부 메서드)
     */
    private Integer getUserCurrentCm(Integer userCmIndex) {
        // UserCm 테이블에서 현재 CM 조회
        Optional<UserCm> userCm = userCmRepository.findByUserCmIndex(userCmIndex);
        if (userCm.isPresent()) {
            UserCm cm = userCm.get();
            // CM 잔액 = 입금 + 출금 (출금은 음수)
            Integer currentCm = (cm.getUserCmDeposit() != null ? cm.getUserCmDeposit() : 0) +
                    (cm.getUserCmWithdrawal() != null ? cm.getUserCmWithdrawal() : 0);
            log.info("현재 CM 잔액 조회 - userCmIndex: {}, currentCm: {}", userCmIndex, currentCm);
            return currentCm;
        } else {
            log.warn("UserCm 정보를 찾을 수 없습니다. userCmIndex: {}", userCmIndex);
            return 0;
        }
    }

    /**
     * 사용자 CM 차감
     */
    private void updateUserCm(Integer userCmIndex, Integer useAmount) {
        // UserCm 테이블에서 CM 차감
        Optional<UserCm> userCmOpt = userCmRepository.findByUserCmIndex(userCmIndex);
        if (userCmOpt.isPresent()) {
            UserCm userCm = userCmOpt.get();

            // 현재 출금량에 차감할 금액을 더함 (증가)
            Integer currentWithdrawal = userCm.getUserCmWithdrawal() != null ? userCm.getUserCmWithdrawal() : 0;
            userCm.setUserCmWithdrawal(currentWithdrawal - useAmount);

            // DB에 저장
            userCmRepository.save(userCm);

            log.info("CM 차감 완료 - userCmIndex: {}, 차감액: {}, 새로운 출금량: {}",
                    userCmIndex, useAmount, userCm.getUserCmWithdrawal());
        } else {
            log.error("UserCm 정보를 찾을 수 없어 CM 차감을 할 수 없습니다. userCmIndex: {}", userCmIndex);
        }
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

    /**
     * 쿠폰 선물 알림 전송 (트랜잭션 외부에서 호출)
     */
    public void sendCouponAlarm(List<Integer> customerIndexes, String storeUserIndex, String couponName) {
        try {
            // customerIndexes를 실제 user_index로 변환
            List<String> userIndexes = new ArrayList<>();
            for (Integer customerIndex : customerIndexes) {
                Optional<StoreCustomer> customer = customerManagementJtjRepo.findById(customerIndex);
                if (customer.isPresent()) {
                    String userIndex = customer.get().getStoreCustomerUserIndex();
                    userIndexes.add(userIndex);
                    log.info("고객 인덱스 {} -> 사용자 인덱스 {} 변환", customerIndex, userIndex);
                }
            }
            
            alarmSvc.sendCouponAlarm(userIndexes, storeUserIndex, couponName);
            log.info("쿠폰 선물 알림 전송 완료 - customerIndexes: {}, userIndexes: {}, storeUserIndex: {}, couponName: {}",
                    customerIndexes, userIndexes, storeUserIndex, couponName);
        } catch (Exception e) {
            log.info("쿠폰 선물 알림 전송 실패 - customerIndexes: {}, storeUserIndex: {}, couponName: {}",
                    customerIndexes, storeUserIndex, couponName);
            // 알림 전송 실패해도 DB 저장은 성공으로 처리
        }
    }
}