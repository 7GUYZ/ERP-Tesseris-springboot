package com.jakdang.labs.api.deokkyu.businessman.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jakdang.labs.api.deokkyu.businessman.dto.BusinessmanListDto;
import com.jakdang.labs.api.deokkyu.businessman.dto.BusinessmanSearchDto;
import com.jakdang.labs.api.deokkyu.businessman.dto.OrgChartDto;
import com.jakdang.labs.api.deokkyu.businessman.service.BusinessmanService;

@RestController
@RequestMapping("/api/businessman")
public class BusinessmanController {
    
    private final  BusinessmanService businessmanService;

    public BusinessmanController(BusinessmanService businessmanService) {
        this.businessmanService = businessmanService;
    }
    
    @GetMapping("/allowance") // 
    public ResponseEntity<List<BusinessmanListDto>> getFilteredAllowance(BusinessmanSearchDto filter) {
        List<BusinessmanListDto> allowanceList = businessmanService.getAllowanceListDtos(filter);
        return ResponseEntity.ok(allowanceList);
    }

    @GetMapping("/orgchart") // 조직도 조회
    public ResponseEntity<List<OrgChartDto>> getOrgChart() {
        List<OrgChartDto> orgChartList = businessmanService.getOrgChartListDtos();
        return ResponseEntity.ok(orgChartList);
    }
}
