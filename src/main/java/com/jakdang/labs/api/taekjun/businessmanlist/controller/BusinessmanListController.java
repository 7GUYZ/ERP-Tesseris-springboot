package com.jakdang.labs.api.taekjun.businessmanlist.controller;

import com.jakdang.labs.api.taekjun.businessmanlist.dto.BusinessmanListResponseDTO;
import com.jakdang.labs.api.taekjun.businessmanlist.dto.BusinessmanListSearchDTO;
import com.jakdang.labs.api.taekjun.businessmanlist.dto.BusinessmanCreateRequestDTO;
import com.jakdang.labs.api.taekjun.businessmanlist.dto.BusinessmanUpdateRequestDTO;
import com.jakdang.labs.api.taekjun.businessmanlist.service.BusinessmanListService;
import com.jakdang.labs.api.jihun.common.config.ExcelDownloadConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/businessmanlist")
@RequiredArgsConstructor
public class BusinessmanListController {
    private final BusinessmanListService businessmanListService;
    private final ExcelDownloadConfig.ExcelDownloadProperties excelDownloadProperties;

    @GetMapping
    public ResponseEntity<List<BusinessmanListResponseDTO>> getBusinessmanList() {
        List<BusinessmanListResponseDTO> list = businessmanListService.getBusinessmanList();
        return ResponseEntity.ok(list);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<BusinessmanListResponseDTO>> getAllActiveBusinessmen() {
        List<BusinessmanListResponseDTO> list = businessmanListService.getAllActiveBusinessmen();
        return ResponseEntity.ok(list);
    }
    
    @GetMapping("/all-businessmen")
    public ResponseEntity<List<BusinessmanListResponseDTO>> getAllBusinessmen() {
        List<BusinessmanListResponseDTO> list = businessmanListService.getAllBusinessmen();
        return ResponseEntity.ok(list);
    }
    
    @PostMapping("/search")
    public ResponseEntity<List<BusinessmanListResponseDTO>> searchBusinessmanList(@RequestBody BusinessmanListSearchDTO searchDTO) {
        List<BusinessmanListResponseDTO> list = businessmanListService.searchBusinessmanList(searchDTO);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/business-grades")
    public ResponseEntity<List<BusinessGradeDTO>> getBusinessGrades() {
        List<BusinessGradeDTO> grades = businessmanListService.getBusinessGrades();
        return ResponseEntity.ok(grades);
    }
    
    @GetMapping("/business-areas")
    public ResponseEntity<List<BusinessAreaDTO>> getBusinessAreas() {
        List<BusinessAreaDTO> areas = businessmanListService.getBusinessAreas();
        return ResponseEntity.ok(areas);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBusinessman(@RequestBody BusinessmanCreateRequestDTO dto) {
        return businessmanListService.createBusinessman(dto);
    }
    
    @PutMapping("/update")
    public ResponseEntity<?> updateBusinessman(@RequestBody BusinessmanUpdateRequestDTO dto) {
        return businessmanListService.updateBusinessman(dto);
    }
    
    @PostMapping("/download")
    public ResponseEntity<byte[]> downloadBusinessmanList(@RequestBody BusinessmanListSearchDTO searchDTO) {
        try {
            byte[] csvData = businessmanListService.generateCsvFile(searchDTO);
            
            // 파일명 생성 (현재 날짜 포함)
            String fileName = "사업자목록_" + java.time.LocalDate.now().toString().replace("-", "") + ".csv";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentLength(csvData.length);
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(csvData);
                
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteBusinessman(@RequestBody Map<String, Integer> request) {
        Integer userIndex = request.get("userIndex");
        if (userIndex == null) {
            return ResponseEntity.badRequest().body("userIndex는 필수입니다.");
        }
        return businessmanListService.deactivateBusinessman(userIndex);
    }
    
    // 디버깅용 엔드포인트

    
    // BusinessGradeDTO 내부 클래스
    public static class BusinessGradeDTO {
        private Integer businessGradeIndex;
        private Integer businessGradeLevel;
        private String businessGradeName;
        private Double businessGradeRate;
        
        // 생성자
        public BusinessGradeDTO() {}
        
        public BusinessGradeDTO(Integer businessGradeIndex, Integer businessGradeLevel, String businessGradeName, Double businessGradeRate) {
            this.businessGradeIndex = businessGradeIndex;
            this.businessGradeLevel = businessGradeLevel;
            this.businessGradeName = businessGradeName;
            this.businessGradeRate = businessGradeRate;
        }
        
        // Getter/Setter
        public Integer getBusinessGradeIndex() { return businessGradeIndex; }
        public void setBusinessGradeIndex(Integer businessGradeIndex) { this.businessGradeIndex = businessGradeIndex; }
        
        public Integer getBusinessGradeLevel() { return businessGradeLevel; }
        public void setBusinessGradeLevel(Integer businessGradeLevel) { this.businessGradeLevel = businessGradeLevel; }
        
        public String getBusinessGradeName() { return businessGradeName; }
        public void setBusinessGradeName(String businessGradeName) { this.businessGradeName = businessGradeName; }
        
        public Double getBusinessGradeRate() { return businessGradeRate; }
        public void setBusinessGradeRate(Double businessGradeRate) { this.businessGradeRate = businessGradeRate; }
    }
    
    // BusinessAreaDTO 내부 클래스
    public static class BusinessAreaDTO {
        private Integer businessAreaIndex;
        private String businessAreaName;
        private Integer businessAreaLevel;
        
        // 생성자
        public BusinessAreaDTO() {}
        
        public BusinessAreaDTO(Integer businessAreaIndex, String businessAreaName, Integer businessAreaLevel) {
            this.businessAreaIndex = businessAreaIndex;
            this.businessAreaName = businessAreaName;
            this.businessAreaLevel = businessAreaLevel;
        }
        
        // Getter/Setter
        public Integer getBusinessAreaIndex() { return businessAreaIndex; }
        public void setBusinessAreaIndex(Integer businessAreaIndex) { this.businessAreaIndex = businessAreaIndex; }
        
        public String getBusinessAreaName() { return businessAreaName; }
        public void setBusinessAreaName(String businessAreaName) { this.businessAreaName = businessAreaName; }
        
        public Integer getBusinessAreaLevel() { return businessAreaLevel; }
        public void setBusinessAreaLevel(Integer businessAreaLevel) { this.businessAreaLevel = businessAreaLevel; }
    }
} 