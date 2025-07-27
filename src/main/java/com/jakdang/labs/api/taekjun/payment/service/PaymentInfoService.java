package com.jakdang.labs.api.taekjun.payment.service;

import com.jakdang.labs.api.taekjun.payment.dto.PaymentInfoDTO;
import com.jakdang.labs.api.taekjun.payment.dto.PaymentRequestDTO;
import com.jakdang.labs.api.taekjun.payment.dto.StoreDTO;
import com.jakdang.labs.api.taekjun.payment.dto.CouponDTO;
import com.jakdang.labs.api.taekjun.payment.repository.PaymentJtjRepo;
import com.jakdang.labs.api.taekjun.payment.repository.StoreCustomerRepository;
import com.jakdang.labs.api.taekjun.dashdord.repository.UserCmLogJtjRepo;
import com.jakdang.labs.entity.Store;
import com.jakdang.labs.entity.Coupon;
import com.jakdang.labs.entity.UserCm;
import com.jakdang.labs.entity.UserCmLog;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.entity.StoreCustomer;
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
    
    /**
     * 결제 정보 조회 (월 한도, 사용량, 보유 CM)
     */
    @Transactional(readOnly = true)
    public PaymentInfoDTO getPaymentInfo(Integer userIndex) {
        log.info("결제 정보 조회 - userIndex: {}", userIndex);
        
        // 월 결제 한도 (Setting 테이블에서 조회 - 임시로 1000000 설정)
        Integer monthlyLimit = 1000000;
        
        // 이번 달 사용 금액
        Integer monthlyUsed = paymentJtjRepo.getMonthlyUsedCm(userIndex);
        
        // 현재 보유 CM
        Optional<UserCm> userCm = paymentJtjRepo.findByUserCmIndex(userIndex);
        Integer currentCm = userCm.map(cm -> 
            (cm.getUserCmDeposit() != null ? cm.getUserCmDeposit() : 0) - 
            (cm.getUserCmWithdrawal() != null ? cm.getUserCmWithdrawal() : 0)
        ).orElse(0);
        
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
        
        // 2. 현재 보유 CM 확인
        Integer currentCm = (userCm.get().getUserCmDeposit() != null ? userCm.get().getUserCmDeposit() : 0) - 
                           (userCm.get().getUserCmWithdrawal() != null ? userCm.get().getUserCmWithdrawal() : 0);
        
        // 3. 결제 금액 계산 (입력 금액 + 쿠폰 금액)
        Integer totalAmount = request.getAmount();
        if (request.getCouponIndexes() != null && !request.getCouponIndexes().isEmpty()) {
            // 쿠폰 금액 계산 로직 추가 필요
            // totalAmount += 쿠폰 총액;
        }
        
        // 4. CM 잔액 확인
        if (currentCm < totalAmount) {
            log.warn("보유 CM이 부족합니다. 현재: {}, 필요: {}", currentCm, totalAmount);
            throw new RuntimeException("보유 CM이 부족합니다.");
        }
        
        // 5. 사용자 CM 차감 (withdrawal 증가)
        Integer newWithdrawal = (userCm.get().getUserCmWithdrawal() != null ? userCm.get().getUserCmWithdrawal() : 0) + totalAmount;
        userCm.get().setUserCmWithdrawal(newWithdrawal);
        
        // 6. 가맹점 CM 입금 (deposit 증가)
        Optional<UserCm> storeCm = paymentJtjRepo.findByUserCmIndex(request.getTargetUserIndex());
        if (storeCm.isPresent()) {
            Integer newStoreDeposit = (storeCm.get().getUserCmDeposit() != null ? storeCm.get().getUserCmDeposit() : 0) + totalAmount;
            storeCm.get().setUserCmDeposit(newStoreDeposit);
            log.info("가맹점 입금 완료 - 가맹점: {}, 입금액: {}, 새로운 deposit: {}", 
                request.getTargetUserIndex(), totalAmount, newStoreDeposit);
        } else {
            log.warn("가맹점 CM 정보를 찾을 수 없습니다. 가맹점: {}", request.getTargetUserIndex());
        }
        
        // 7. 사용자 결제 로그 기록 (CM 출금)
        UserCmLog userPaymentLog = new UserCmLog();
        userPaymentLog.setUserCmLogValue(-totalAmount); // 거래에 사용된 단위 (출금이므로 음수)
        userPaymentLog.setUserCmLogTransactionTypeIndex(1); // 거래 타입 (1: 구매/결제)
        userPaymentLog.setUserCmLogValueTypeIndex(1); // 화폐 단위 (1: CM)
        userPaymentLog.setUserCmLogPaymentIndex(2); // 거래의 종류 (2: 출금)
        userPaymentLog.setUserCmpLogPaymentIndex(null); // CMP 거래의 종류 (사용하지 않음)
        userPaymentLog.setUserCmLogCreateTime(LocalDateTime.now()); // 거래 발생 시간
        userPaymentLog.setUserCmLogReason("결제 - 가맹점: " + request.getTargetUserIndex() + ", 금액: " + totalAmount + " CM"); // 거래에 대한 메모
        userPaymentLog.setUserCmLogTransactionCancel(null); // 판매 취소 (아님)
        userPaymentLog.setUserCouponValue(0); // 쿠폰으로 사용된 금액 (사용하지 않음)
        
        // UserTesseris 객체 생성 - 거래 요청인 (결제하는 사용자)
        UserTesseris userTesseris = new UserTesseris();
        userTesseris.setUserIndex(userIndex);
        userPaymentLog.setUserIndexEventTrigger(userTesseris);
        
        // UserTesseris 객체 생성 - 거래 상대방 (가맹점)
        UserTesseris storeTesseris = new UserTesseris();
        storeTesseris.setUserIndex(request.getTargetUserIndex());
        userPaymentLog.setUserIndexEventParty(storeTesseris);
        
        log.info("사용자 결제 로그 생성 - 사용자: {}, 차감액: {}, 거래타입: 구매", 
            userIndex, totalAmount);
        
        // 8. 가맹점 입금 로그 기록 (CM 입금)
        UserCmLog storePaymentLog = new UserCmLog();
        storePaymentLog.setUserCmLogValue(totalAmount); // 거래에 사용된 단위 (입금이므로 양수)
        storePaymentLog.setUserCmLogTransactionTypeIndex(2); // 거래 타입 (2: 판매/입금)
        storePaymentLog.setUserCmLogValueTypeIndex(1); // 화폐 단위 (1: CM)
        storePaymentLog.setUserCmLogPaymentIndex(1); // 거래의 종류 (1: 입금)
        storePaymentLog.setUserCmpLogPaymentIndex(null); // CMP 거래의 종류 (사용하지 않음)
        storePaymentLog.setUserCmLogCreateTime(LocalDateTime.now()); // 거래 발생 시간
        storePaymentLog.setUserCmLogReason("판매 - 고객: " + userIndex + ", 금액: " + totalAmount + " CM"); // 거래에 대한 메모
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
            request.getTargetUserIndex(), totalAmount);
        
        // 9. 데이터베이스에 저장 (UserCm 업데이트 및 UserCmLog 저장)
        // UserCm은 JPA의 변경 감지로 자동 저장됨 (트랜잭션 내에서)
        userCmLogJtjRepo.save(userPaymentLog);
        userCmLogJtjRepo.save(storePaymentLog);
        
        log.info("CM 사용 로그 저장 완료 - 사용자: {}, 가맹점: {}, 거래액: {}", 
            userIndex, request.getTargetUserIndex(), totalAmount);
        
        // 10. 사용자와 가맹점 간의 관계 생성 (store_customer 테이블)
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