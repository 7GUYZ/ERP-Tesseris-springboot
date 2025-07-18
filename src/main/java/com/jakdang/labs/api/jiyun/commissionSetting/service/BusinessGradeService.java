package com.jakdang.labs.api.jiyun.commissionSetting.service;

import org.springframework.stereotype.Service;

import com.jakdang.labs.api.jiyun.commissionSetting.repository.BusinessGradekjyRepository;
import com.jakdang.labs.api.jiyun.dto.PasswordVerifyRequest;
import com.jakdang.labs.api.jiyun.updateLog.repository.UpdateUserLogkjyRepository;
import com.jakdang.labs.entity.UpdateUserLog;
import com.jakdang.labs.api.jungeun.repository.UserTesserisLjeRepo;
import com.jakdang.labs.entity.UserTesseris;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import com.jakdang.labs.entity.BusinessGrade;
import com.jakdang.labs.api.auth.repository.AuthRepository;
import com.jakdang.labs.api.auth.entity.UserEntity;
import com.jakdang.labs.security.jwt.utils.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BusinessGradeService {
  private final BusinessGradekjyRepository businessGradeRepository;
  private final AuthRepository authRepository;
  private final JwtUtil jwtUtil;
  private final PasswordEncoder passwordEncoder;
  private final UpdateUserLogkjyRepository updateUserLogRepository;
  private final UserTesserisLjeRepo userTesserisRepository;

  // 비즈니스 등급 전체 조회
  public List<BusinessGrade> getAllBusinessGrades() {
    return businessGradeRepository.findAll();
  }

  // 비즈니스 등급 전체 일괄 수정
  @Transactional
  public List<BusinessGrade> updateAllBusinessGrades(List<BusinessGrade> updatedGrades, String authHeader) {
    // 수정 전 데이터 저장
    List<BusinessGrade> beforeGrades = businessGradeRepository.findAll();
    String beforeData = formatBusinessGradesForLog(beforeGrades);
    
    // 기존 엔티티를 Map으로 조회
    Map<Integer, BusinessGrade> existingMap = beforeGrades.stream()
      .collect(Collectors.toMap(BusinessGrade::getBusinessGradeIndex, bg -> bg));
    for (BusinessGrade updated : updatedGrades) {
      BusinessGrade existing = existingMap.get(updated.getBusinessGradeIndex());
      if (existing != null) {
        existing.setBusinessGradeName(updated.getBusinessGradeName());
        existing.setBusinessGradeRate(updated.getBusinessGradeRate());
        existing.setBusinessGradeLevel(updated.getBusinessGradeLevel());
      }
    }
    
    // 수정 후 데이터 저장
    List<BusinessGrade> savedGrades = businessGradeRepository.saveAll(existingMap.values());
    String afterData = formatBusinessGradesForLog(savedGrades);
    
    // 변경 이력 로그 기록
    saveUpdateLog(authHeader, beforeData, afterData);
    
    return savedGrades;
  }

  // 비밀번호 검증
  public boolean verifyPassword(PasswordVerifyRequest request, String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer ")) return false;
    String token = authHeader.substring(7);
    String email = jwtUtil.getUserEmail(token);
    UserEntity user = authRepository.findByEmail(email).orElse(null);
    if (user == null) return false;
    return passwordEncoder.matches(request.getPassword(), user.getPassword());
  }

  // 비즈니스 등급 데이터를 로그 형식으로 포맷팅
  private String formatBusinessGradesForLog(List<BusinessGrade> grades) {
    return grades.stream()
      .sorted((a, b) -> Integer.compare(a.getBusinessGradeIndex(), b.getBusinessGradeIndex()))
      .map(grade -> String.format("(등급:%s 수수료율:%.1f)", 
          grade.getBusinessGradeName(), 
          grade.getBusinessGradeRate()))
      .collect(Collectors.joining(","));
  }

  // 변경 이력 로그 저장
  private void saveUpdateLog(String authHeader, String beforeData, String afterData) {
    try {
      // JWT에서 사용자 정보 추출
      String token = authHeader.substring(7);
      String email = jwtUtil.getUserEmail(token);
      String userId = jwtUtil.getUserId(token);
      
      // UserTesseris에서 user_index 조회
      Optional<UserTesseris> userTesserisOpt = userTesserisRepository.findByUsersId(userId);
      if (userTesserisOpt.isEmpty()) return;
      
      UserTesseris userTesseris = userTesserisOpt.get();
      Integer updateUserIndex = userTesseris.getUserIndex();
      
      // UpdateUserLog 엔티티 생성 및 저장
      UpdateUserLog updateLog = new UpdateUserLog();
      updateLog.setUpdateUserIndex(updateUserIndex);
      updateLog.setInflictUserIndex(updateUserIndex); // 자기 자신을 수정 대상으로 설정
      updateLog.setUpdateBeforeData(beforeData);
      updateLog.setUpdateAfterData(afterData);
      updateLog.setUpdateUserLogUpdateTime(LocalDateTime.now());
      updateLog.setUpdateDataValue("프로그램명: 중개수수료율 설정, 기능: 수정");
      
      updateUserLogRepository.save(updateLog);
    } catch (Exception e) {
      // 로그 저장 실패 시에도 메인 로직은 계속 진행
      System.err.println("UpdateUserLog 저장 실패: " + e.getMessage());
    }
  }
} 