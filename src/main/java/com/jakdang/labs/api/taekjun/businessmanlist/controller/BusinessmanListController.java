package com.jakdang.labs.api.taekjun.businessmanlist.controller;

import com.jakdang.labs.api.taekjun.businessmanlist.dto.BusinessmanListResponseDTO;
import com.jakdang.labs.api.taekjun.businessmanlist.dto.BusinessmanListSearchDTO;
import com.jakdang.labs.api.taekjun.businessmanlist.dto.BusinessmanCreateRequestDTO;
import com.jakdang.labs.api.taekjun.businessmanlist.service.BusinessmanListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/businessmanlist")
@RequiredArgsConstructor
public class BusinessmanListController {
    private final BusinessmanListService businessmanListService;

    @PostMapping("/search")
    public ResponseEntity<List<BusinessmanListResponseDTO>> searchBusinessmanList(@RequestBody BusinessmanListSearchDTO searchDTO) {
        List<BusinessmanListResponseDTO> list = businessmanListService.searchBusinessmanList(searchDTO);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createBusinessman(@RequestBody BusinessmanCreateRequestDTO dto) {
        return businessmanListService.createBusinessman(dto);
    }
} 