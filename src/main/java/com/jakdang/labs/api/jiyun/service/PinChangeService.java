package com.jakdang.labs.api.jiyun.service;

import org.springframework.stereotype.Service;

import com.jakdang.labs.api.jiyun.dto.PinChangeDTO;
import com.jakdang.labs.api.entity.UserCm;
import com.jakdang.labs.api.jiyun.repository.PinChangekjyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PinChangeService {
  private final PinChangekjyRepository pinChangeRepository;

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
} 