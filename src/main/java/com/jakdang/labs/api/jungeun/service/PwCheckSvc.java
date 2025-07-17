package com.jakdang.labs.api.jungeun.service;

import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.api.auth.repository.UserRepository;
import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.exceptions.handler.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PwCheckSvc {
    private final UserRepository userRepo;
    private final Argon2PasswordEncoder encoder;

    public ResponseDTO<Void> pwCheck(String email, String password) {
        UserEntity user = userRepo.findByEmail(email)
            .orElseThrow(() -> new CustomException("유저를 찾을 수 없습니다.", -200));
    
        // (1) 비밀번호 일치 확인 - 예: 평문 비교 (암호화 안되어있다고 가정)
        boolean matches = encoder.matches(password, user.getPassword());
        if (!matches) {
            throw new CustomException("비밀번호가 일치하지 않습니다.", -201);
        }
    
        // (2) 성공 응답 반환 (굳이 비밀번호를 클라이언트에 넘기진 않음)
        return ResponseDTO.createSuccessResponse("비밀번호 일치", null);
    }

}


