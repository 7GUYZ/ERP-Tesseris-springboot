package com.jakdang.labs.api.taekjun.user_update.service;

import com.jakdang.labs.api.taekjun.user_update.dto.UserInfoDto;
import com.jakdang.labs.api.taekjun.user_update.dto.UserUpdateRequestDto;
import com.jakdang.labs.api.taekjun.user_update.repository.UserUpdateJtjRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserUpdateService {

    private final UserUpdateJtjRepo userUpdateRepository;

    /**
     * 사용자 정보를 조회합니다.
     */
    @Transactional(readOnly = true)
    public UserInfoDto getUserInfo(Long userIndex) {
        log.info("사용자 정보 조회 - userIndex: {}", userIndex);
        return userUpdateRepository.findUserInfoByUserIndex(userIndex);
    }

    /**
     * 사용자 정보를 수정합니다.
     */
    @Transactional
    public boolean updateUserInfo(Long userIndex, UserUpdateRequestDto requestDto) {
        log.info("사용자 정보 수정 - userIndex: {}, requestDto: {}", userIndex, requestDto);
        
        try {
            int updatedRows = userUpdateRepository.updateUserInfo(userIndex, requestDto);
            boolean success = updatedRows > 0;
            log.info("사용자 정보 수정 결과 - userIndex: {}, updatedRows: {}, success: {}", userIndex, updatedRows, success);
            return success;
        } catch (Exception e) {
            log.error("사용자 정보 수정 중 오류: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 은행 목록을 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getBankList() {
        log.info("은행 목록 조회");
        return userUpdateRepository.getBankList();
    }
} 