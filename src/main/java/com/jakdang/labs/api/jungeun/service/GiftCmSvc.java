package com.jakdang.labs.api.jungeun.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.api.auth.repository.UserRepository;
import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.jungeun.dto.CurrentCmDTO;
import com.jakdang.labs.api.jungeun.dto.GiftSearchUserDTO;
import com.jakdang.labs.api.jungeun.repository.UserCmLjeRepo;
import com.jakdang.labs.entity.UserCm;
import com.jakdang.labs.exceptions.handler.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GiftCmSvc {
    private final UserCmLjeRepo userCmRepo;

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
}
