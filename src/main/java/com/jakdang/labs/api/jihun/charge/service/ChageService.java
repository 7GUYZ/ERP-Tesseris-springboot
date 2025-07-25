package com.jakdang.labs.api.jihun.charge.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.jakdang.labs.api.jihun.charge.repository.AjgRegularPayment;
import com.jakdang.labs.api.jihun.charge.repository.AjgTemporaryRegularDetail;
import com.jakdang.labs.api.jihun.charge.repository.AjgTemporaryRegularMaster;
import com.jakdang.labs.api.jihun.charge.repository.AjgUserCmLog;
import com.jakdang.labs.entity.RegularPayment;
import com.jakdang.labs.entity.TemporaryRegularDetail;
import com.jakdang.labs.entity.TemporaryRegularMaster;
import com.jakdang.labs.entity.UserCmLog;
import com.jakdang.labs.entity.UserTesseris;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChageService {
    private final String TOSS_SECRET_KET = "test_sk_Gv6LjeKD8aEWZO419N7k8wYxAdXy";
    private final AjgTemporaryRegularMaster ajgTemporaryRegularMaster;
    private final AjgTemporaryRegularDetail ajgTemporaryRegularDetail;
    private final AjgRegularPayment ajgRegularPayment;
    private final AjgUserCmLog ajgUserCmLog;

    /**
     * 결제 확인
     * @param data
     * @param source
     * @param data.paymentKey 결제 키
     * @param data.orderId 주문 아이디
     * @param data.amount 결제 금액
     * @param source 소스 어디서왔는지
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
                            "Basic " + Base64.getEncoder().encodeToString((TOSS_SECRET_KET + ":").getBytes()))
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
                    case "charge":
                        chargeProcess(result.body(),data);
                        break;
                    case "take":
                        mypageProcess(data);
                        break;
                    default: break;
                }
                return ResponseEntity.status(HttpStatus.OK).body(Map.of("result", result.body()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("result", result.body()));
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    @Transactional
    private void chargeProcess(String result, Map<String, Object> data) {
        log.info("결제 처리 시작: {}", result);
        try {
            // 토스페이먼츠 응답 데이터 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> resultData = objectMapper.readValue(result, Map.class);
            // 한국 시간으로 변환
            ZonedDateTime createTimeZoned = ZonedDateTime.parse((String) resultData.get("createdAt"));
            ZonedDateTime approvedTimeZoned = ZonedDateTime.parse((String) resultData.get("approvedAt"));
            LocalDateTime createTime = createTimeZoned.toLocalDateTime();
            LocalDateTime approvedTime = approvedTimeZoned.toLocalDateTime();
            // 1. RegularPayment 테이블 INSERT
            RegularPayment regularPayment = new RegularPayment();
            regularPayment.setUserIndex((UserTesseris) data.get("userIndex"));
            regularPayment.setResultCd((String) resultData.get("status")); //결제 성공 여부 (DONE)
            regularPayment.setResultMsg("정상"); //결제 성공 메시지
            regularPayment.setAdvanceMsg("정상 승인"); //결제 성공 메시지
            regularPayment.setCreateTime(approvedTime); //결제 승인 시간 (approvedAt)
            regularPayment.setAuthCd((String) resultData.get("lastTransactionKey")); //결제 승인 코드 (lastTransactionKey)
            regularPayment.setCardId((String) resultData.get("paymentKey")); //결제 키 (paymentKey)
            regularPayment.setInstallment(null); //할부 정보 (카드 결제가 아닌 경우 null)
            regularPayment.setBin(null); //카드 BIN (카드 결제가 아닌 경우 null)
            regularPayment.setLast4(null); //카드 끝4자리 (카드 결제가 아닌 경우 null)
            regularPayment.setIssuer(null); //카드 발행사 (카드 결제가 아닌 경우 null)
            regularPayment.setCardType(null); //카드 타입 (카드 결제가 아닌 경우 null)
            regularPayment.setAcquirer(null); //카드 매입사 (카드 결제가 아닌 경우 null)
            regularPayment.setWebhookUrl(null); //웹훅 URL (필요시 설정)
            regularPayment.setTrxId((String) resultData.get("paymentKey")); //거래 ID (paymentKey)
            regularPayment.setTrxType((String) resultData.get("type")); //거래 타입 (NORMAL)
            regularPayment.setTmnId((String) resultData.get("mId")); //가맹점 ID (mId)
            regularPayment.setTrackId((String) resultData.get("orderId")); //주문 ID (orderId)
            regularPayment.setAmount((Integer) resultData.get("suppliedAmount")); //공급가액 (부가세 제외 금액)
            regularPayment.setUdf1((String) resultData.get("orderName")); //주문명 (orderName)
            regularPayment.setUdf2((String) resultData.get("method")); //결제 방법 (간편결제)
            
            RegularPayment savedRegularPayment = ajgRegularPayment.save(regularPayment);
            log.info("RegularPayment 저장 완료: {}", savedRegularPayment.getId());
            
            // 2. TemporaryRegularMaster 테이블 INSERT
            TemporaryRegularMaster temporaryRegularMaster = new TemporaryRegularMaster();
            temporaryRegularMaster.setStoreUserIndex((UserTesseris) data.get("userIndex"));
            temporaryRegularMaster.setTemporaryStoreMasterTransactionName("충전 거래");
            temporaryRegularMaster.setTemporaryStoreMasterChargeTime(approvedTime);
            temporaryRegularMaster.setTemporaryStoreMasterDistributionTime(null);
            temporaryRegularMaster.setTemporaryStoreMasterDistributionStatus("n");
            temporaryRegularMaster.setTemporaryStoreCmValue((Integer) resultData.get("suppliedAmount")); //공급가액 (부가세 제외 금액)
            temporaryRegularMaster.setTemporaryStoreCashValue((Integer) resultData.get("suppliedAmount")); //공급가액 (부가세 제외 금액)
            temporaryRegularMaster.setTemporaryStoreFeeValue(0);
            
            TemporaryRegularMaster savedTemporaryRegularMaster = ajgTemporaryRegularMaster.save(temporaryRegularMaster);
            log.info("TemporaryRegularMaster 저장 완료: {}", savedTemporaryRegularMaster.getTemporaryStoreMasterIndex());
            
            // 3. UserCmLog 테이블 INSERT
            UserCmLog userCmLog = new UserCmLog();
            userCmLog.setUserCmLogPaymentIndex(1); // 입금
            userCmLog.setUserCmpLogPaymentIndex(null);
            userCmLog.setUserCmLogTransactionTypeIndex(7); // 충전
            userCmLog.setUserCmLogValueTypeIndex(2); // CM
            userCmLog.setUserIndexEventTrigger((UserTesseris) data.get("userIndex"));
            userCmLog.setUserIndexEventParty((UserTesseris) data.get("userIndex"));
            userCmLog.setUserCmLogValue((Integer) resultData.get("suppliedAmount")); //공급가액 (부가세 제외 금액)
            userCmLog.setUserCmLogReason("포인트 충전");
            userCmLog.setUserCmLogCreateTime(approvedTime);
            userCmLog.setUserCmLogTransactionCancel(null);
            userCmLog.setUserCouponValue(null);
            
            UserCmLog savedUserCmLog = ajgUserCmLog.save(userCmLog);
            log.info("UserCmLog 저장 완료: {}", savedUserCmLog.getUserCmLogIndex());
            
            // 4. TemporaryRegularDetail 테이블 INSERT
            TemporaryRegularDetail temporaryRegularDetail = new TemporaryRegularDetail();
            temporaryRegularDetail.setUserIndex((UserTesseris) data.get("userIndex"));
            Integer suppliedAmount = (Integer) resultData.get("suppliedAmount"); //공급가액 (부가세 제외 금액)
            Double commissionRate = 0.033; //추천인 수수료 3.3%
            Double commissionAmount = suppliedAmount * commissionRate; //수수료 금액
            Double actualAmount = suppliedAmount - commissionAmount; //원금 - 수수료
            
            temporaryRegularDetail.setOriginalCashValue(suppliedAmount); //공급가액 (부가세 제외 금액)
            temporaryRegularDetail.setTax("0.033"); //부가세 3.3% 추천인수수료
            temporaryRegularDetail.setAccount(null);
            temporaryRegularDetail.setTemporaryRegularCashValue(actualAmount); //수수료 제외 실제 금액
            temporaryRegularDetail.setPaymentStatus("지급");
            temporaryRegularDetail.setDescription("회원");
            temporaryRegularDetail.setTemporaryRegularMasterIndex(savedTemporaryRegularMaster.getTemporaryStoreMasterIndex());
            
            TemporaryRegularDetail savedTemporaryRegularDetail = ajgTemporaryRegularDetail.save(temporaryRegularDetail);
            log.info("TemporaryRegularDetail 저장 완료: {}", savedTemporaryRegularDetail.getTemporaryStoreDetailIndex());
            
            log.info("충전 DB 처리 완료");
            
        } catch (Exception e) {
            log.error("충전 DB 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("충전 DB 처리 실패", e);
        }
    }
    @Transactional
    private void mypageProcess(Map<String, Object> data) {
        log.info("마이페이지 처리 시작: {}", data);
    }
}
