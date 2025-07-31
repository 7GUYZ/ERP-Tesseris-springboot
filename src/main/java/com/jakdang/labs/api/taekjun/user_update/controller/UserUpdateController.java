package com.jakdang.labs.api.taekjun.user_update.controller;

import com.jakdang.labs.api.taekjun.user_update.service.UserUpdateService;
import com.jakdang.labs.api.taekjun.user_update.dto.UserInfoDto;
import com.jakdang.labs.api.taekjun.user_update.dto.UserUpdateRequestDto;
import com.jakdang.labs.api.common.ResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserUpdateController {

    private final UserUpdateService userUpdateService;

    /**
     * 사용자 정보 조회
     */
    @GetMapping("/info")
    public ResponseEntity<ResponseDTO<UserInfoDto>> getUserInfo(@RequestParam(required = false) Long userIndex) {
        log.info("사용자 정보 조회 요청 - userIndex: {}, userIndex type: {}", userIndex, userIndex != null ? userIndex.getClass().getSimpleName() : "null");
        
        if (userIndex == null) {
            log.error("userIndex 파라미터가 누락되었습니다.");
            return ResponseEntity.badRequest().body(ResponseDTO.<UserInfoDto>builder()
                .resultCode(400)
                .resultMessage("userIndex 파라미터가 필요합니다. 요청 URL에 ?userIndex=값 형태로 추가해주세요.")
                .build());
        }
        
        log.info("사용자 정보 조회 요청 - userIndex: {}", userIndex);
        
        try {
            UserInfoDto userInfo = userUpdateService.getUserInfo(userIndex);
            if (userInfo != null) {
                return ResponseEntity.ok(ResponseDTO.<UserInfoDto>createSuccessResponse("사용자 정보 조회 성공", userInfo));
            } else {
                return ResponseEntity.badRequest().body(ResponseDTO.<UserInfoDto>builder()
                    .resultCode(400)
                    .resultMessage("사용자 정보를 찾을 수 없습니다.")
                    .build());
            }
        } catch (Exception e) {
            log.error("사용자 정보 조회 중 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ResponseDTO.<UserInfoDto>builder()
                .resultCode(500)
                .resultMessage("사용자 정보 조회 중 오류가 발생했습니다.")
                .build());
        }
    }

    /**
     * 사용자 정보 수정 (계좌번호 포함)
     */
    @PutMapping("/update")
    public ResponseEntity<ResponseDTO<String>> updateUserInfo(@RequestParam(required = false) Long userIndex, @RequestBody UserUpdateRequestDto requestDto) {
        if (userIndex == null) {
            log.error("userIndex 파라미터가 누락되었습니다.");
            return ResponseEntity.badRequest().body(ResponseDTO.<String>builder()
                .resultCode(400)
                .resultMessage("userIndex 파라미터가 필요합니다. 요청 URL에 ?userIndex=값 형태로 추가해주세요.")
                .build());
        }
        
        log.info("사용자 정보 수정 요청 - userIndex: {}, requestDto: {}", userIndex, requestDto);
        
        try {
            boolean success = userUpdateService.updateUserInfo(userIndex, requestDto);
            if (success) {
                return ResponseEntity.ok(ResponseDTO.<String>createSuccessResponse("사용자 정보 수정 성공", "수정이 완료되었습니다."));
            } else {
                return ResponseEntity.badRequest().body(ResponseDTO.<String>builder()
                    .resultCode(400)
                    .resultMessage("사용자 정보 수정에 실패했습니다.")
                    .build());
            }
        } catch (Exception e) {
            log.error("사용자 정보 수정 중 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ResponseDTO.<String>builder()
                .resultCode(500)
                .resultMessage("사용자 정보 수정 중 오류가 발생했습니다.")
                .build());
        }
    }

    /**
     * 은행 목록 조회
     */
    @GetMapping("/banks")
    public ResponseEntity<ResponseDTO<Object>> getBankList() {
        log.info("은행 목록 조회 요청");
        
        try {
            Object bankList = userUpdateService.getBankList();
            return ResponseEntity.ok(ResponseDTO.<Object>createSuccessResponse("은행 목록 조회 성공", bankList));
        } catch (Exception e) {
            log.error("은행 목록 조회 중 오류: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ResponseDTO.<Object>builder()
                .resultCode(500)
                .resultMessage("은행 목록 조회 중 오류가 발생했습니다.")
                .build());
        }
    }
} 