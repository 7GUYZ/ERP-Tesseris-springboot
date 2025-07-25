package com.jakdang.labs.api.jungeun.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.jungeun.dto.CurrentCmDTO;
import com.jakdang.labs.api.jungeun.dto.GiftPinCheckDTO;
import com.jakdang.labs.api.jungeun.dto.GiftSearchUserDTO;
import com.jakdang.labs.api.jungeun.dto.GiftTransferDTO;
import com.jakdang.labs.api.jungeun.repository.UserCmLjeRepo;
import com.jakdang.labs.api.jungeun.repository.UserCmLogLjeRepo;
import com.jakdang.labs.api.jungeun.repository.UserTesserisLjeRepo;
import com.jakdang.labs.entity.UserCm;
import com.jakdang.labs.entity.UserCmLog;
import com.jakdang.labs.entity.UserTesseris;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GiftCmSvc {
    private final UserCmLjeRepo userCmRepo;
    private final UserCmLogLjeRepo userCmLogRepo;
    private final UserTesserisLjeRepo userTesserisRepo;

    public ResponseDTO<CurrentCmDTO> getCurrentCM(Integer user_index){
        UserCm userCm = userCmRepo.findByUserCmIndex(user_index);
        return ResponseDTO.createSuccessResponse("현재 CM 보유량 조회 성공", CurrentCmDTO.builder()
            .userIndex(userCm.getUserCmIndex())
            .currentCM(userCm.getUserCmDeposit() + userCm.getUserCmWithdrawal())
            .build()
        );
    }

    public ResponseDTO<?> searchUser(String recipientEmail){
        Object[] result = (Object[]) userCmRepo.findUserByEmail(recipientEmail);
        if (result == null) {
            return ResponseDTO.createErrorResponse(500, "사용자 찾기 실패");
        }

        GiftSearchUserDTO dto = GiftSearchUserDTO.builder()
            .userIndex(result[0] == null ? null : ((Number) result[0]).intValue())
            .userName(result[1] == null ? null : result[1].toString())
            .userEmail(result[2] == null ? null : result[2].toString())
            .userRoleIndex(result[3] == null ? null : ((Number) result[3]).intValue())
            .userPhone(result[4] == null ? null : result[4].toString())
            .build();
        return ResponseDTO.createSuccessResponse("사용자 찾기 성공", dto);
    }

    public ResponseDTO<?> pinCheck(GiftPinCheckDTO giftPinCheckDTO){
        UserCm userCm = userCmRepo.findByUserCmIndex(giftPinCheckDTO.getUserIndex());
        
        if (userCm.getUserCmPincode().equals(giftPinCheckDTO.getUserCmPincode())) {
            return ResponseDTO.createSuccessResponse("PIN 번호 일치", null);
        }
        return ResponseDTO.createErrorResponse(-500, "PIN 번호 불일치");
    }

    @Transactional
    public ResponseDTO<?> giftTransfer(GiftTransferDTO giftTransferDTO){
        try {
            // 보내는 사람의 UserCm 정보 조회
            UserCm sendUserCm = userCmRepo.findByUserCmIndex(giftTransferDTO.getSendUserIndex());
            if (sendUserCm == null) {
                return ResponseDTO.createErrorResponse(500, "보내는 사람 정보를 찾을 수 없습니다");
            }
            
            // 받는 사람의 UserCm 정보 조회
            UserCm receiveUserCm = userCmRepo.findByUserCmIndex(giftTransferDTO.getReceiveUserIndex());
            if (receiveUserCm == null) {
                return ResponseDTO.createErrorResponse(500, "받는 사람 정보를 찾을 수 없습니다");
            }
            
            // 보내는 사람의 user_cm_withdrawal 업데이트 (음수로 변환하여 차감)
            Integer currentWithdrawal = sendUserCm.getUserCmWithdrawal();
            Integer giftAmount = giftTransferDTO.getGiftAmount();
            Integer newWithdrawal = currentWithdrawal - giftAmount; // 양수를 음수로 변환하여 차감
            
            sendUserCm.setUserCmWithdrawal(newWithdrawal);
            userCmRepo.save(sendUserCm);
            
            // 받는 사람의 user_cm_deposit 업데이트 (양수로 추가)
            Integer currentDeposit = receiveUserCm.getUserCmDeposit();
            Integer newDeposit = currentDeposit + giftAmount;
            
            receiveUserCm.setUserCmDeposit(newDeposit);
            userCmRepo.save(receiveUserCm);

            // UserTesseris 객체 조회 (userIndex로 직접 조회)
            UserTesseris sendUserTesseris = userTesserisRepo.findById(giftTransferDTO.getSendUserIndex()).orElse(null);
            UserTesseris receiveUserTesseris = userTesserisRepo.findById(giftTransferDTO.getReceiveUserIndex()).orElse(null);
            
            // 선물 출금 로그 저장 (보내는 사람)
            UserCmLog sendUserLog = UserCmLog.builder()
                .userCmLogPaymentIndex(2) // 출금
                .userCmLogTransactionTypeIndex(10) // 선물
                .userCmLogValueTypeIndex(2) // CM
                .userIndexEventTrigger(receiveUserTesseris) // 받는 사람
                .userIndexEventParty(sendUserTesseris) // 보내는 사람
                .userCmLogValue(-giftAmount) // 음수로 저장
                .userCmLogReason("선물 보냄")
                .userCmLogCreateTime(LocalDateTime.now())
                .build();
            userCmLogRepo.save(sendUserLog);
            
            // 선물 입금 로그 저장 (받는 사람)
            UserCmLog receiveUserLog = UserCmLog.builder()
                .userCmLogPaymentIndex(1) // 입금
                .userCmLogTransactionTypeIndex(10) // 선물
                .userCmLogValueTypeIndex(2) // CM
                .userIndexEventTrigger(sendUserTesseris) // 보내는 사람
                .userIndexEventParty(receiveUserTesseris) // 받는 사람
                .userCmLogValue(giftAmount) // 양수로 저장
                .userCmLogReason("선물 받음")
                .userCmLogCreateTime(LocalDateTime.now())
                .build();
            userCmLogRepo.save(receiveUserLog);
            
            return ResponseDTO.createSuccessResponse("선물 전송 성공", GiftTransferDTO.builder()
                .sendUserIndex(sendUserCm.getUserCmIndex())
                .receiveUserIndex(receiveUserCm.getUserCmIndex())
                .giftAmount(giftAmount)
                .build()
            );
            
        } catch (Exception e) {
            log.error("선물 전송 중 오류 발생", e);
            return ResponseDTO.createErrorResponse(400, "선물 전송 중 오류가 발생했습니다");
        }
    }
}
