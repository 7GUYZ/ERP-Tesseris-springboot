package com.jakdang.labs.api.jihun.charge.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.jakdang.labs.api.jihun.charge.repository.AjgRegularPayment;
import com.jakdang.labs.api.jihun.charge.repository.AjgSuggestionUser;
import com.jakdang.labs.api.jihun.charge.repository.AjgUserCm;
import com.jakdang.labs.api.jihun.charge.repository.AjgUserCmLog;
import com.jakdang.labs.api.jihun.charge.repository.AjgUserCmLogPayment;
import com.jakdang.labs.api.jihun.charge.repository.AjgUserCmLogTransactionType;
import com.jakdang.labs.api.jihun.charge.repository.AjgUserCmLogValueType;
import com.jakdang.labs.api.jihun.charge.repository.AjgUserTesseris;
import com.jakdang.labs.api.jihun.charge.repository.AjgTemporaryRegularMaster;
import com.jakdang.labs.api.jihun.charge.repository.AjgTemporaryRegularDetail;
import com.jakdang.labs.entity.RegularPayment;
import com.jakdang.labs.entity.SuggestionUser;
import com.jakdang.labs.entity.UserCm;
import com.jakdang.labs.entity.UserCmLog;
import com.jakdang.labs.entity.UserCmLogPayment;
import com.jakdang.labs.entity.UserCmLogTransactionType;
import com.jakdang.labs.entity.UserCmLogValueType;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.entity.TemporaryRegularMaster;
import com.jakdang.labs.entity.TemporaryRegularDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChageService {
    @Value("${toss.secret-key}")
    private String TOSS_SECRET_KEY;

    private final AjgRegularPayment ajgRegularPayment;
    private final AjgSuggestionUser ajgSuggestionUser;
    private final AjgUserCm ajgUserCm;
    private final AjgUserCmLog ajgUserCmLog;
    private final AjgUserTesseris ajgUserTesseris;
    private final AjgUserCmLogPayment ajgUserCmLogPayment;
    private final AjgUserCmLogTransactionType ajgUserCmLogTransactionType;
    private final AjgUserCmLogValueType ajgUserCmLogValueType;
    private final AjgTemporaryRegularMaster ajgTemporaryRegularMaster;
    private final AjgTemporaryRegularDetail ajgTemporaryRegularDetail;

    /**
     * 결제 확인
     * 
     * @param data
     * @param source
     * @param data.paymentKey 결제 키
     * @param data.orderId    주문 아이디
     * @param data.amount     결제 금액
     * @param source          소스 어디서왔는지
     * @return ResponseEntity<Map<String, Object>>
     */
    @Transactional
    public ResponseEntity<Map<String, Object>> confirmPayment(Map<String, Object> data, String source) {
        Map<String, Object> response = new HashMap<>();
        log.info("source : {}", source);
        try {
            log.info("결제 요청 데이터: {}", data);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
                    .header("Authorization",
                            "Basic " + Base64.getEncoder().encodeToString((TOSS_SECRET_KEY + ":").getBytes()))
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString(
                            "{\"paymentKey\":\"" + data.get("paymentKey") + "\",\"orderId\":\"" + data.get("orderId")
                                    + "\",\"amount\":" + data.get("amount") + "}"))
                    .build();
            HttpResponse<String> result = HttpClient.newHttpClient().send(request,
                    HttpResponse.BodyHandlers.ofString());
            log.info("결과값 : {}", result.body());
            if (result.body().contains("DONE")) {
                // DB처리하는곳
                switch (source) {
                    case "user":
                        chargeProcess(result.body(), data);
                        break;
                    case "onlinepayment":
                        chargeProcess(result.body(), data);
                        break;
                    default:
                        break;
                }
                return ResponseEntity.status(HttpStatus.OK).body(Map.of("result", result.body()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("result", result.body()));
            }
        } catch (Exception e) {
            log.error("결제 확인 중 오류 발생: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * 충전 처리 (PHP 원본 로직을 JPA로 변환)
     * 
     * @param result 토스페이먼츠 응답 결과
     * @param data   요청 데이터
     */
    @Transactional
    private void chargeProcess(String result, Map<String, Object> data) {
        log.info("충전 처리 시작: {}", result);
        try {
            // 토스페이먼츠 응답 데이터 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> resultData = objectMapper.readValue(result, Map.class);

            // 사용자 정보 추출
            Map<String, Object> userInfo = (Map<String, Object>) data.get("info");
            Integer userIndex = Integer.parseInt((String) userInfo.get("user_index"));
            Integer userRoleIndex = Integer.parseInt((String) userInfo.get("user_role_index"));

            // 결제 정보 추출 및 실 결제 금액 추출
            String paymentKey = (String) resultData.get("paymentKey");
            Integer suppliedAmount = (Integer) resultData.get("suppliedAmount");
            Integer amount = suppliedAmount; // 실 결제 금액을 amount로 사용

            // 한국 시간으로 변환
            ZonedDateTime approvedTimeZoned = ZonedDateTime.parse((String) resultData.get("approvedAt"));
            LocalDateTime approvedTime = approvedTimeZoned.toLocalDateTime();

            // UserTesseris 객체 조회
            UserTesseris userTesseris = ajgUserTesseris.findById(userIndex)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userIndex));

            // 1. RegularPayment 테이블 INSERT (결제내역 저장)
            RegularPayment regularPayment = new RegularPayment();
            regularPayment.setUserIndex(userTesseris);
            regularPayment.setResultCd((String) resultData.get("status"));
            regularPayment.setResultMsg("정상");
            regularPayment.setAdvanceMsg("정상승인");
            regularPayment.setCreateTime(approvedTime);
            regularPayment.setAuthCd((String) resultData.get("lastTransactionKey"));
            regularPayment.setCardId(paymentKey);
            regularPayment.setInstallment(null);
            regularPayment.setBin(null);
            regularPayment.setLast4(null);
            regularPayment.setIssuer(null);
            regularPayment.setCardType(null);
            regularPayment.setAcquirer(null);
            regularPayment.setWebhookUrl(null);
            regularPayment.setTrxId(paymentKey);
            regularPayment.setTrxType((String) resultData.get("type"));
            regularPayment.setTmnId((String) resultData.get("mId"));
            regularPayment.setTrackId((String) resultData.get("orderId"));
            regularPayment.setAmount(suppliedAmount);
            regularPayment.setUdf1((String) resultData.get("orderName"));
            regularPayment.setUdf2((String) resultData.get("method"));

            RegularPayment savedRegularPayment = ajgRegularPayment.save(regularPayment);
            log.info("RegularPayment 저장 완료: {}", savedRegularPayment.getId());

            // 2. 충전한 사람 UserCm 업데이트 (amount만큼 +)
            UserCm userCm = ajgUserCm.findById(userIndex)
                    .orElseThrow(() -> new RuntimeException("UserCm 정보를 찾을 수 없습니다: " + userIndex));

            // 기존 UserCm의 모든 데이터를 유지하면서 deposit만 + 업데이트
            Integer currentDeposit = userCm.getUserCmDeposit() != null ? userCm.getUserCmDeposit() : 0;
            Integer newDeposit = currentDeposit + amount;
            userCm.setUserCmDeposit(newDeposit);
            // 나머지 필드들은 기존 값 그대로 유지 (userCmWithdrawal, userCmIndex 등)

            UserCm savedUserCm = ajgUserCm.save(userCm);
            log.info("충전한 사람 UserCm 업데이트 완료: userIndex={}, 기존={}, 새={}",
                    userIndex, currentDeposit, newDeposit);

            // 3. 충전한 사람 UserCmLog 생성 (발생자=본인, 받는사람=본인)
            UserCmLog userCmLog = new UserCmLog();

            // 입금 타입 조회
            UserCmLogPayment paymentType = ajgUserCmLogPayment.findByUserCmLogPaymentName("입금");
            userCmLog.setUserCmLogPaymentIndex(paymentType.getUserCmLogPaymentIndex());
            userCmLog.setUserCmpLogPaymentIndex(null);

            // 충전 타입 조회
            UserCmLogTransactionType transactionType = ajgUserCmLogTransactionType
                    .findByUserCmLogTransactionTypeName("충전(CM)");
            userCmLog.setUserCmLogTransactionTypeIndex(transactionType.getUserCmLogTransactionTypeIndex());

            // CM 타입 조회
            UserCmLogValueType valueType = ajgUserCmLogValueType.findByUserCmLogValueTypeName("CM");
            userCmLog.setUserCmLogValueTypeIndex(valueType.getUserCmLogValueTypeIndex());

            userCmLog.setUserIndexEventTrigger(userTesseris); // 발생자 = 본인
            userCmLog.setUserIndexEventParty(userTesseris); // 받는사람 = 본인
            userCmLog.setUserCmLogValue(amount);
            userCmLog.setUserCmLogReason("CM충전");
            userCmLog.setUserCmLogCreateTime(approvedTime);
            userCmLog.setUserCmLogTransactionCancel(null);
            userCmLog.setUserCouponValue(null);

            UserCmLog savedUserCmLog = ajgUserCmLog.save(userCmLog);
            log.info("충전한 사람 UserCmLog 생성 완료: {}", savedUserCmLog.getUserCmLogIndex());

            // 4. 추천인 조회 및 처리
            Optional<SuggestionUser> suggestionUserOpt = ajgSuggestionUser
                    .findBySuggestionUserIndex(userTesseris.getUserIndex());
            log.info("추천인 조회 결과: suggestionUserOpt.isPresent()={}", suggestionUserOpt.isPresent());
            if (suggestionUserOpt.isPresent()) {
                SuggestionUser suggestionUser = suggestionUserOpt.get();
                Integer referralUserIndex = suggestionUser.getRecommendationUserIndex(); // 추천인 인덱스
                log.info("추천인 인덱스: {}", referralUserIndex);

                // 추천인 UserTesseris 조회
                UserTesseris referralUserTesseris = ajgUserTesseris.findById(referralUserIndex).orElse(null);
                log.info("추천인 UserTesseris 조회 결과: {}", referralUserTesseris != null ? "성공" : "실패");
                if (referralUserTesseris != null) {
                    // 5. 추천인 혜택 계산
                    double benefitRate = calculateBenefitRate(referralUserTesseris.getUserRoleIndex());
                    double originalCashValue = amount * benefitRate;
                    double taxRate = 0.033;
                    double taxAmount = originalCashValue * taxRate;
                    double finalCashValue = originalCashValue - taxAmount;
                    
                    log.info("추천인 혜택 계산: 등급={}, 원금={}, 세금={}, 최종지급액={}", 
                            referralUserTesseris.getUserRoleIndex(), originalCashValue, taxAmount, finalCashValue);

                    // 6. 추천인 UserCm 업데이트 (계산된 금액만큼 +)
                    UserCm referralUserCm = ajgUserCm.findById(referralUserIndex)
                            .orElseThrow(() -> new RuntimeException("추천인 UserCm 정보를 찾을 수 없습니다: " + referralUserIndex));

                    // 기존 추천인 UserCm의 모든 데이터를 유지하면서 deposit만 + 업데이트
                    Integer referralCurrentDeposit = referralUserCm.getUserCmDeposit() != null
                            ? referralUserCm.getUserCmDeposit()
                            : 0;
                    Integer referralNewDeposit = referralCurrentDeposit + (int) finalCashValue;
                    referralUserCm.setUserCmDeposit(referralNewDeposit);
                    // 나머지 필드들은 기존 값 그대로 유지 (userCmWithdrawal, userCmIndex 등)

                    UserCm savedReferralUserCm = ajgUserCm.save(referralUserCm);
                    log.info("추천인 UserCm 업데이트 완료: userIndex={}, 기존={}, 새={}, 지급액={}",
                            referralUserIndex, referralCurrentDeposit, referralNewDeposit, (int) finalCashValue);

                    // 7. 추천인 UserCmLog 생성 (발생자=충전한사람, 받는사람=추천인)
                    UserCmLog referralUserCmLog = new UserCmLog();
                    referralUserCmLog.setUserCmLogPaymentIndex(paymentType.getUserCmLogPaymentIndex());
                    referralUserCmLog.setUserCmpLogPaymentIndex(null);
                    referralUserCmLog
                            .setUserCmLogTransactionTypeIndex(transactionType.getUserCmLogTransactionTypeIndex());
                    referralUserCmLog.setUserCmLogValueTypeIndex(valueType.getUserCmLogValueTypeIndex());
                    referralUserCmLog.setUserIndexEventTrigger(userTesseris); // 발생자 = 충전한사람
                    referralUserCmLog.setUserIndexEventParty(referralUserTesseris); // 받는사람 = 추천인
                    referralUserCmLog.setUserCmLogValue((int) finalCashValue); // 계산된 금액
                    referralUserCmLog.setUserCmLogReason("추천인 CM충전");
                    referralUserCmLog.setUserCmLogCreateTime(approvedTime);
                    referralUserCmLog.setUserCmLogTransactionCancel(null);
                    referralUserCmLog.setUserCouponValue(null);

                    UserCmLog savedReferralUserCmLog = ajgUserCmLog.save(referralUserCmLog);
                    log.info("추천인 UserCmLog 생성 완료: {}", savedReferralUserCmLog.getUserCmLogIndex());

                    // 8. 추천인 혜택 계산 및 임시 테이블 저장
                    calculateAndSaveReferralBenefit(userTesseris, referralUserTesseris, amount, approvedTime, 
                            originalCashValue, taxAmount, finalCashValue,paymentKey);
                } else {
                    log.warn("추천인 UserTesseris 정보가 없음: {}", referralUserIndex);
                }
            } else {
                log.info("추천인이 없는 사용자: {}", userIndex);
            }

            log.info("충전 처리 완료: userIndex={}, amount={}", userIndex, amount);

        } catch (Exception e) {
            log.error("충전 DB 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("충전 DB 처리 실패", e);
        }
    }

    /**
     * 추천인 혜택 계산 및 임시 테이블 저장
     */
    private void calculateAndSaveReferralBenefit(UserTesseris userTesseris, UserTesseris referralUserTesseris, 
                                                Integer amount, LocalDateTime approvedTime,
                                                double originalCashValue, double taxAmount, double finalCashValue,String paymentKey) {
        try {
            log.info("추천인 혜택 임시 테이블 저장 시작: 충전자={}, 추천인={}, 충전금액={}", 
                    userTesseris.getUserIndex(), referralUserTesseris.getUserIndex(), amount);

            // 추천인의 user_role_index 조회
            Integer referralUserRoleIndex = referralUserTesseris.getUserRoleIndex();
            log.info("추천인 등급: {}", referralUserRoleIndex);

            // 등급별 설명 설정
            String description = "";
            switch (referralUserRoleIndex) {
                case 1: // 일반
                case 7: // 정회원
                case 8: // 모범회원
                case 9: // VIP회원
                case 10: // 프리미엄회원
                    description = "CM충전";
                    break;
                case 3: // 가맹점
                    description = "가맹점_결제_추천인혜택";
                    break;
                case 4: // 관리자
                    description = "관리자_결제_추천인혜택";
                    break;
                default:
                    log.warn("알 수 없는 추천인 등급: {}", referralUserRoleIndex);
                    return;
            }

            log.info("추천인 혜택 계산: 원금={}, 세금={}, 최종지급액={}", 
                    originalCashValue, taxAmount, finalCashValue);

            // 1. Temporary_Regular_Master 저장
            TemporaryRegularMaster master = new TemporaryRegularMaster();
            master.setStoreUserIndex(userTesseris); // 충전한 사람
            master.setTemporaryStoreMasterTransactionName(paymentKey);
            master.setTemporaryStoreMasterChargeTime(approvedTime);
            master.setTemporaryStoreMasterDistributionTime(null);
            master.setTemporaryStoreMasterDistributionStatus("n");
            master.setTemporaryStoreCmValue((int) amount);
            master.setTemporaryStoreCashValue((int) amount);
            master.setTemporaryStoreFeeValue((int) taxAmount);

            TemporaryRegularMaster savedMaster = ajgTemporaryRegularMaster.save(master);
            log.info("Temporary_Regular_Master 저장 완료: {}", savedMaster.getTemporaryStoreMasterIndex());

            // 2. Temporary_Regular_Detail 저장
            TemporaryRegularDetail detail = new TemporaryRegularDetail();
            detail.setUserIndex(referralUserTesseris); // 추천인
            detail.setOriginalCashValue((int) originalCashValue);
            detail.setTax("0.033"); // 세금 비율 고정
            detail.setAccount(null);
            detail.setTemporaryRegularCashValue(finalCashValue);
            detail.setPaymentStatus("지급");
            detail.setDescription(description);
            detail.setTemporaryRegularMasterIndex(savedMaster.getTemporaryStoreMasterIndex());

            TemporaryRegularDetail savedDetail = ajgTemporaryRegularDetail.save(detail);
            log.info("Temporary_Regular_Detail 저장 완료: {}", savedDetail.getTemporaryStoreDetailIndex());

            log.info("추천인 혜택 임시 테이블 저장 완료: 추천인={}, 지급액={}", 
                    referralUserTesseris.getUserIndex(), finalCashValue);

        } catch (Exception e) {
            log.error("추천인 혜택 임시 테이블 저장 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("추천인 혜택 임시 테이블 저장 실패", e);
        }
    }

    /**
     * 추천인 혜택 비율 계산
     * 
     * @param roleIndex 추천인 등급
     * @return 혜택 비율
     */
    private double calculateBenefitRate(Integer roleIndex) {
        switch (roleIndex) {
            case 1: // 일반
                return 0.20;
            case 7: // 정회원
            case 8: // 모범회원
            case 9: // VIP회원
            case 10: // 프리미엄회원
                return 0.20; // 20% 지급
            case 3: // 가맹점
                return 0.20; // 20% 지급
            case 4: // 관리자
                return 1.0; // 100% 지급
            case 2: // 사업자
            case 5: // 특판부
            case 6: // 가맹점 서브
                return 0.0; // 혜택 없음
            default:
                log.warn("알 수 없는 추천인 등급: {}", roleIndex);
                return 0.0; // 기본값
        }
    }
}
