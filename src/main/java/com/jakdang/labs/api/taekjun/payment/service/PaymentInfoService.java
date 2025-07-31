package com.jakdang.labs.api.taekjun.payment.service;

import com.jakdang.labs.api.taekjun.payment.dto.PaymentInfoDTO;
import com.jakdang.labs.api.taekjun.payment.dto.PaymentRequestDTO;
import com.jakdang.labs.api.taekjun.payment.dto.StoreDTO;
import com.jakdang.labs.api.taekjun.payment.dto.CouponDTO;
import com.jakdang.labs.api.taekjun.payment.repository.PaymentJtjRepo;
import com.jakdang.labs.api.taekjun.payment.repository.StoreCustomerRepository;
import com.jakdang.labs.api.jungeun.repository.SettingLjeRepo;
import com.jakdang.labs.api.taekjun.dashdord.repository.UserCmLogJtjRepo;
import com.jakdang.labs.api.taekjun.customermanagement.repository.CouponRepository;
import com.jakdang.labs.api.taekjun.signin.repository.UserCmRepository;
import com.jakdang.labs.entity.Store;
import com.jakdang.labs.entity.Coupon;
import com.jakdang.labs.entity.UserCm;
import com.jakdang.labs.entity.UserCmLog;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.entity.StoreCustomer;
import com.jakdang.labs.entity.Setting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentInfoService {

    private final PaymentJtjRepo paymentJtjRepo;
    private final UserCmLogJtjRepo userCmLogJtjRepo;
    private final StoreCustomerRepository storeCustomerRepository;
    private final CouponRepository couponRepository;
    private final SettingLjeRepo settingLjeRepo;
    private final UserCmRepository userCmRepository;
    
    /**
     * 결제 정보 조회
     */
    @Transactional(readOnly = true)
    public PaymentInfoDTO getPaymentInfo(Integer userIndex) {
        log.info("결제 정보 조회 - userIndex: {}", userIndex);
        
        // 월 결제 한도 (Setting 테이블에서 조회 - setting_index = 2)
        Setting cmLimitSetting = settingLjeRepo.findBySettingIndex(2);
        Integer monthlyLimit = null;
        if (cmLimitSetting != null && cmLimitSetting.getSettingValue() != null) {
            try {
                monthlyLimit = Integer.parseInt(cmLimitSetting.getSettingValue());
                log.info("월 한도 설정값 조회: {}", monthlyLimit);
            } catch (NumberFormatException e) {
                log.warn("월 한도 설정값 파싱 실패: {}", cmLimitSetting.getSettingValue());
            }
        } else {
            log.warn("월 한도 설정값을 찾을 수 없습니다.");
        }
        
        // 이번 달 사용 금액
        String currentMonthStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        log.info("현재 월: {}", currentMonthStr);
        
        Integer monthlyUsed = paymentJtjRepo.getMonthlyUsedCm(userIndex);
        log.info("월별 사용금액 조회 - userIndex: {}, monthlyUsed: {}", userIndex, monthlyUsed);
        
        // 디버깅: 실제 로그 데이터 확인
        try {
            List<Object[]> debugLogs = paymentJtjRepo.getDebugLogs(userIndex);
            log.info("이번 달 구매 로그 개수: {}", debugLogs.size());
            for (Object[] logData : debugLogs) {
                log.info("구매 로그 - 금액: {}, 거래타입: {}, 결제타입: {}, 시간: {}, 사유: {}, 월: {}", 
                    logData[0], logData[1], logData[2], logData[3], logData[4], logData[5]);
            }
            
            // 모든 조건 제거한 로그 확인
            List<Object[]> allUserLogs = paymentJtjRepo.getAllUserLogs(userIndex);
            log.info("이번 달 전체 사용자 로그 개수: {}", allUserLogs.size());
            for (Object[] logData : allUserLogs) {
                log.info("전체 로그 - 금액: {}, 거래타입: {}, 결제타입: {}, 값타입: {}, 시간: {}, 사유: {}, 월: {}", 
                    logData[0], logData[1], logData[2], logData[3], logData[4], logData[5], logData[6]);
            }
            
            // 수동으로 계산해보기
            int manualSum = 0;
            for (Object[] logData : allUserLogs) {
                Integer amount = (Integer) logData[0];
                Integer transactionType = (Integer) logData[1];
                Integer paymentType = (Integer) logData[2];
                Integer valueType = (Integer) logData[3];
                
                if (transactionType == 9 && paymentType == 2 && valueType == 2 && amount < 0) {
                    manualSum += Math.abs(amount);
                    log.info("수동 계산 포함 - 금액: {}, 거래타입: {}, 결제타입: {}, 값타입: {}", 
                        amount, transactionType, paymentType, valueType);
                } else {
                    log.info("수동 계산 제외 - 금액: {}, 거래타입: {}, 결제타입: {}, 값타입: {}", 
                        amount, transactionType, paymentType, valueType);
                }
            }
            log.info("수동 계산 결과: {}", manualSum);
            
        } catch (Exception e) {
            log.warn("로그 디버깅 중 오류: {}", e.getMessage());
        }
        
        // 현재 보유 CM - 더 정확한 계산
        Optional<UserCm> userCm = paymentJtjRepo.findByUserCmIndex(userIndex);
        Integer currentCm = 0;
        if (userCm.isPresent()) {
            UserCm cm = userCm.get();
            Integer deposit = cm.getUserCmDeposit() != null ? cm.getUserCmDeposit() : 0;
            Integer withdrawal = cm.getUserCmWithdrawal() != null ? cm.getUserCmWithdrawal() : 0;
            currentCm = deposit + withdrawal; // withdrawal는 음수로 저장되어 있음
            log.info("보유 CM 계산 - deposit: {}, withdrawal: {}, currentCm: {}", deposit, withdrawal, currentCm);
        } else {
            log.warn("사용자 CM 정보를 찾을 수 없습니다. userIndex: {}", userIndex);
        }
        
        // 현재 월
        String currentMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월"));
        
        PaymentInfoDTO paymentInfo = PaymentInfoDTO.builder()
            .monthlyLimit(monthlyLimit)
            .monthlyUsed(monthlyUsed)
            .currentCm(currentCm)
            .currentMonth(currentMonth)
            .build();
        
        log.info("결제 정보 조회 완료 - monthlyLimit: {}, monthlyUsed: {}, currentCm: {}", 
            monthlyLimit, monthlyUsed, currentCm);
        
        return paymentInfo;
    }
    
    /**
     * 가맹점 목록 조회
     */
    @Transactional(readOnly = true)
    public List<StoreDTO> getStoreList() {
        log.info("가맹점 목록 조회");
        
        List<Store> stores = paymentJtjRepo.findAllActiveStores();
        
        List<StoreDTO> storeList = stores.stream()
            .map(store -> StoreDTO.builder()
                .userIndex(store.getUserIndex() != null ? store.getUserIndex().getUserIndex() : null)
                .storeName(store.getStoreName())
                .build())
            .collect(Collectors.toList());
        
        log.info("가맹점 목록 조회 완료 - 조회된 가맹점 수: {}", storeList.size());
        
        return storeList;
    }
    
    /**
     * 사용자의 쿠폰 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CouponDTO> getUserCoupons(Integer userIndex, String couponName) {
        log.info("사용자 쿠폰 목록 조회 - userIndex: {}, couponName: {}", userIndex, couponName);
        
        List<Coupon> coupons = paymentJtjRepo.findUserCoupons(userIndex, couponName);
        
        List<CouponDTO> couponList = coupons.stream()
            .map(this::toCouponDTO)
            .collect(Collectors.toList());
        
        log.info("사용자 쿠폰 목록 조회 완료 - 조회된 쿠폰 수: {}", couponList.size());
        
        return couponList;
    }
    
    /**
     * 특정 가맹점이 사용자에게 준 쿠폰 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CouponDTO> getStoreCouponsForUser(Integer userIndex, Integer storeUserIndex, String couponName) {
        log.info("가맹점 쿠폰 목록 조회 - userIndex: {}, storeUserIndex: {}, couponName: {}", userIndex, storeUserIndex, couponName);
        
        List<Coupon> coupons = paymentJtjRepo.findStoreCouponsForUser(userIndex, storeUserIndex, couponName);
        
        List<CouponDTO> couponList = coupons.stream()
            .map(this::toCouponDTO)
            .collect(Collectors.toList());
        
        log.info("가맹점 쿠폰 목록 조회 완료 - 조회된 쿠폰 수: {}", couponList.size());
        
        return couponList;
    }
    
    /**
     * 결제 실행
     */
    @Transactional
    public boolean processPayment(PaymentRequestDTO request, Integer userIndex) {
        log.info("결제 실행 - userIndex: {}, targetUserIndex: {}, amount: {}, couponIndexes: {}", 
            userIndex, request.getTargetUserIndex(), request.getAmount(), request.getCouponIndexes());
        
        // 1. 핀번호 검증
        Optional<UserCm> userCm = paymentJtjRepo.findByUserCmIndex(userIndex);
        if (userCm.isEmpty()) {
            log.warn("사용자 CM 정보를 찾을 수 없습니다. userIndex: {}", userIndex);
            throw new RuntimeException("사용자 정보를 찾을 수 없습니다.");
        }
        
        if (userCm.get().getUserCmPincode() == null) {
            log.warn("핀번호가 설정되지 않았습니다. userIndex: {}", userIndex);
            throw new RuntimeException("핀번호가 설정되지 않았습니다.");
        }
        
        if (!userCm.get().getUserCmPincode().equals(request.getPinCode())) {
            log.warn("핀번호가 일치하지 않습니다. userIndex: {}, 입력된 핀번호: {}", userIndex, request.getPinCode());
            throw new RuntimeException("핀번호가 일치하지 않습니다.");
        }
        
        // 2. 현재 보유 CM 확인 (실제 차감될 금액 기준으로 계산)
        Integer actualCmAmount = request.getActualCmAmount() != null ? request.getActualCmAmount() : request.getAmount();
        
        // 현재 CM 잔액 = 입금 + 출금 (출금은 음수)
        Integer currentCm = (userCm.get().getUserCmDeposit() != null ? userCm.get().getUserCmDeposit() : 0) + 
                           (userCm.get().getUserCmWithdrawal() != null ? userCm.get().getUserCmWithdrawal() : 0);
        
        log.info("현재 CM 잔액: {}, 실제 차감될 금액: {}", currentCm, actualCmAmount);
        
        // 3. 결제 금액 계산 (입력 금액 + 쿠폰 금액)
        Integer totalAmount = request.getAmount();
        Integer couponTotalAmount = 0;
        
        // 프론트엔드에서 계산된 쿠폰 총액 사용
        if (request.getCouponTotal() != null) {
            couponTotalAmount = request.getCouponTotal();
            log.info("프론트엔드에서 계산된 쿠폰 총액: {}", couponTotalAmount);
        }
        
        if (request.getCouponIndexes() != null && !request.getCouponIndexes().isEmpty()) {
            // 쿠폰 검증 및 상태 업데이트
            for (Integer couponIndex : request.getCouponIndexes()) {
                Optional<Coupon> couponOpt = couponRepository.findById(couponIndex);
                if (couponOpt.isPresent()) {
                    Coupon coupon = couponOpt.get();
                    
                    // 쿠폰이 해당 사용자의 것인지 확인
                    if (coupon.getProvidedUser().getUserIndex().equals(userIndex)) {
                        // 쿠폰이 사용 가능한 상태인지 확인 (1: 사용 가능)
                        if (coupon.getCouponProvidedStatusIndex() == 1) {
                            // 쿠폰 상태를 사용 완료(2)로 변경
                            coupon.setCouponProvidedStatusIndex(2);
                            couponRepository.save(coupon);
                            
                            log.info("쿠폰 사용 완료 - 쿠폰 인덱스: {}, 쿠폰명: {}, 금액: {}", 
                                couponIndex, coupon.getCouponName(), coupon.getCouponPrice());
                        } else {
                            log.warn("사용할 수 없는 쿠폰입니다. 쿠폰 인덱스: {}, 상태: {}", 
                                couponIndex, coupon.getCouponProvidedStatusIndex());
                            throw new RuntimeException("사용할 수 없는 쿠폰이 포함되어 있습니다.");
                        }
                    } else {
                        log.warn("다른 사용자의 쿠폰입니다. 쿠폰 인덱스: {}, 소유자: {}, 요청자: {}", 
                            couponIndex, coupon.getProvidedUser().getUserIndex(), userIndex);
                        throw new RuntimeException("다른 사용자의 쿠폰이 포함되어 있습니다.");
                    }
                } else {
                    log.warn("존재하지 않는 쿠폰입니다. 쿠폰 인덱스: {}", couponIndex);
                    throw new RuntimeException("존재하지 않는 쿠폰이 포함되어 있습니다.");
                }
            }
            
            totalAmount += couponTotalAmount;
            log.info("쿠폰 사용 총액: {}, 최종 결제 금액: {}", couponTotalAmount, totalAmount);
            
            // 쿠폰 사용 시 회사 계정(user_index 1번)에서 쿠폰 금액 차감
            try {
                Optional<UserCm> companyUserCm = paymentJtjRepo.findByUserCmIndex(1);
                if (companyUserCm.isPresent()) {
                    // 회사 계정에서 쿠폰 총 금액 차감
                    Integer currentWithdrawal = companyUserCm.get().getUserCmWithdrawal() != null ? companyUserCm.get().getUserCmWithdrawal() : 0;
                    companyUserCm.get().setUserCmWithdrawal(currentWithdrawal - couponTotalAmount);
                    userCmRepository.save(companyUserCm.get());
                    
                    // 회사 계정 쿠폰 사용 로그 기록
                    UserCmLog companyCmLog = new UserCmLog();
                    companyCmLog.setUserCmLogValue(-couponTotalAmount); // 차감이므로 음수
                    companyCmLog.setUserCmLogTransactionTypeIndex(16); // 거래 타입 (16: 쿠폰 사용)
                    companyCmLog.setUserCmLogValueTypeIndex(2); // 화폐 단위 (2: CM)
                    companyCmLog.setUserCmLogPaymentIndex(2); // 거래의 종류 (2: 출금)
                    companyCmLog.setUserCmpLogPaymentIndex(null); // CMP 거래의 종류 (사용하지 않음)
                    companyCmLog.setUserCmLogCreateTime(LocalDateTime.now()); // 거래 발생 시간
                    companyCmLog.setUserCmLogReason("쿠폰 사용 - 고객: " + userIndex + ", 가맹점: " + request.getTargetUserIndex() + ", 쿠폰 총액: " + couponTotalAmount + " CM"); // 거래에 대한 메모
                    companyCmLog.setUserCmLogTransactionCancel(null); // 취소 (아님)
                    companyCmLog.setUserCouponValue(couponTotalAmount); // 쿠폰으로 사용된 금액
                    
                    // UserTesseris 객체 생성 - 거래 요청인 (회사)
                    UserTesseris companyTriggerTesseris = new UserTesseris();
                    companyTriggerTesseris.setUserIndex(1);
                    companyCmLog.setUserIndexEventTrigger(companyTriggerTesseris);
                    
                    // UserTesseris 객체 생성 - 거래 상대방 (결제하는 사용자)
                    UserTesseris userPartyTesseris = new UserTesseris();
                    userPartyTesseris.setUserIndex(userIndex);
                    companyCmLog.setUserIndexEventParty(userPartyTesseris);
                    
                    // 회사 CM 로그 저장
                    userCmLogJtjRepo.save(companyCmLog);
                    
                    log.info("회사 계정에서 쿠폰 사용 차감 완료 - 차감액: {}, 새로운 withdrawal: {}", 
                        couponTotalAmount, companyUserCm.get().getUserCmWithdrawal());
                } else {
                    log.warn("회사 계정(user_index 1번)의 UserCm 정보를 찾을 수 없습니다.");
                }
            } catch (Exception e) {
                log.error("회사 계정에서 쿠폰 사용 차감 중 오류 발생", e);
                throw new RuntimeException("쿠폰 사용 처리 중 오류가 발생했습니다.");
            }
        }
        
        // 4. CM 잔액 확인 (실제 차감될 CM 금액으로 확인)
        if (currentCm < actualCmAmount) {
            log.warn("보유 CM이 부족합니다. 현재: {}, 필요: {}", currentCm, actualCmAmount);
            throw new RuntimeException("보유 CM이 부족합니다.");
        }
        
        // 5. 사용자 CM 차감 (withdrawal에 음수 추가)
        Integer newWithdrawal = (userCm.get().getUserCmWithdrawal() != null ? userCm.get().getUserCmWithdrawal() : 0) - actualCmAmount;
        userCm.get().setUserCmWithdrawal(newWithdrawal);
        
        // 6. 가맹점 CM 입금 (deposit 증가) - 원래 결제 금액을 받음
        Optional<UserCm> storeCm = paymentJtjRepo.findByUserCmIndex(request.getTargetUserIndex());
        if (storeCm.isPresent()) {
            Integer newStoreDeposit = (storeCm.get().getUserCmDeposit() != null ? storeCm.get().getUserCmDeposit() : 0) + request.getAmount();
            storeCm.get().setUserCmDeposit(newStoreDeposit);
            log.info("가맹점 입금 완료 - 가맹점: {}, 입금액: {}, 새로운 deposit: {}", 
                request.getTargetUserIndex(), request.getAmount(), newStoreDeposit);
        } else {
            log.warn("가맹점 CM 정보를 찾을 수 없습니다. 가맹점: {}", request.getTargetUserIndex());
        }
        
        // 7. 사용자 결제 로그 기록 (CM 출금)
        UserCmLog userPaymentLog = new UserCmLog();
        userPaymentLog.setUserCmLogValue(-actualCmAmount); // 거래에 사용된 단위 (출금이므로 음수)
        userPaymentLog.setUserCmLogTransactionTypeIndex(9); // 거래 타입 (9: 구매)
        userPaymentLog.setUserCmLogValueTypeIndex(2); // 화폐 단위 (2: CM)
        userPaymentLog.setUserCmLogPaymentIndex(2); // 거래의 종류 (2: 출금)
        userPaymentLog.setUserCmpLogPaymentIndex(null); // CMP 거래의 종류 (사용하지 않음)
        userPaymentLog.setUserCmLogCreateTime(LocalDateTime.now()); // 거래 발생 시간
        userPaymentLog.setUserCmLogReason("결제 - 가맹점: " + request.getTargetUserIndex() + ", 금액: " + actualCmAmount + " CM" + 
            (couponTotalAmount > 0 ? ", 쿠폰 사용: " + couponTotalAmount + " CM" : "")); // 거래에 대한 메모
        userPaymentLog.setUserCmLogTransactionCancel(null); // 판매 취소 (아님)
        userPaymentLog.setUserCouponValue(couponTotalAmount); // 쿠폰으로 사용된 금액
        
        // UserTesseris 객체 생성 - 거래 요청인 (결제하는 사용자)
        UserTesseris userTesseris = new UserTesseris();
        userTesseris.setUserIndex(userIndex);
        userPaymentLog.setUserIndexEventTrigger(userTesseris);
        
        // UserTesseris 객체 생성 - 거래 상대방 (가맹점)
        UserTesseris storeTesseris = new UserTesseris();
        storeTesseris.setUserIndex(request.getTargetUserIndex());
        userPaymentLog.setUserIndexEventParty(storeTesseris);
        
        log.info("사용자 결제 로그 생성 - 사용자: {}, 차감액: {}, 거래타입: 구매", 
            userIndex, actualCmAmount);
        
        // 8. 가맹점 입금 로그 기록 (CM 입금) - 원래 결제 금액으로 기록
        UserCmLog storePaymentLog = new UserCmLog();
        storePaymentLog.setUserCmLogValue(request.getAmount()); // 거래에 사용된 단위 (입금이므로 양수)
        storePaymentLog.setUserCmLogTransactionTypeIndex(8); // 거래 타입 (8: 판매)
        storePaymentLog.setUserCmLogValueTypeIndex(2); // 화폐 단위 (2: CM)
        storePaymentLog.setUserCmLogPaymentIndex(2); // 거래의 종류 (2: 출금)
        storePaymentLog.setUserCmpLogPaymentIndex(null); // CMP 거래의 종류 (사용하지 않음)
        storePaymentLog.setUserCmLogCreateTime(LocalDateTime.now()); // 거래 발생 시간
        storePaymentLog.setUserCmLogReason("판매 - 고객: " + userIndex + ", 금액: " + request.getAmount() + " CM" + 
            (couponTotalAmount > 0 ? ", 쿠폰 할인: " + couponTotalAmount + " CM" : "")); // 거래에 대한 메모
        storePaymentLog.setUserCmLogTransactionCancel(null); // 판매 취소 (아님)
        storePaymentLog.setUserCouponValue(0); // 쿠폰으로 사용된 금액 (사용하지 않음)
        
        // UserTesseris 객체 생성 - 거래 요청인 (가맹점)
        UserTesseris storeTriggerTesseris = new UserTesseris();
        storeTriggerTesseris.setUserIndex(request.getTargetUserIndex());
        storePaymentLog.setUserIndexEventTrigger(storeTriggerTesseris);
        
        // UserTesseris 객체 생성 - 거래 상대방 (결제하는 사용자)
        UserTesseris userPartyTesseris = new UserTesseris();
        userPartyTesseris.setUserIndex(userIndex);
        storePaymentLog.setUserIndexEventParty(userPartyTesseris);
        
        log.info("가맹점 입금 로그 생성 - 가맹점: {}, 입금액: {}, 거래타입: 판매", 
            request.getTargetUserIndex(), request.getAmount());
        
        // 9. 데이터베이스에 저장 (UserCm 업데이트 및 UserCmLog 저장)
        // UserCm은 JPA의 변경 감지로 자동 저장됨 (트랜잭션 내에서)
        userCmLogJtjRepo.save(userPaymentLog);
        userCmLogJtjRepo.save(storePaymentLog);
        
        log.info("CM 사용 로그 저장 완료 - 사용자: {}, 가맹점: {}, 거래액: {}", 
            userIndex, request.getTargetUserIndex(), actualCmAmount);
        
        // 10. 사용자와 가맹점 간의 관계 생성 (store_customer 테이블)
        // 같은 user_index인 경우에는 관계를 생성하지 않음
        if (!userIndex.equals(request.getTargetUserIndex())) {
            try {
                // 이미 관계가 존재하는지 확인
                Optional<StoreCustomer> existingRelation = storeCustomerRepository.findByStoreAndCustomer(
                    request.getTargetUserIndex().toString(), 
                    userIndex.toString()
                );
                
                if (existingRelation.isEmpty()) {
                    // 새로운 관계 생성
                    StoreCustomer storeCustomer = StoreCustomer.builder()
                        .storeStoreUserIndex(request.getTargetUserIndex().toString()) // 가맹점 유저 인덱스
                        .storeCustomerUserIndex(userIndex.toString()) // 고객 유저 인덱스
                        .storeCustomerStatus("일반 고객") // 고객 상태 (처음에는 무조건 일반 고객)
                        .storeCustomerInsertDate(LocalDateTime.now()) // 등록일시
                        .build();
                    
                    storeCustomerRepository.save(storeCustomer);
                    
                    log.info("사용자-가맹점 관계 생성 완료 - 가맹점: {}, 고객: {}, 상태: 일반 고객", 
                        request.getTargetUserIndex(), userIndex);
                } else {
                    log.info("이미 존재하는 사용자-가맹점 관계 - 가맹점: {}, 고객: {}", 
                        request.getTargetUserIndex(), userIndex);
                }
            } catch (Exception e) {
                log.error("사용자-가맹점 관계 생성 중 오류 발생: ", e);
                // 관계 생성 실패는 결제 성공에 영향을 주지 않도록 함
            }
        } else {
            log.info("자기 자신에게 결제하는 경우이므로 손님 연결성을 생성하지 않습니다. userIndex: {}", userIndex);
        }
        
        return true;
    }
    
    /**
     * Coupon 엔티티를 CouponDTO로 변환
     */
    private CouponDTO toCouponDTO(Coupon coupon) {
        return CouponDTO.builder()
            .couponIndex(coupon.getCouponIndex())
            .couponName(coupon.getCouponName())
            .couponPrice(coupon.getCouponPrice())
            .storeName("가맹점명") // TODO: 실제 가맹점명 조회 로직 필요
            .couponLimitTime(coupon.getCouponLimitTime() != null ? 
                coupon.getCouponLimitTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "")
            .couponProvidedStatus("사용 가능")
            .couponIssuanceTime(coupon.getCouponIssuanceTime())
            .build();
    }
} 