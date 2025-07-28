package com.jakdang.labs.api.jungeun.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.jungeun.service.BrokerageFeeSvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/user/brokerageFee")
public class BrokerageFeeController {
    private final BrokerageFeeSvc brokerageFeeSvc;

    // @GetMapping
    // private ResponseDTO<?> getBrokerageFee(@RequestParam("user_index") Integer user_index){
    //     return brokerageFeeSvc.getBrokerageFee(user_index);
    // }
}
