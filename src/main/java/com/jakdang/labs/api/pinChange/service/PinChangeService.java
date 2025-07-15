package com.jakdang.labs.api.pinChange.service;

import org.springframework.stereotype.Service;

import com.jakdang.labs.api.pinChange.dto.PinChangeDTO;
import com.jakdang.labs.api.pinChange.entity.UserCm;
import com.jakdang.labs.api.pinChange.repository.PinChangeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PinChangeService {
  private final PinChangeRepository pinChangeRepository;

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
