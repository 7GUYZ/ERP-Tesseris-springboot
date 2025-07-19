package com.jakdang.labs.api.jungeun.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.jungeun.dto.UserBusinessFilteredListDTO;
import com.jakdang.labs.api.jungeun.dto.UserBusinessListDTO;
import com.jakdang.labs.api.jungeun.service.UserBusinessListSvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/user/businessList")
public class UserBusinessListController {

    private final UserBusinessListSvc businessSvc;

    @GetMapping
    public ResponseEntity<ResponseDTO<List<UserBusinessListDTO>>> getAvailableGrades(@RequestParam("user_index") Integer user_index){
        return ResponseEntity.ok().body(businessSvc.getAvailableGrades(user_index));
    }

    @GetMapping("/filtered")
    public ResponseEntity<ResponseDTO<List<UserBusinessFilteredListDTO>>> getFilteredBusinessList(@RequestParam("business_grade_index") Integer business_grade_index){
        return ResponseEntity.ok().body(businessSvc.getFilteredBusinessList(business_grade_index));
    }
}
