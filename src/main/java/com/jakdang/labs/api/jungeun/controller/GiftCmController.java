package com.jakdang.labs.api.jungeun.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jakdang.labs.api.common.ResponseDTO;
import com.jakdang.labs.api.jungeun.dto.CurrentCmDTO;
import com.jakdang.labs.api.jungeun.dto.GiftPinCheckDTO;
import com.jakdang.labs.api.jungeun.dto.GiftTransferDTO;
import com.jakdang.labs.api.jungeun.service.GiftCmSvc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/user/giftCM")
public class GiftCmController {
    private final GiftCmSvc giftSvc;

    @GetMapping("/currenCM")
    public ResponseEntity<ResponseDTO<CurrentCmDTO>> getCurrentCM(@RequestParam("user_index") Integer user_index){
        return ResponseEntity.ok().body(giftSvc.getCurrentCM(user_index));
    }

    @GetMapping("/searchUser")
    public ResponseEntity<ResponseDTO<?>> searchUser(@RequestParam("recipientEmail") String recipientEmail){
        return ResponseEntity.ok().body(giftSvc.searchUser(recipientEmail));
    }

    @PostMapping("/pinCheck")
    public ResponseEntity<ResponseDTO<?>> pinCheck(@RequestBody GiftPinCheckDTO giftPinCheckDTO){
        return ResponseEntity.ok().body(giftSvc.pinCheck(giftPinCheckDTO));
    }

    @PostMapping("/giftTransfer")
    public ResponseEntity<ResponseDTO<?>> giftTransfer(@RequestBody GiftTransferDTO giftTransferDTO){
        return ResponseEntity.ok().body(giftSvc.giftTransfer(giftTransferDTO));
    }
}
