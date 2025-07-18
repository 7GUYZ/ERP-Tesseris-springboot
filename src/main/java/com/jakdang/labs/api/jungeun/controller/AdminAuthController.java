package com.jakdang.labs.api.jungeun.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.jungeun.dto.NaviAuthorityDTO;
import com.jakdang.labs.api.jungeun.service.AdminAuthSvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/adminAuthority")
public class AdminAuthController {
    private final AdminAuthSvc authSvc;

    @GetMapping
    public ResponseEntity<ResponseDTO<List<NaviAuthorityDTO>>> getAuthority(@RequestParam("adminTypeIndex") String adminTypeIndex){
        return ResponseEntity.ok().body(authSvc.getAuthority(Integer.valueOf(adminTypeIndex)));
    }

}