package com.jakdang.labs.api.jungeun.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jakdang.labs.api.auth.dto.UserLoginRequest;
import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.jungeun.service.PwCheckSvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/passwordCheck")
public class PwCheckController {

    private final PwCheckSvc pwCheckSvc;

    @PostMapping
    public ResponseEntity<ResponseDTO<Void>> pwCheck(@RequestBody UserLoginRequest dto){
        ResponseDTO<Void> result = pwCheckSvc.pwCheck(dto.getUsername(), dto.getPassword());
        return ResponseEntity.ok().body(result);    }

}
