package com.jakdang.labs.api.jungeun.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.jungeun.dto.SettingDTO;
import com.jakdang.labs.api.jungeun.service.SettingSvc;
import com.jakdang.labs.entity.UserTesseris;
import com.jakdang.labs.security.jwt.utils.JwtUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/cmLimit")
public class CmLimitController {

    private final SettingSvc settingSvc;
    private final JwtUtil jwtUtil;
    @PersistenceContext
    private EntityManager em;

    @GetMapping
    public ResponseEntity<ResponseDTO<SettingDTO>> getCmLimit(){
        return ResponseEntity.ok().body(settingSvc.getCmLimit());
    }

    @PostMapping("/save")
    public ResponseEntity<ResponseDTO<SettingDTO>> saveCmLimit(@RequestBody SettingDTO settingUpdateDTO, @RequestHeader("Authorization") String authHeader){
        // 1. DB 저장 (트랜잭션 내에서)
        ResponseDTO<SettingDTO> response = settingSvc.saveCmLimit(settingUpdateDTO);
        
        // 2. DB 저장 성공 후 알림 전송 (트랜잭션 커밋 후)
        try {
            String token = authHeader.replace("Bearer ", "");
            String userId = jwtUtil.getUserId(token);
            // userId로 UserTesseris의 userIndex 조회
            UserTesseris user = em.createQuery("SELECT u FROM UserTesseris u WHERE u.usersId.id = :id", UserTesseris.class)
                    .setParameter("id", userId)
                    .getSingleResult();
            Integer cmLimit = Integer.valueOf(settingUpdateDTO.getSettingValue());
            settingSvc.sendCmLimitChangedAlarm(user.getUserIndex(), cmLimit);
        } catch (Exception e) {
            log.error("월 CM 한도 변경 알림 전송 실패: {}", e.getMessage());
            // 알림 전송 실패해도 DB 저장은 성공으로 처리
        }
        
        return ResponseEntity.ok().body(response);
    }

}
