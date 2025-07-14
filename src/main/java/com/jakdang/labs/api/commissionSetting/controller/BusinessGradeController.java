package com.jakdang.labs.api.commissionSetting.controller;
import org.springframework.web.bind.annotation.*;
import com.jakdang.labs.api.commissionSetting.service.BusinessGradeService;
import com.jakdang.labs.api.commissionSetting.entity.BusinessGrade;
import lombok.RequiredArgsConstructor;
import java.util.List;

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
  public List<BusinessGrade> updateAllBusinessGrades(@RequestBody List<BusinessGrade> updatedGrades) {
    return businessGradeService.updateAllBusinessGrades(updatedGrades);
  }
}
