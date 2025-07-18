package com.jakdang.labs.api.jiyun.commissionSetting.controller;

import org.springframework.web.bind.annotation.*;

import com.jakdang.labs.api.jiyun.commissionSetting.service.BusinessGradeService;
import com.jakdang.labs.api.jiyun.dto.PasswordVerifyRequest;
import com.jakdang.labs.entity.BusinessGrade;
import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/commission-setting")
public class BusinessGradeController {
  private final BusinessGradeService businessGradeService;

  // 비즈니스 등급 전체 조회
  @GetMapping("/business-grades")
  public List<BusinessGrade> getAllBusinessGrades() {
    return businessGradeService.getAllBusinessGrades();
  }

  // 비즈니스 등급 전체 일괄 수정
  @PostMapping("/business-grades-update")
  public List<BusinessGrade> updateAllBusinessGrades(@RequestBody List<BusinessGrade> updatedGrades, @RequestHeader("Authorization") String authHeader) {
    return businessGradeService.updateAllBusinessGrades(updatedGrades, authHeader);
  }

  // 비밀번호 확인 엔드포인트
  @PostMapping("/pwCheck")
  public ResponseEntity<?> verifyPassword(
      @RequestHeader("Authorization") String authHeader,
      @RequestBody PasswordVerifyRequest request
  ) {
      boolean result = businessGradeService.verifyPassword(request, authHeader);
      if (result) {
          return ResponseEntity.ok().body(Map.of("success", true, "message", "비밀번호 일치"));
      } else {
          return ResponseEntity.ok().body(Map.of("success", false, "message", "비밀번호 불일치"));
      }
  }
} 