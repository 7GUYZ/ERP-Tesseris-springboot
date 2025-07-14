package com.jakdang.labs.api.commissionSetting.service;

import org.springframework.stereotype.Service;

import com.jakdang.labs.api.commissionSetting.repository.BusinessGradeRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import com.jakdang.labs.api.commissionSetting.entity.BusinessGrade;

@Service
@RequiredArgsConstructor
public class BusinessGradeService {
  private final BusinessGradeRepository businessGradeRepository;

  // 비즈니스 등급 전체 조회
  public List<BusinessGrade> getAllBusinessGrades() {
    return businessGradeRepository.findAll();
  }

  // 비즈니스 등급 전체 일괄 수정
  @Transactional
  public List<BusinessGrade> updateAllBusinessGrades(List<BusinessGrade> updatedGrades) {
    // 기존 엔티티를 Map으로 조회
    Map<Integer, BusinessGrade> existingMap = businessGradeRepository.findAll().stream()
      .collect(Collectors.toMap(BusinessGrade::getBusinessGradeIndex, bg -> bg));
    for (BusinessGrade updated : updatedGrades) {
      BusinessGrade existing = existingMap.get(updated.getBusinessGradeIndex());
      if (existing != null) {
        existing.setBusinessGradeName(updated.getBusinessGradeName());
        existing.setBusinessGradeRate(updated.getBusinessGradeRate());
        existing.setBusinessGradeLevel(updated.getBusinessGradeLevel());
      }
    }
    return businessGradeRepository.saveAll(existingMap.values());
  }
}
