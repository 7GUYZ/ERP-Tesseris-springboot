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
import com.jakdang.labs.entity.RegularPayment;
import com.jakdang.labs.entity.SuggestionUser;
import com.jakdang.labs.entity.UserCm;
import com.jakdang.labs.entity.UserCmLog;
import com.jakdang.labs.entity.UserCmLogPayment;
import com.jakdang.labs.entity.UserCmLogTransactionType;
import com.jakdang.labs.entity.UserCmLogValueType;
import com.jakdang.labs.entity.UserTesseris;
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
                    // 5. 추천인 UserCm 업데이트 (amount만큼 +)
                    UserCm referralUserCm = ajgUserCm.findById(referralUserIndex)
                            .orElseThrow(() -> new RuntimeException("추천인 UserCm 정보를 찾을 수 없습니다: " + referralUserIndex));

                    // 기존 추천인 UserCm의 모든 데이터를 유지하면서 deposit만 + 업데이트
                    Integer referralCurrentDeposit = referralUserCm.getUserCmDeposit() != null
                            ? referralUserCm.getUserCmDeposit()
                            : 0;
                    Integer referralNewDeposit = referralCurrentDeposit + amount;
                    referralUserCm.setUserCmDeposit(referralNewDeposit);
                    // 나머지 필드들은 기존 값 그대로 유지 (userCmWithdrawal, userCmIndex 등)

                    UserCm savedReferralUserCm = ajgUserCm.save(referralUserCm);
                    log.info("추천인 UserCm 업데이트 완료: userIndex={}, 기존={}, 새={}",
                            referralUserIndex, referralCurrentDeposit, referralNewDeposit);

                    // 6. 추천인 UserCmLog 생성 (발생자=충전한사람, 받는사람=추천인)
                    UserCmLog referralUserCmLog = new UserCmLog();
                    referralUserCmLog.setUserCmLogPaymentIndex(paymentType.getUserCmLogPaymentIndex());
                    referralUserCmLog.setUserCmpLogPaymentIndex(null);
                    referralUserCmLog
                            .setUserCmLogTransactionTypeIndex(transactionType.getUserCmLogTransactionTypeIndex());
                    referralUserCmLog.setUserCmLogValueTypeIndex(valueType.getUserCmLogValueTypeIndex());
                    referralUserCmLog.setUserIndexEventTrigger(userTesseris); // 발생자 = 충전한사람
                    referralUserCmLog.setUserIndexEventParty(referralUserTesseris); // 받는사람 = 추천인
                    referralUserCmLog.setUserCmLogValue(amount);
                    referralUserCmLog.setUserCmLogReason("추천인 CM충전");
                    referralUserCmLog.setUserCmLogCreateTime(approvedTime);
                    referralUserCmLog.setUserCmLogTransactionCancel(null);
                    referralUserCmLog.setUserCouponValue(null);

                    UserCmLog savedReferralUserCmLog = ajgUserCmLog.save(referralUserCmLog);
                    log.info("추천인 UserCmLog 생성 완료: {}", savedReferralUserCmLog.getUserCmLogIndex());
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

}
