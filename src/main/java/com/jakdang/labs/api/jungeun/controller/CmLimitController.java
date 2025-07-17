package com.jakdang.labs.api.jungeun.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.jungeun.dto.SettingDTO;
import com.jakdang.labs.api.jungeun.service.SettingSvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/cmLimit")
public class CmLimitController {

    private final SettingSvc settingSvc;

    @GetMapping
    public ResponseEntity<ResponseDTO<SettingDTO>> getCmLimit(){
        return ResponseEntity.ok().body(settingSvc.getCmLimit());
    }

    @PostMapping("/save")
    public ResponseEntity<ResponseDTO<SettingDTO>> saveCmLimit(@RequestBody SettingDTO settingUpdateDTO){
        return ResponseEntity.ok().body(settingSvc.saveCmLimit(settingUpdateDTO));
    }

}
