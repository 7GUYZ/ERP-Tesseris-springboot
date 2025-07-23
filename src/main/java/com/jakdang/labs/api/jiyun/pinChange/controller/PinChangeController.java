package com.jakdang.labs.api.jiyun.pinChange.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jakdang.labs.api.jiyun.mypage.repository.MypageGeneralRepository;
import com.jakdang.labs.api.jiyun.pinChange.dto.PinChangeDTO;
import com.jakdang.labs.api.jiyun.pinChange.service.PinChangeService;
import com.jakdang.labs.security.jwt.utils.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pinChange")
public class PinChangeController {
  private final PinChangeService pinChangeService;
  private final JwtUtil jwtUtil;
  private final MypageGeneralRepository mypageGeneralRepository;

  @PostMapping("/update")
  public ResponseEntity<?> postMethodName(@RequestHeader("Authorization") String authHeader, @RequestBody PinChangeDTO.Response response) {
    String token = authHeader.replace("Bearer ", "");
    String id = jwtUtil.getUserId(token); 
    // user_tesseris에서 userIndex 조회
    Integer userIdx = mypageGeneralRepository.findByUsersId_Id(id)
        .map(u -> u.getUserIndex())
        .orElseThrow(() -> new IllegalArgumentException("UserTesseris not found for id: " + id));
    boolean result = pinChangeService.updatePin(response, userIdx);
    if(result){
      return ResponseEntity.ok("핀번호 변경 성공");
    }else{
      return ResponseEntity.ok("핀번호 등록 실패");
    }
  }

    @PostMapping("/pwCheck")
    public ResponseEntity<?> verifyPassword(
        @RequestHeader("Authorization") String authHeader,
        @RequestBody PinChangeDTO.PasswordVerifyRequest request
    ) {
        boolean result = pinChangeService.verifyPassword(authHeader, request.getPassword());
        if (result) {
            return ResponseEntity.ok().body(Map.of("success", true, "message", "비밀번호 일치"));
        } else {
            return ResponseEntity.ok().body(Map.of("success", false, "message", "비밀번호 불일치"));
        }
    }
} 