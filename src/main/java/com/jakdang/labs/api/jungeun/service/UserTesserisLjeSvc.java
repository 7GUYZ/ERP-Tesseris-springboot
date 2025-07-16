package com.jakdang.labs.api.jungeun.service;

import com.jakdang.labs.api.jungeun.dto.LoginUserTesserisDTO;
import com.jakdang.labs.api.jungeun.repository.UserTesserisLjeRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * 사용자 관리 서비스 클래스
 * 사용자 조회, 수정, 프로필 관리 등의 비즈니스 로직을 처리
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserTesserisLjeSvc {
    private final UserTesserisLjeRepo userTesserisLjeRepo;

    /**
     * 사용자 엔티티 조회
     * 사용자 ID로 UserTesserisEntity 객체를 조회
     */
    public LoginUserTesserisDTO findByUsersId(String usersId) {
        return userTesserisLjeRepo.findByUsersId(usersId)
            .map(user -> LoginUserTesserisDTO.builder()
                .userIndex(user.getUserIndex())
                .userRoleIndex(user.getUserRoleIndex())
                .build())
            .orElse(null);
    }

}


