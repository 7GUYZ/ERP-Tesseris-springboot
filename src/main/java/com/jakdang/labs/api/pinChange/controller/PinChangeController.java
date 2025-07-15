package com.jakdang.labs.api.pinChange.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jakdang.labs.api.pinChange.dto.PinChangeDTO;
import com.jakdang.labs.api.pinChange.service.PinChangeService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pinChange")
public class PinChangeController {
  private final PinChangeService pinChangeService;
  
  @PostMapping("/update")
  public ResponseEntity<?> postMethodName(@RequestBody PinChangeDTO.Response response) {
    int userIdx=872;
    boolean result = pinChangeService.updatePin(response, userIdx);
    if(result){
      return ResponseEntity.ok("핀번호 변경 성공");
    }else{
      return ResponseEntity.ok("공지사항 등록 성공");
    }
  }
  
}
