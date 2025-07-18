package com.jakdang.labs.api.jiyun.pinChange.service;

import org.springframework.stereotype.Service;

import com.jakdang.labs.entity.UserCm;
import com.jakdang.labs.api.jiyun.pinChange.dto.PinChangeDTO;
import com.jakdang.labs.api.jiyun.pinChange.repository.PinChangekjyRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.api.auth.repository.AuthRepository;
import com.jakdang.labs.security.jwt.utils.JwtUtil;

@Service
@RequiredArgsConstructor
public class PinChangeService {
  private final PinChangekjyRepository pinChangeRepository;

  @Autowired
  private JwtUtil jwtUtil;
  @Autowired
  private AuthRepository authRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;

  public boolean updatePin(PinChangeDTO.Response response, Integer userIdx){
    try{
      UserCm userCm = new UserCm();
      userCm.setUserCmIndex(userIdx);
      userCm.setUserCmPincode(response.getUserCmPincode());
      pinChangeRepository.save(userCm);
      return true;
    }catch(Exception e){
      return false;
    }
  }

  public boolean verifyPassword(String authHeader, String password) {
      String token = authHeader.replace("Bearer ", "");
      String userEmail = jwtUtil.getUserEmail(token); // 이메일 추출
      UserEntity user = authRepository.findByEmail(userEmail)
          .orElseThrow(() -> new RuntimeException("사용자 없음"));
      return passwordEncoder.matches(password, user.getPassword());
  }
} 